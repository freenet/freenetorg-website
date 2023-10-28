@file:UseSerializers(InstantSerializer::class)

package org.freenet.website.pages.blog

import InstantSerializer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.coroutines.time.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import mu.two.KotlinLogging
import java.time.Duration
import java.time.Instant

private val logger = KotlinLogging.logger { }

object GitHubDiscussions {

    private val scope = CoroutineScope(Dispatchers.IO)

    @Volatile var discussions: DiscussionStore? = null

    data class DiscussionStore(val discussions : List<Discussion>) {
        val discussionsByNumber = discussions.associateBy { it.number }
    }

    init {
        scope.launch(Dispatchers.IO) {
            while(true) {
                logger.info { "Retrieving discussions from GitHub" }
                discussions = DiscussionStore(getDiscussions())
                logger.info { "Github discussions retrieved" }
                delay(Duration.ofHours(1))
            }
        }
    }

    suspend fun getDiscussions(): List<Discussion> {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                })
            }
        }

        val token: String = System.getenv("GITHUB_TOKEN") ?: error("GITHUB_TOKEN not set")
        var cursor: String? = null
        val allDiscussions = mutableListOf<Discussion>()

        do {
            val query = """
                query {
                    repository(owner: "freenet", name: "freenet-core") {
                        discussions( first: 10, after: ${cursor?.let { "\"$it\"" } ?: "null"}, categoryId: "DIC_kwDOFwu4Lc4CaWeE") {
                            nodes {
                                id
                                number
                                title
                                url
                                createdAt
                                bodyHTML
                            }
                            pageInfo {
                                endCursor
                                hasNextPage
                            }
                        }
                    }
                }
            """

            val response: GitHubResponse =
                //val response: GitHubResponse =
                client.post("https://api.github.com/graphql") {
                    headers["Authorization"] = "Bearer $token"
                    contentType(ContentType.Application.Json)
                    setBody(GraphQLRequest(query))
                }.body()

           // println(Json.encodeToString(response))

            val discussions = response.data.repository.discussions.nodes
            allDiscussions.addAll(discussions)

            cursor = response.data.repository.discussions.pageInfo.endCursor


        } while (response.data.repository.discussions.pageInfo.hasNextPage)

        allDiscussions.sortByDescending { it.createdAt }

        return allDiscussions
    }

    @Serializable
    data class GitHubResponse(
        val data: Data
    )

    @Serializable
    data class Data(
        val repository: Repository
    )

    @Serializable
    data class Repository(
        val discussions: Discussions,
    )

    @Serializable
    data class Discussions(
        val nodes: List<Discussion>,
        val pageInfo: PageInfo,
    )

    @Serializable
    data class Discussion(
        val id: String,
        val number: Int,
        val title: String,
        val url: String,
        val createdAt: Instant,
        val bodyHTML: String,
    )

    @Serializable
    data class GraphQLRequest(
        val query: String
    )

    @Serializable
    data class PageInfo(
        val endCursor: String?,
        val hasNextPage: Boolean
    )
}

fun main() = runBlocking {
    val discussions = GitHubDiscussions.getDiscussions()
    for (discussion in discussions) {
        println(discussion)
    }
}