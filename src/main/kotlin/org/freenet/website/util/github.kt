@file:UseSerializers(InstantSerializer::class)

package org.freenet.website.util

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
import org.freenet.website.pages.blog.formatForUrl
import java.time.Duration
import java.time.Instant

private val logger = KotlinLogging.logger { }

object Github {

    private val scope = CoroutineScope(Dispatchers.IO)

    @Volatile var discussions: DiscussionStore? = null

    @Volatile var pullRequests: List<PullRequest>? = null

    data class DiscussionStore(val discussions : List<Discussion>, val generated : Instant = Instant.now()) {
        val discussionsByNumber = discussions.associateBy { it.number }
    }

    init {
        scope.launch(Dispatchers.IO) {
            while(true) {
                logger.info { "Retrieving discussions from GitHub" }
                val newDiscussions = getDiscussions()
                val curDiscussions = discussions
                if (curDiscussions != null && newDiscussions == curDiscussions.discussions) {
                    logger.debug { "Discussions unchanged" }
                } else {
                    logger.info { "Discussions updated" }
                    discussions = DiscussionStore(newDiscussions)
                }
                logger.info { "Github discussions retrieved" }

                logger.info { "Retrieve recently merged pull requests from Github" }
                pullRequests = getPullRequests(5)

                delay(Duration.ofMinutes(30))
            }
        }
    }

    suspend fun getPullRequests(howMany: Int): List<PullRequest> {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                })
            }
        }

        val token: String = System.getenv("GITHUB_TOKEN") ?: error("GITHUB_TOKEN not set")

        val query = """
            {
              repository(owner: "freenet", name: "freenet-core") {
                pullRequests(
                  first: $howMany
                  states: MERGED
                  orderBy: {field: UPDATED_AT, direction: DESC}
                ) {
                  nodes {
                    title
                    url
                    mergedAt
                    author {
                      login
                      url
                    }
                  }
                }
              }
            }
        """.trimIndent()

        val response: GitHubPullRequestsResponse =
            client.post("https://api.github.com/graphql") {
                headers["Authorization"] = "Bearer $token"
                contentType(ContentType.Application.Json)
                setBody(GraphQLRequest(query))
            }.body()

        return response.data.repository.pullRequests.nodes
    }

    // Data classes for pull requests with author
    @Serializable
    data class GitHubPullRequestsResponse(
        val data: PullRequestData
    )

    @Serializable
    data class PullRequestData(
        val repository: PullRequestRepository
    )

    @Serializable
    data class PullRequestRepository(
        val pullRequests: PullRequests,
    )

    @Serializable
    data class PullRequests(
        val nodes: List<PullRequest>,
    )

    @Serializable
    data class PullRequest(
        val title: String,
        val url: String,
        val mergedAt: Instant,
        val author: Author,
    )

    @Serializable
    data class Author(
        val login: String,
        val url: String,
    )

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
    ) {
        val freenetUrlPath : String by lazy {
            "/blog/${number}/${formatForUrl(title)}.html"
        }
    }

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
    val discussions = Github.getDiscussions()
    for (discussion in discussions) {
        println(discussion)
    }
}