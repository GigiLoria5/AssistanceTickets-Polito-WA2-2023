# Server

## API

- GET `/API/products/`

    - Description: Allows to obtain all products within the system
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `500 Internal Server Error` (generic error)
    - Response body: An array of objects, for each containing productId, asin, brand, category,
      manufacturerNumber, name, price and weight (expressed in kg) or error message in case of error

      ```
      [
          ...,
          {
            "productId": 5,
            "asin": "B001AVRD62",
            "brand": "Yamaha",
            "category": "Surround Speakers",
            "manufacturerNumber": "NSSP1800BL",
            "name": "NS-SP1800BL 5.1-Channel Home Theater System (Black)",
            "price": 244.01,
            "weight": 1.60
          },
          ...
      ]
      ```

- GET `/API/products/{productId}`

    - Description: Allows to obtain all the information of a single product
    - Request parameter: productId of the requested product
    - Response: `200 OK` (success)
    - Error responses: `404 Not Found` (productId not found), `422 Unprocessable Entity` (validation of productId
      failed) or `500 Internal Server Error` (generic error)
    - Response body: An object containing productId, asin, brand, category, manufacturerNumber, name, price and
      weight (expressed in kg) of the requested product or error message in case of error

      ```
      {
          "productId": 61,
          "asin": "B06XSGYCHC",
          "brand": "Siriusxm",
          "category": "Satellite Radio",
          "manufacturerNumber": "SXEZR1V1",
          "name": "SiriusXM SXEZR1V1 XM Onyx EZR Satellite Radio Receiver with Vehicle Kit",
          "price": 79.99,
          "weight": 1.25
      }
      ```

- GET `/API/profiles/{email}`

    - Description: Allows to obtain all the information of a single profile
    - Request parameter: email of the requested user profile
    - Response: `200 OK` (success)
    - Error responses: `404 Not Found` (email not found), `422 Unprocessable Entity` (validation of email failed) or
      `500 Internal Server Error` (generic error)
    - Response body: An object containing profileId, email, name, surname, phoneNumber, address, city and country of the
      requested user or error message in case of error

      ```
      {
          "profileId": 1,
          "email": "johngreen@group.com",
          "name": "John",
          "surname": "Green",
          "phoneNumber": "3466281644",
          "address": "Corso Duca degli Abruzzi, 24",
          "city": "Turin",
          "country": "Italy",
      }
      ```

- POST `/API/profiles`

    - Description: Allows to create a profile
    - Request body: email, name, surname, phoneNumber, address, city and country of the profile

      ```
      {
        "email": "johngreen@group.com",
        "name": "John",
        "surname": "Green",
        "phoneNumber": "3466281644",
        "address": "Corso Duca degli Abruzzi, 24",
        "city": "Turin",
        "country": "Italy"
      }
      ```

    - Response: `201 Created` (success)
    - Error responses: `409 Conflict` (email already exists or phoneNumber already exists), `422 Unprocessable Entity`
      (validation of request body failed) or `500 Internal Server Error` (generic error)
    - Response body: An error message in case of error

      ```
      {
        "errorMessage": "a profile with the same email already exists"
      }
      ```


- PUT `/API/profiles/{email}`

    - Description: Allows to update information of an existing profile
    - Request parameter: email of the user profile to update
    - Request body: email, name, surname, phoneNumber, address, city and country of the profile to be updated

      ```
      {
        "email": "johnatan@group.com",
        "name": "John",
        "surname": "Green",
        "phoneNumber": "3163122442",
        "address": "Corso Einaudi, 16",
        "city": "Turin",
        "country": "Italy"
      }
      ```

    - Response: `200 OK` (success)
    - Error responses: `404 Not Found` (email not found), `409 Conflict` (email already exists or phoneNumber already
      exists), `422 Unprocessable Entity` (validation of request body or email failed) or `500 Internal Server Error` (
      generic error)
    - Response body: An error message in case of error

      ```
      {
        "errorMessage": "a profile with the same phoneNumber already exists"
      }
      ```
