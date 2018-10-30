# Project Overview
## Introduction
The purpose of this document is to provide an overview of the entire project. It will provide specific details on certain aspects of the project, including dependencies, database entities, layers, security, testing, etc.

## Gradle Tasks and Dependencies
- `./gradlew bootRun`
- `./gradlew test`

For more details, see [The Gradle Wrapper Documentation](https://docs.gradle.org/current/userguide/gradle_wrapper.html).

## Database and Hibernate
The project relies on PostgreSQL 10.5 for a persistent storage solution.

The project uses the Spring JPA and Hibernate ORM libraries for managing database entities.

## Project Structure
### Infrastructure Layer
### Core Layer
### Application Layer
### API Layer

## Exception Handling

## Internationalization
Currently, our project only supports the US English locale. Internationalization (i18n) will be implemented at a later date.

## Security
The project is secured using the JSON Web Token library provided by the Spring Security framework. For more details, see `docs/Security.md`.

## Testing
### Unit Tests
### Integration Tests

## Continuous Integration
Continuous Integration (CI) is provided through [GitLab Pipelines](https://docs.gitlab.com/ee/ci/pipelines.html).

A new job is executed every time a commit is pushed to the GitLab remote repository. It is configured to run all unit and integration tests.

Continuous integration is configured in `.gitlab-ci.yml` located in the project root directory.

## Externalized Configuration

For more details, see [Spring Boot Externalized Config Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html).