package org.freenet.website.auth
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.client.engine.cio.*
import io.ktor.client.statement.*
import io.ktor.server.response.*
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.http.HeadersBuilder
import kweb.plugins.KwebPlugin
import org.jsoup.nodes.Document

class GithubAuthPlugin(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String = "",
    dependsOn: Set<KwebPlugin> = emptySet()
) : KwebPlugin(dependsOn) {

    override fun decorate(doc: Document) {
        // Add the GitHub OAuth2 API stylesheet to the HTML document
        doc.head().appendElement("link")
            .attr("rel", "stylesheet")
            .attr("href", "https://cdnjs.cloudflare.com/ajax/libs/octicons/3.5.0/octicons.min.css")

        // Add the GitHub OAuth2 API JavaScript library to the HTML document
        doc.head().appendElement("script")
            .attr("src", "https://cdnjs.cloudflare.com/ajax/libs/octicons/3.5.0/octicons.min.js")
    }

    override fun executeAfterPageCreation(): String {
        // Create the "Login with GitHub" button
        return """
            function createGithubAuthButton() {
                var button = document.createElement('button');
                button.innerHTML = '<span class="octicon octicon-mark-github"></span> Login with GitHub';
                button.className = 'btn btn-primary';
                button.onclick = function() {
                    window.location.href = 'https://github.com/login/oauth/authorize?client_id=$clientId&redirect_uri=' + window.location.origin + '$redirectUri';
                };
                document.body.appendChild(button);
            }
            createGithubAuthButton();
        """
    }

    override fun appServerConfigurator(routeHandler: Routing) {
        // Set up a new route to handle the redirect URI
        routeHandler.get("$redirectUri") {
            // Retrieve the authorization code from the query parameters
            val code = call.request.queryParameters["code"] ?: error("No code parameter found")

            // Send the request to the GitHub OAuth2 API to retrieve an access token for the user
            val client = HttpClient(CIO)
            /*
            val response = client.submitForm(
                url = "https://github.com/login/oauth/access_token",
                formParameters = Parameters.build {
                    append("client_id", clientId)
                    append("client_secret", clientSecret)
                    append("code", code)
                },
                headers = {
                    append(HttpHeaders.Accept, "application/json")
                }
            )

            Submit the request to the GitHub OAuth2 API to retrieve an access token for the user
            */
            val response = client.submitForm(
                url = "https://github.com/login/oauth/access_token",
                formParameters = Parameters.build {
                    append("client_id", clientId)
                    append("client_secret", clientSecret)
                    append("code", code)
                },
            )

            // Parse the response from the GitHub OAuth2 API to retrieve the access token
            if (response.status.isSuccess()) {
                val authResponse : GithubAuthResponse = response.body()
            } else {
                error("Failed to retrieve access token from GitHub OAuth2 API: ${response.status}")
            }


            // You can now use the access token to authenticate the user and access their GitHub information
            // ...
        }
    }


}

class GithubAuthResponse(val accessToken: String, val tokenType: String, val scope: String)