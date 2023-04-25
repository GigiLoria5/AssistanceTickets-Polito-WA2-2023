# Assistance Tickets System

This is a system for managing assistance tickets developed during the Web Applications II course of the Master's degree
in Computer Engineering at the Polytechnic of Turin.

## Overall Idea

The purpose of this system is to provide a ticketing portal for customers of electronic goods who have purchased an
(extra) warranty. The ticketing system allows customers to request assistance or information for their purchased
devices.

- **Customers** must register on the platform using a unique identifier provided with their purchase receipt.
  After registration, customers can log in and raise problems.
- **Experts** are responsible for reviewing and resolving the problems of customers. They are assigned problems based on their skills and expertise, and may provide assistance
  through the platform or other means, depending on the service level agreement.
- **Managers** have the ability to modify the system configuration, create new professional users (cashiers and experts), and handle customer complaints
  about the service received.
- **Cashiers** are responsible for adding the warranty option to the customer's purchase and printing a one-time code associated with the device on the receipt for later registration.

This system is designed to streamline the assistance ticketing process, providing customers with a more efficient and
effective way to receive support for their electronic devices.

## Ticketing Process

The ticketing progress consists of five states: OPEN, IN PROGRESS, RESOLVED, CLOSED, and REOPENED. 
When a new ticket is created, it is in the **OPEN** state without a priority or an assigned expert. When the manager assigns a priority and an expert, the ticket moves to the **IN PROGRESS** state. 
When a ticket is **IN PROGRESS** or **RESOLVED**, users or experts can open a chat and exchange messages. If a chat is already open, it is not possible to open another one. A ticket in progress can be stopped, returning it to the initial state, with no expert assigned and the chat (if any) will become temporarily inactive. 
A **REOPENED** ticket is similar to an **OPEN** ticket, except that it already has the priority set (although it can be changed), it has no expert assigned, and it is not possible to start a new chat or send messages to an existing one. Once a ticket is RESOLVED, the chat is left open for feedback and/or updates before closing the ticket. 
A ticket can be **CLOSED** from any state (except if it is already closed). Once closed, it cannot be modified, but it can be reopened later.

## Technologies Used

### Client

- React
- Javascript
- Bootstrap
- Webpack

### Server

- Kotlin
- Spring Boot
- Gradle
- PostgreSQL
- TestContainers

## Usage

1. Navigate to the `server` directory, start the PostgreSQL database using Docker Compose, and create the
   `assistance_tickets` database:
   ```
   cd server
   docker-compose up -d
   docker exec -it pg_container psql -U postgres -W -c "CREATE DATABASE assistance_tickets;"
   ```
   When you run the command, you will be asked for the password of the database, which in this
   case is `p4ssw0rd`. Enter it and press enter.
2. If this is the first time you are running the application, open the application.properties file located in the server/src/main/resources directory and set the following property:
   ```
   spring.jpa.hibernate.ddl-auto=create
   ```
   This will create the necessary tables in the database. If you have already run the application before, set the property to `validate` instead to avoid recreating the tables.
3. Build and start the server by running the ServerApplication.kt file or with the following commands:
   ```
   ./gradlew build
   ./gradlew bootRun
   ```

The application will be available at `http://localhost:8080/` and already contains some predefined data. See the
client [documentation](client/README.md) for the specific routes available.

## Contributing

1. Create a new branch from the dev branch (e.g., feature/my-new-feature)
2. Implement your changes and commit them to the new branch
3. Push the branch
4. Open a pull request against the dev branch of the original repository (describing your changes)
