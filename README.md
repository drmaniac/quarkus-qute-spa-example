[![Java CI with Gradle](https://github.com/drmaniac/quarkus-qute-spa-example/actions/workflows/build.yml/badge.svg)](https://github.com/drmaniac/quarkus-qute-spa-example/actions/workflows/build.yml)

# quarkus spa example using qute and htmx

Example Project using quarkus qute and htmx for a single page application.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./gradlew build
```
It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.
