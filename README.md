# BookingTesting

A small educational project focused on unit testing a Java application for hotel room booking.

 Russian version: [README.ru.md](README.ru.md)

## Overview

This project models a simple booking domain:

- **Booking** — booking entity
- **Customer** — customer entity
- **Room** — room entity
- **BookingService** — booking management service
- **RoomService** — room management service
- **NotificationService** — external notification service (dependency)

## Tech stack

- Java 17
- Gradle 7.4.1
- JUnit 5
- Mockito
- AssertJ

## Tests

Included test coverage:

- Model tests for **Booking**
- Unit tests for **BookingService** and **RoomService**
- Mocking external dependencies with **Mockito**
- Business logic assertions

## How to run

Build:

```bash
./gradlew clean build

Run tests:

./gradlew test

Project structure
src/main/java/com/skillbox/hotel
src/test/java/com/skillbox/hotel



