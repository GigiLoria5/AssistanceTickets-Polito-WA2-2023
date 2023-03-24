# Assistance Tickets System
This is a system for managing assistance tickets developed during the Web Applications II course of the Master's degree in Computer Engineering at the Polytechnic University of Turin.

## Purpose
The purpose of this system is to allow customers of electronic goods to manage assistance tickets. The system can be used by customers, experts, and managers, each with their specific functionalities.

## API
- GET /API/products/

    - Description: Lists all registered products in the DBo create a client
    - Request body: _None_
    - Response: 200 OK (success)
    - Error responses: 500 Internal Server Error (generic error)
    - Response body: list of all registered products, for each containing productId, price, asin, brand, category, manufacturer_number, name and weight, or error message in case of error

    ```
    {
        ["productId":"",
        "price": "",
        "asin": "",
        "brand": "",
        "category": "",
        "manufacturer_number": "",
        "name": "",
        "weight": ""],...
    }
    ```

- GET /API/products/{productId}

    - Description: Retrieves the details of product {productId} or fails if it does not exist
    - Request parameter: ean of requested product
    - Response: 200 OK (success)
    - Error responses: 404 Not Found (product does not exist), 422 Unprocessable Entity (productId is not valid),  500 Internal Server Error (generic error)
    - Response body: productId, price, asin, brand, category, manufacturer_number, name and weight for the requested product, or error message in case of error

      ```
      {
        "productId":"",
        "price": "",
        "asin": "",
        "brand": "",
        "category": "",
        "manufacturer_number": "",
        "name": "",
        "weight": ""
      }
      ```

- GET /API/profiles/{email}

    - Description: Retrieves the details of user profiles {email} or fails if does not exists
    - Request parameter: email of the requested user profile
    - Response: 200 OK (success)
    - Error responses: 404 Not Found (user profile does not exist), 422 Unprocessable Entity (email is not valid),  500 Internal Server Error (generic error)
    - Response body: id, email, name, surname, phone_number, address, city and nation of the requested user, or error message in case of error

    ```
    {
      "id": "1",
      "email": "johngreen@group.com",
      "name": "John",
      "surname": "Green",
      "phone_number": "3466281644",
      "address": "Corso Duca degli Abruzzi, 24",
      "city": "Turin",
      "nation": "Italy",
    }
    ```

- POST /API/profiles

    - Description: Converts the request body into a ProfileDTO and store it into the DB, provided that the email address does not already exist
    - Request body: email, name, surname, phone_number, address, city and nation of the new user profile

      ```
      {
        "email": "johngreen@group.com",
        "name": "John",
        "surname": "Green",
        "phone_number": "3466281644",
        "address": "Corso Duca degli Abruzzi, 24",
        "city": "Turin",
        "nation": "Italy"
      }
      ```

    - Response: 201 Created (success)
    - Error responses: 422 Unprocessable Entity (request body is not valid),  500 Internal Server Error (generic error)
    - Response body: _None_, or error message in case of error





- PUT /API/profiles/{email}

    - Description: Converts the request body into a ProfileDTO and replace the corresponding entry in the DB; fails if the email does not exist.
    - Request body: field/s of the user profile to be updated

      ```
      {
        "phone_number": "3395224124"
      }
      ```

    - Request parameter: email of the requested user profile
    - Response: 201 Created (success)
    - Error responses: 404 Not Found (user profile does not exist), 422 Unprocessable Entity (request body is not valid),  500 Internal Server Error (generic error)
    - Response body: _None_, or error message in case of error




## Installation
To install the backend, follow these steps:
1. Clone the repository on your machine
2. Open a terminal and navigate to the project directory
3. Run the following command to build the application  
   `gradle build`
4. Once the build is complete, run the following command to start the application  
   `java -jar build/libs/assistance-tickets-0.1.jar`  
   The application will start on port 8080 by default.
5. In addition to the backend, you will also need to run a PostgreSQL database using Docker Compose. To do this, run the following command:  
   `docker-compose up -d`

## Contributing
1. Create a new branch from the dev branch (e.g., feature/my-new-feature)
2. Implement your changes and commit them to the new branch
3. Push the branch
4. Open a pull request against the dev branch of the original repository (describing your changes)
