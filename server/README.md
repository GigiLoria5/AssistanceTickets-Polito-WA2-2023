# Server 
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

