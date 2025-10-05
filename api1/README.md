# Axoniq Platform Quickstart

This simple GiftCard application demonstrates the core concepts of the Axoniq Platform, including Command Handling,
Event Sourcing, and Projections. It is built using the Axon Framework and Spring Boot.

## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Docker (for running the Axon Server)
- An IDE like IntelliJ IDEA or Eclipse (optional)

## Getting Started

Run docker-compose to start Axon Server:

```bash
docker-compose up -d
```

Now, you can run the application using Maven:

```bash
mvn spring-boot:run
```

Alternatively, you can run the application from your IDE by running the `QuickstartApplication` class.

## Using the Application
You can interact with the application via the basic UI available on [http://localhost:8080](http://localhost:8080).
The events are stored in Axon Server, which you can access at [http://localhost:8024](http://localhost:8024).

Your application and Axon Server will both connect to the Axoniq Platform. You can monitor and manage your application from there: [https://monitor.axoniq.io/workspace/6fbd767b/env/6fbd767b-0](https://monitor.axoniq.io/workspace/6fbd767b/env/6fbd767b-0).

## Useful resources

You can access a lot of resources through the [Axoniq Platform](https://platform.axoniq.io/). This includes:

- Extensive Documentation
- Axoniq Academy with free courses
- AI-powered Development Agent to create your applications faster
- Community Forum to ask questions and share knowledge
- Monitor your applications
- And much more!
