# Chorister 🎵
## About
Chorister is an open source planning application for choirs and bands. It allows you to manage your repertoire, upload and share the scores, and keep track of setlists of your upcoming events. 
Ditch your spreadsheets and file repositories and start using chorister instead, today!

Chorister is currently in beta. 

## Use
Chorister is free to use. Sign up at [chorister.co](https://chorister.co) for a free account.

## Contribute
By keeping this project open source, we can share this technology with every church band or choir in need of a planning tool without having to resort to expensive commercial tools. You can contribute towards this goal by logging an issue or filing a PR!

### Getting started
This repository contains all the code necessary to run Chorister. In order to develop and run the application locally, you need Java > v17 for the backend; Node > v22 and Yarn for the browser client. 
To run a local database, install a local Cockroach cluster using [the guidelines](https://www.cockroachlabs.com/docs/stable/secure-a-cluster). Zitadel is used as the authentication provider, you can find a deployment guide [here](https://zitadel.com/docs/self-hosting/deploy/overview).
For a local S3 server, you can use [Minio](https://github.com/minio/minio). Use [Caddy](https://caddyserver.com/docs/install) as a local reverse proxy if you want to run the different components from a single URL.

## Connect
This project is started by Steven Bontenbal. You can find him on 🦋 [BlueSky](https://bsky.app/profile/steven-bl.bsky.social) and 🔗 [LinkedIn](https://www.linkedin.com/in/stevenbontenbal/).
