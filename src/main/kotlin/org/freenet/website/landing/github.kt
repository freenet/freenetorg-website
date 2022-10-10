package org.freenet.website.landing

import kweb.state.KVal
import org.kohsuke.github.GHIssueState
import org.kohsuke.github.GitHub

private val github = GitHub.connect()

private val kwebRepo = github.getRepository("kwebio/kweb-core")

fun retrieveIssues(): KVal<List<NewsItem>> {

    kwebRepo.getIssues(GHIssueState.OPEN).toList().map { issue ->


        //       if (issue.labels.con)
    }
    TODO()//  }
}
