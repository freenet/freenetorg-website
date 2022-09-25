# Freenet Org Website Development Notes

## Overview

This is the repository for the Freenet project website. It is built on the [Kweb](https://github.com/kwebio/kweb-core)
framework in Kotlin, and is hosted on [Google Cloud](https://cloud.google.com/). There is a staging site at 
https://staging.freenet.org/, and the live site is at https://freenet.org/. Pushes to the 
[staging branch](https://github.com/freenet/freenetorg-website/tree/staging) will be automatically deployed to staging,
while pushes to the [production branch](https://github.com/freenet/freenetorg-website/tree/production) will go live.

## Prerequisites

1. [A Recent Java Development Environment](https://adoptopenjdk.net/)
2. [Gradle Build Tool](https://gradle.org/install/)
3. [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) (optional but highly recommended)

## Setting up

1. Clone the repository, two options:
   1. Command Line
      ```bash
      $ git clone git@github.com:freenet/freenetorg-website.git
      ```
   2. IntelliJ IDEA
      1. File -> New -> Project from Version Control -> Git
      2. Enter the URL of the repository, `git@github.com:freenet/freenetorg-website.git`
2. Open the project in IntelliJ IDEA
3. Create a new run configuration
   1. Run -> Edit Configurations...
   2. Click the `+` button and select `Application`
   3. Name the configuration `freenet.org website`
   4. Set the `Main class` to `org.freenet.website.MainKt`
   7. Set environment variables to `FREENET_SITE_LOCAL_TESTING=true`
      * This will use dummy data so no connection to the database is required
4. Run the configuration
5. Open a browser and navigate to http://localhost:8080/

## Pull Requests

To submit improvements create a pull request against the `staging` branch.