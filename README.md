# Spring Boot Application with H2 Database

This repository contains a Spring Boot application configured to use a H2 in-memory database. This setup allows for easy testing and integration.

## Table of Contents
- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Design Explanation](#design-explanation)
- [API Documentation](#api-documentation)

## Overview

This Spring Boot application is designed to manage a collection of books. It includes functionality for creating, reading, updating, and deleting books. The application uses an H2 in-memory database for data storage, making it easy to set up and run without needing an external database server.

## Prerequisites

Before you begin, ensure you have met the following requirements:
- Java Development Kit (JDK) 17 or later
- Maven 3.6.3 or later
- Git

## Installation

1. **Clone the repository**

    ```bash
    git clone https://github.com/sypha999/bookStore.git
    cd your-repo
    ```

2. **Build the application**

   Use Maven to build the application.

    ```bash
    mvn clean install
    ```

## Running the Application

1. **Run the application using Maven**

    ```bash
    mvn spring-boot:run
    ```

   Alternatively, you can run the JAR file directly:

    ```bash
    java -jar target/your-application-name.jar
    ```

2. The application will start and listen on the default port `8080`.

## Design Explanation

### Architecture

This application follows a typical layered architecture:
- **Controller Layer**: Handles HTTP requests and responses.
- **Service Layer**: Contains the business logic.
- **Repository Layer**: Interacts with the database.
- **Security Layer**: The application uses JWT to authenticate users

### Entities

The primary entities  in this application are the `Book` entity, which represents a book in the collection and the `User` entity which represents a user in the collection.


### API Documentation
The api documentation can be easily accessed when imported through the postman collection https://api.postman.com/collections/24877700-bc6eaeca-f9b8-4188-80df-b6dee830305f?access_key=PMAT-01HYY73PVX8898SZAECX5EWDKF

It can also be accessed upon running the application on http://localhost:8080/swagger
