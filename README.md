# Assistance Tickets System

This is a system for managing assistance tickets developed during the Web Applications II course of the Master's degree
in Computer Engineering at the Polytechnic of Turin.

## Purpose

The purpose of this system is to allow customers of electronic goods to manage assistance tickets. The system can be
used by customers, technicians, and managers, each with their specific functionalities.

## Technologies Used

### Client

* React
* Javascript
* Bootstrap
* Webpack

### Server

* Kotlin
* Spring Boot
* Gradle
* PostgreSQL

## Usage

1. Navigate to the `server` directory, start the PostgreSQL database using Docker Compose, and create the
   `assistance_tickets` database:
   ```
   cd server
   docker-compose up -d
   docker exec -it <name-of-the-database-container> psql -U postgres -W -c "CREATE DATABASE assistance_tickets;"
   ```
   Replace `<name-of-the-database-container>` with the name of the PostgreSQL container. You can find the name by
   running docker ps. When you run the command, you will be asked for the password of the database user, which in this
   case is `p4ssw0rd`. Enter it and press enter.
2. Build and start the server:
   ```
   ./gradlew build
   ./gradlew bootRun
   ```
3. Navigate to the `client` directory and install the dependencies:
   ```
   cd ../client
   npm install
   ```
4. Start the client:
   ```
   npm start
   ```

The application will be available at `http://localhost:3000/`. See the client [documentation](client/README.md) for the
specific routes available.

## Contributing

1. Create a new branch from the dev branch (e.g., feature/my-new-feature)
2. Implement your changes and commit them to the new branch
3. Push the branch
4. Open a pull request against the dev branch of the original repository (describing your changes)
