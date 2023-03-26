# Assistance Tickets System

This is a system for managing assistance tickets developed during the Web Applications II course of the Master's degree
in Computer Engineering at the Polytechnic of Turin.

## Overall Idea

The purpose of this system is to provide a ticketing portal for customers of electronic goods who have purchased an
(extra) warranty. The ticketing system allows customers to request assistance or information for their purchased
devices.

* **Customers** must register on the platform using a unique identifier provided with their purchase receipt.
  After registration, customers can log in and raise problems.
* **Technicians** are responsible for reviewing and resolving the
  problems of customers. They are assigned problems based on their skills and expertise, and may provide assistance
  through the platform or other means, depending on the service level agreement.
* **Administrators** have the ability to modify
  the system configuration, create new professional users (cashiers and technicians), and handle customer complaints
  about
  the service received.
* **Cashiers** are responsible for adding the warranty option to the customer's purchase and printing a
  one-time code associated with the device on the receipt for later registration.

This system is designed to streamline the assistance ticketing process, providing customers with a more efficient and
effective way to receive support for their electronic devices.

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
   docker exec -it pg_container psql -U postgres -W -c "CREATE DATABASE assistance_tickets;"
   ```
   When you run the command, you will be asked for the password of the database, which in this
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

The application will be available at `http://localhost:3000/` and already contains some predefined data. See the
client [documentation](client/README.md) for the specific routes available.

## Contributing

1. Create a new branch from the dev branch (e.g., feature/my-new-feature)
2. Implement your changes and commit them to the new branch
3. Push the branch
4. Open a pull request against the dev branch of the original repository (describing your changes)
