# helmes-kotlin

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

* [Ktor Documentation](https://ktor.io/docs/home.html)
* [Ktor GitHub page](https://github.com/ktorio/ktor)
* [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). [Request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up).

## Features

Here's a list of features included in this project:

| Name                                                                    | Description                                    |
|-------------------------------------------------------------------------|------------------------------------------------|
| [Status Pages](https://start.ktor.io/p/io.ktor/server-status-pages)     | Provides exception handling for routes         |
| [Static Content](https://start.ktor.io/p/io.ktor/server-static-content) | Serves static files from defined locations     |
| [Thymeleaf](https://start.ktor.io/p/io.ktor/server-thymeleaf)           | Serves HTML content, templated using Thymeleaf |

## Building & Running

The quickest way to start the application is with the Gradle wrapper. This requires Java 25:

```shell
./gradlew run
```

Open http://localhost:8080 after the application starts. The remaining build tasks are:

| Task              | Description       |
|-------------------|-------------------|
| `./gradlew test`  | Run the tests     |
| `./gradlew build` | Build the project |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

### Database configuration

SQLite requires no setup and defaults to `./data/app.db`. To use another location:

```shell
DATABASE_JDBC_URL=jdbc:sqlite:/absolute/path/to/app.db ./gradlew run
```

### Running with Docker Compose

Docker Compose builds and starts the application with its SQLite database stored in the bind-mounted `./data` directory:

```shell
docker compose up
```

The application is then available at http://localhost:8080. Change the host port with `APP_PORT`, or override the JDBC
URL with `DATABASE_JDBC_URL`:

```shell
APP_PORT=9090 docker compose up
```

Compose builds the image automatically when it is missing. Add `--build` only when local source changes need to be
rebuilt.
