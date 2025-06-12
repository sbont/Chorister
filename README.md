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

#### Docker setup
The file `compose.yaml` configures the services needed to run the backend application.
1. Run `docker compose -f 'compose.yaml' up -d --build`.
This starts containers runnning our database (CockroachDB), our S3 file storage service (Minio), our authentication server (Zitadel) with its own database (Postgres), and a reverse-proxy (Caddy). The data of these services is persisted in the `/data` folder; this means that you can delete the containers and recreate them (as long as you don't delete `/data`).
2. When all containers are up, it's time to configure our auth server. Go to `localhost:9000` and log in with `zitadel-admin@chorister.localhost` and password `Password1!`. You will be asked to change your password, make sure to save your newly chosen password somewhere.
3. On tab Projects, add a new project with name "Chorister".
4. Create two Apps, with the following details:
    a. `chorister-web` with Type: Web; Authentication Method: PKCE; Redirect URIs: `http://localhost:8080/authorized` (enable Development Mode); Post Logout URIs: `http://localhost:8080/`
    b. `chorister-api` with Type: API; Authentication Method: Basic. Important!: Make sure to copy the client secret when displayed in the pop-up that follows creating this app.
    Save this secret as an environment variable with key `CHORISTER_API_SECRET`. Add also a variable `CHORISTER_API_CLIENT_ID` with the Client Id as its value.
5. Next, let's set up our S3 server. Go to `localhost:9012` and log in with username `minioadmin` and the same as its password, `minioadmin`.
6. Create a bucket called `chorister-files`.


#### Manual setup
To run a local database, install a local Cockroach cluster using [the guidelines](https://www.cockroachlabs.com/docs/stable/secure-a-cluster). Zitadel is used as the authentication provider, you can find a deployment guide [here](https://zitadel.com/docs/self-hosting/deploy/overview).
For a local S3 server, you can use [Minio](https://github.com/minio/minio). Use [Caddy](https://caddyserver.com/docs/install) as a local reverse proxy if you want to run the different components from a single URL.

## Connect
This project is started by Steven Bontenbal. You can find him on 🦋 [BlueSky](https://bsky.app/profile/steven-bl.bsky.social) and 🔗 [LinkedIn](https://www.linkedin.com/in/stevenbontenbal/).
