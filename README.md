# Freenet Org Website Development Notes

## Overview

This is the repository for the Freenet project website. It is built on the [Kweb](https://github.com/kwebio/kweb-core)
framework in Kotlin, and is hosted on [Google Cloud](https://cloud.google.com/). There is a staging site at 
https://staging.freenet.org/, and the live site is at https://freenet.org/. Pushes to the 
[staging branch](https://github.com/freenet/freenetorg-website/tree/staging) will be automatically deployed to staging,
while pushes to the [production branch](https://github.com/freenet/freenetorg-website/tree/production) will go live.

## Testing Locally

If and only if the environment variable `FREENET_SITE_LOCAL_TESTING` is set to `true`, the site will run in "offline" 
mode, using dummy data instead of the database. This makes it easy to test locally without needing to set up a local 
FireStore instance.