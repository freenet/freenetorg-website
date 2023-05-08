package org.freenet.website.apis

import org.kohsuke.github.GitHub
import org.kohsuke.github.GitHubBuilder

object Github {
    private val token: String = System.getenv("GITHUB_TOKEN") ?: error("GITHUB_TOKEN not set")

    private val github = GitHubBuilder().withOAuthToken(token).build()

    fun getDiscussions() {
        val teams = github.myTeams["freenet"]!!.first { it.name == "Locutus" }
        println(teams)
    }
}

fun main() {
    Github.getDiscussions()
}