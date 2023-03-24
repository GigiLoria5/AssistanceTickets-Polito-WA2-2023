## API

_TBD_

## Installation

To install the backend, follow these steps:

1. Clone the repository on your machine
2. Open a terminal and navigate to the project directory
3. Run the following command to build the application  
   `gradle build`
4. Once the build is complete, run the following command to start the application  
   `java -jar build/libs/assistance-tickets-0.1.jar`  
   The application will start on port 8080 by default.
5. In addition to the backend, you will also need to run a PostgreSQL database using Docker Compose. To do this, run the
   following command:  
   `docker-compose up -d`

## Contributing

1. Create a new branch from the dev branch (e.g., feature/my-new-feature)
2. Implement your changes and commit them to the new branch
3. Push the branch
4. Open a pull request against the dev branch of the original repository (describing your changes)
