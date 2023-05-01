@file:UseSerializers(LocalDateSerializer::class)

package org.freenet.website.dev

import LocalDateSerializer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kweb.state.KVar
import mu.two.KotlinLogging
import java.time.Duration
import java.time.LocalDate
import java.util.*
import kotlinx.coroutines.time.delay as delay

private val logger = KotlinLogging.logger { }

object PivotalTracker {

    private val scope = MainScope()

    val releases: KVar<TreeSet<ReleaseWithDate>?> = KVar(null)

    init {
        scope.launch(Dispatchers.IO) {
            logger.info { "Retrieving releases from PivotalTracker" }
            while (true) {
                releases.value = getReleases()
                delay(Duration.ofHours(4))
                logger.info { "Refreshing releases from PivotalTracker" }
            }
        }
    }

    suspend fun getReleases(): TreeSet<ReleaseWithDate> {
        // Initialize the Ktor client
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    serializersModule = SerializersModule {
                        contextual(LocalDate::class, LocalDateSerializer)
                    }
                })
            }
        }

        val projectId = "2477110"

        // API token for authentication
        val token: String = System.getenv("PIVOTAL_API_TOKEN") ?: error("PIVOTAL_API_TOKEN not set")

        val endpoint = "https://www.pivotaltracker.com/services/v5/projects/$projectId/iterations"

        var offset = 0
        val limit = 10
        val iterations: MutableList<Iteration> = mutableListOf()

// Use the client to send a GET request to the endpoint with limit and offset parameters
        while (true) {
            val paginatedIterations: List<Iteration> =
                client.get("$endpoint?scope=current_backlog&limit=$limit&offset=$offset") {
                    headers["X-TrackerToken"] = token
                }.body()

            if (paginatedIterations.isEmpty()) {
                break
            }

            iterations.addAll(paginatedIterations)
            offset += limit

            // Avoid hitting rate limit
            delay(Duration.ofSeconds(1))
        }

        val releases: TreeSet<ReleaseWithDate> = TreeSet()

// Iterate through the iterations and print release information for each one
        for (iteration in iterations) {
            for (story in iteration.stories) {
                if (story.storyType == "release") {
                    releases += ReleaseWithDate(story.id, story.name, story.url, iteration.finish)
                }
            }
        }

        return releases

    }

    // Define data classes for the Iteration and Story objects
    @Serializable
    data class Iteration(val number: Int, val start: LocalDate, val finish: LocalDate, val stories: List<Story>)

    @Serializable
    data class Story(val id: Int, val name: String, @SerialName("story_type") val storyType: String, val url: String)

    @Serializable
    data class Release(val id: Int, val name: String)

    data class ReleaseWithDate(val storyId: Int, val name: String, val url: String, val date: LocalDate) :
        Comparable<ReleaseWithDate> {
        override fun compareTo(other: ReleaseWithDate): Int {
            return if (date == other.date) {
                storyId.compareTo(other.storyId)
            } else {
                date.compareTo(other.date)
            }
        }
    }

}

