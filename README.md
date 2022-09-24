# Freenet Org Website Development Notes

## Overview

This is the repository for the Freenet project website. It is built on the [Kweb](https://github.com/kwebio/kweb-core)
framework in Kotlin, and is hosted on [Google Cloud](https://cloud.google.com/). There is a staging site at 
https://staging.freenet.org/, and the live site is at https://freenet.org/. Pushes to the 
[staging branch](https://github.com/freenet/freenetorg-website/tree/staging) will be automatically deployed to staging,
while pushes to the [production branch](https://github.com/freenet/freenetorg-website/tree/production) will go live.

## Testing Locally

If the environment variable `GOOGLE_APPLICATION_CREDENTIALS` is not set, or `FREENET_SITE_NO_DB` is set to `true`, 
the site will run in "offline" mode, using dummy data instead of the database. This makes it easy to test locally 
without needing to set up a local FireStore instance.