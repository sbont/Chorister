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
This repository contains all the code necessary to run Chorister. In order to develop and run the application locally, you need Java > v17 and Gradle for the backend; Node > v22 and Yarn for the browser client.

#### Docker setup
The file `compose.yaml` configures the services needed to run the backend application.
1. Run `docker compose -f 'compose.yaml' up -d --build`.
This starts containers runnning our database (CockroachDB), our S3 file storage service (Minio), our authentication server (Zitadel) with its own database (Postgres), and a reverse-proxy (Caddy). The data of these services is persisted in the `/data` folder; this means that you can delete the containers and recreate them (as long as you don't delete `/data`).
1. When all containers are up, it's time to configure our auth server. Go to `localhost:9000` and log in with `zitadel-admin@chorister.localhost` and password `Password1!`. 
1. On tab Projects, add a new project with name "Chorister".
1. Create two Apps, with the following details:
    a. `chorister-web` with Type: Web; Authentication Method: PKCE; Redirect URIs: `http://localhost:8080/authorized` (enable Development Mode); Post Logout URIs: `http://localhost:8080/`. After creatiopn, change the Auth Token Type to JWT and enable the 3 checkboxes to include user roles to the access token and to include user roles and info in the ID token.
    Then copy the client for this app to a new `.env.development` file (use the sample file) and set the client ID as value for `VITE_APP_CHORISTER_WEB_CLIENT_ID`.
    b. `chorister-api` with Type: API; Authentication Method: Basic. Important!: Make sure to copy the client secret when displayed in the pop-up that follows creating this app.
    Save this secret as an environment variable with key `CHORISTER_API_SECRET`. Add also a variable `CHORISTER_API_CLIENT_ID` with the Client Id as its value.
1. Create a system user called `API-admin` and make this user an org-wide user manager.
1. For this user, generate a personal access token and store this as a variable with key `CHORISTER_ADMIN_ACCESS_TOKEN`.
1. Next, let's set up our S3 server. Go to `localhost:9012` and log in with username `minioadmin` and the same as its password, `minioadmin`.
1. Create a bucket called `chorister-files`.
1. Finally, create a database user by executing ```
CREATE USER app_user WITH PASSWORD 'user1234!';
GRANT CONNECT ON DATABASE chorister TO app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE ON SEQUENCES TO app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT EXECUTE ON FUNCTIONS TO app_user;
GRANT SELECT, INSERT, DELETE, UPDATE ON all TABLES in schema public TO app_user;
```

#### Manual setup
To run a local database, install a local Cockroach cluster using [the guidelines](https://www.cockroachlabs.com/docs/stable/secure-a-cluster). Zitadel is used as the authentication provider, you can find a deployment guide [here](https://zitadel.com/docs/self-hosting/deploy/overview).
For a local S3 server, you can use [Minio](https://github.com/minio/minio). Use [Caddy](https://caddyserver.com/docs/install) as a local reverse proxy if you want to run the different components from a single URL.

#### Running the app

Before the first time running the app, run `gradle update` in order to migrate the database. Run `ChoristerApplication.kt` with program arguments `--spring.profiles.active=development`.

## Developing the app

Whenever adding or making changes to entities, make sure to have Liquibase add a migration for these changes. Run `gradle diffChangelog` in order to update the changelog and run `gradle update` to run the migration against the database before running the app.

## Connect
This project is started by Steven Bontenbal. You can find him on 🦋 [BlueSky](https://bsky.app/profile/steven-bl.bsky.social) and 🔗 [LinkedIn](https://www.linkedin.com/in/stevenbontenbal/).
