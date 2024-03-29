# Server

## API

- [Auth](#auth)
- [Products](#products)
- [Profiles](#profiles)
- [Experts](#experts)
- [Tickets](#tickets)
- [Chats](#chats)

### Auth

- POST `/API/auth/signup`

    - Description: Allows to create a client account
    - Request body: email, password, name, surname, phoneNumber, address, city and country of the new client

      ```
      {
        "email": "johngreen@group.com",
        "password": "YouWontGuessThisOne",
        "name": "John",
        "surname": "Green",
        "phoneNumber": "3466281644",
        "address": "Corso Duca degli Abruzzi, 24",
        "city": "Turin",
        "country": "Italy"
      }
      ```

    - Response: `201 Created` (success)
    - Error responses: `409 Conflict` (email already exists or phoneNumber already exists), `422 Unprocessable Entity`(
      validation of request body failed) or `500 Internal Server Error` (generic error)
    - Response body: An error message in case of error

      ```
      {
        "error": "a client with the same email already exists"
      }
      ```

- POST `/API/auth/login`
    - Description: Allows the user to authenticate
    - Request body: username (i.e., email) and password

      ```
      {
        "username": "expert1@wa2.it",
        "password": "password"
      }
      ```

    - Response: `200 Ok` (success)
    - Error responses: `401 Unauthorized` (invalid username and/or password),`422 Unprocessable Entity` (validation of
      request body failed) or `500 Internal Server Error` (generic error)
    - Response body: An object containing the access_token. An error message in case of error
      ```
      {
        "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJTdEl1VDdvRGJ5cDc2SFg5QmlfdzZIUUk0SVZ5TXhfZ2g2WmpGT0ItT0RrIn0.eyJleHAiOjE2ODM4ODcyMzcsImlhdCI6MTY4Mzg4N"
      }
      ```

- GET `/API/auth/user`
    - Description: Allows to retrieve information about the authenticated user
    - Request body: _None_
    - Response: `200 Ok` (success)
    - Error responses: `401 Unauthorized` (not logged in) or `500 Internal Server Error` (generic error)
    - Response body: An object containing main user information (the id is null for managers). An error message in case
      of error
      ```
      {
        "id": 5,
        "email": "bbowering4@vistaprint.com",
        "name": "Barr Bowering",
        "role": "Expert"
      }
      ```

### Products

- GET `/API/products`

    - Description: Allows to obtain all products within the system
    - Permissions allowed:
        - authenticated users
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `500 Internal Server Error` (generic
      error)
    - Response body: An array of objects, for each containing productId, asin, brand, category,
      manufacturerNumber, name, price and weight (expressed in kg). An error message in case of error

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
    - Permissions allowed:
        - authenticated users
    - Request parameter: productId of the requested product
    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (productId not
      found), `422 Unprocessable Entity` (validation of productId failed) or `500 Internal Server Error` (generic error)
    - Response body: An object containing productId, asin, brand, category, manufacturerNumber, name, price and
      weight (expressed in kg) of the requested product. An error message in case of error

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

- POST `API/products/{productId}/token`

    - Description: Allows to generate a token that represents the proof of a product purchase
    - Permissions allowed:
        - Managers
    - Request body: _None_
    - Response: `201 Created` (success)
    - Error responses: `401 Unauthorized` (not logged in), `404 Not Found` (productId not
      found), `422 Unprocessable Entity` (validation of productId failed) or `500 Internal Server Error` (generic error)
    - Response body: The token generated. An error message in case of error

      ```
      {
          "token": "7305dba7-6635-4931-8463-4c1872fb9f3d"
      }
      ```

- POST `API/products/register`

    - Description: Allows to register a purchased product for the logged in customer
    - Permissions allowed:
        - Customer
    - Request body: token of the purchase
    ```
    {
        "token": "7305dba7-6635-4931-8463-4c1872fb9f3d"
    }
    ```
    - Response: `200 Ok` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (token not
      found),`409 Conflict` (token already used), `422 Unprocessable Entity` (validation of
      request body failed) or `500 Internal Server Error` (generic error)
    - Response body: An error message in case of error
      ```
      {
          "error": "Token already used"
      }
      ```

### Profiles

- GET `/API/profiles/{profileId}`

    - Description: Allows to obtain all the information of a single profile
    - Permissions allowed:
        - The Client associated with the specified identifier
        - Experts who have a not closed ticket with the client
        - Managers
    - Request parameter: profile identifier of the requested user profile
    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (profileId not
      found), `422 Unprocessable Entity` (validation of profileId failed) or `500 Internal Server Error` (generic error)
    - Response body: An object containing profileId, email, name, surname, phoneNumber, address, city, country of the
      requested user. An error message in case of error

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

- GET `/API/profiles/{profileId}/tickets`

    - Description: Allows to obtain all the tickets of a single profile
    - Permissions allowed:
        - The Client associated with the specified profileId
        - Managers
    - Request parameter: profileId of the requested user profile
    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (profileId not
      found), `422 Unprocessable Entity` (validation of profileId failed) or
      `500 Internal Server Error` (generic error)
    - Response body: An object containing all the tickets of the
      requested user. An error message in case of error

    ```
      [
          ...,
          {
            "ticketId": 5,
            "title": "Smoke coming out of TV",
            "description": "I was watching TV when I noticed smoke coming out of the back. I immediately unplugged it, but now I'm afraid to turn it back on.",
            "productId": 1,
            "productTokenId": 1,
            "customerId": 1,
            "expertId": 1,
            "totalExchangedMessages": 32,
            "status": "IN PROGRESS",
            "priorityLevel": "CRITICAL",
            "createdAt": 1682087627,
            "lastModifiedAt": 1682091233
          },
          {
            "ticketId": 6,
            "title": "Broken screen",
            "description": "My computer fell and the screen is shattered",
            "productId": 2,
            "productTokenId": 11,
            "customerId": 1,
            "expertId": null,
            "totalExchangedMessages": 0,
            "status": "OPEN",
            "priorityLevel": null,
            "createdAt": 1682087637,
            "lastModifiedAt": 1682087637
          },
          ...
      ]
      

- GET `/API/profiles/{profileId}/products`

    - Description: Allows to obtain all the purchased products data of a single profile
    - Permissions allowed:
        - The Client associated with the specified profileId
        - Managers
    - Request parameter: profileId of the requested user profile
    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (profileId not
      found), `422 Unprocessable Entity` (validation of profileId failed) or
      `500 Internal Server Error` (generic error)
    - Response body: An object containing all the purchased products data of the
      requested user. An error message in case of error

    ```
      [
          ...,
          {
            "productTokenId": 5,
            "createdAt": 1682087627,
            "registeredAt": 1682087727,
            "token": "7305dba7-6635-4931-8463-4c1872fb9f3d",
            "userId": 1,
            "product": { 
                          "productId": 61,
                          "asin": "B06XSGYCHC",
                          "brand": "Siriusxm",
                          "category": "Satellite Radio",
                          "manufacturerNumber": "SXEZR1V1",
                          "name": "SiriusXM SXEZR1V1 XM Onyx EZR Satellite Radio Receiver with Vehicle Kit",
                          "price": 79.99,
                          "weight": 1.25
                          "expertise":"COMPUTER",
                          "level":"SKILLED"
                        }
          },
           ...
      ]
      
  
- GET `/API/profiles/{profileId}/products/{productTokenId}`

    - Description: Allows to obtain a purchased product of a single profile
    - Permissions allowed:
        - The Client associated with the specified profileId
        - Managers
    - Request parameter: profileId of the requested user profile, productTokenId
    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (profileId or productTokenId not
      found), `422 Unprocessable Entity` (validation of profileId failed) or
      `500 Internal Server Error` (generic error)
    - Response body: An object containing the purchased product of the
      requested user. An error message in case of error

    ```
          {
            "productTokenId": 5,
            "createdAt": 1682087627,
            "registeredAt": 1682087727,
            "token": "7305dba7-6635-4931-8463-4c1872fb9f3d",
            "userId": 1,
            "product": { 
                          "productId": 61,
                          "asin": "B06XSGYCHC",
                          "brand": "Siriusxm",
                          "category": "Satellite Radio",
                          "manufacturerNumber": "SXEZR1V1",
                          "name": "SiriusXM SXEZR1V1 XM Onyx EZR Satellite Radio Receiver with Vehicle Kit",
                          "price": 79.99,
                          "weight": 1.25
                          "expertise":"COMPUTER",
                          "level":"SKILLED"
                        }
          }


- PUT `/API/profiles`

    - Description: Allows to update information of an existing profile
    - Permissions allowed:
        - Clients, each only with their own profile
    - Request body: name, surname, phoneNumber, address, city and country of the profile to be updated

      ```
      {
        "name": "John",
        "surname": "Green",
        "phoneNumber": "3163122442",
        "address": "Corso Einaudi, 16",
        "city": "Turin",
        "country": "Italy"
      }
      ```

    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `409 Conflict` (phoneNumber already
      exists), `422 Unprocessable Entity` (validation of request body failed) or `500 Internal Server Error` (
      generic error)
    - Response body: An error message in case of error

      ```
      {
        "error": "a profile with the same phoneNumber already exists"
      }
      ```

### Experts

- GET `/API/experts`

    - Description: Allows to obtain all experts within the system
    - Permissions allowed:
        - Managers
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `500 Internal Server Error` (generic
      error)
    - Response body: An array of objects, for each containing expertId, name, surname, email and skills (array of
      objects, for each containing expertise and level). An error message in case of error

      ```
      [
          ...,
          {
            "expertId": 5,
            "name": "Gino",
            "surname": "Fastdio",
            "email": "gino.fastidio@expert.org",
            "country": "Italy",
            "city": "Naples",
            "skills": [
                        ..., 
                        { 
                          "expertise":"COMPUTER",
                          "level":"SKILLED"
                        }, 
                        ...
                      ]
          },
          ...
      ]
      ```

- GET `/API/experts/{expertId}`

    - Description: Allows to obtain all the information of an expert
    - Permissions allowed:
        - The Expert associated with the specified expertId
        - Managers
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (expertId not
      found), `422 Unprocessable Entity` (validation of expertId failed) or `500 Internal Server Error` (generic error)
    - Response body: An object containing expertId, name, surname, email and skills (array of objects, for each
      containing expertise and level). An error message in case of error

      ```
      {
        "expertId": 4,
        "name": "Ambra",
        "surname": "Grigia",
        "email": "a.grigia@expert.org",
        "country": "Italy",
        "city": "Turin",
        "skills": [
                    ..., 
                    {
                      "expertise":"APPLIANCES",
                      "level":"SPECIALIST"
                    }, 
                    ...
                  ]
      }
      ```

- GET `/API/experts/{expertId}/statusChanges`

    - Description: Allows to obtain a list of all status changes made by a specific expert
    - Permissions allowed:
        - The Expert associated with the specified expertId
        - Managers
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (expertId not
      found), `422 Unprocessable Entity` (validation of expertId
      failed) or `500 Internal Server Error` (generic error)
    - Response body: An array of objects, sorted by time descending, for each containing ticketId, currentExpertId,
      oldStatus, newStatus, changedBy, description and time. An error message in case of error
    ```
    [
        ...,
        {
          "ticketId": 49,
          "currentExpertId": 25,
          "oldStatus": "RESOLVED",
          "newStatus": "CLOSED",
          "changedBy":"EXPERT",
          "description":"",
          "time": 1682092244
        }
        ...
    ]
    ```

- GET `/API/experts/{expertId}/tickets`

    - Description: Allows to obtain the list of tickets assigned to an expert
    - Permissions allowed:
        - The Expert associated with the specified expertId
        - Managers
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (expertId not
      found), `422 Unprocessable Entity` (validation of expertId
      failed) or `500 Internal Server Error` (generic error)
    - Response body: An array of objects (sorted by priorityLevel descending) for each containing
      ticketId, description, productId, productTokenId, customerId, expertId, totalExchangedMessages, status, priorityLevel, createdAt
      and lastModifiedAt.
      An error message in case of error
      ```
      [
          ...,
          {
            "ticketId": 21,
            "title": "Microwave not heating food",
            "description": "I've tried using my microwave multiple times, but it's not heating up my food. The light turns on and the plate rotates, but the food stays cold.",
            "productId": 2, 
            "productTokenId": 1,
            "customerId": 3,
            "expertId": 2,
            "totalExchangedMessages": 32,
            "status": "RESOLVED",
            "priorityLevel": "MEDIUM",
            "createdAt": 1682092233,
            "lastModifiedAt": 168211444
          },
          ...
      ]
      ```

- POST `API/experts/createExpert`

    - Description: Allows to create an expert account
    - Permissions allowed:
        - Managers
    - Request body: email, password, name, surname, country, city and skills of the new expert
    ```
    {
        "email": "a.grigia@expert.org",
        "password": "Zw86F442KgsZwb1r",
        "name": "Ambra",
        "surname": "Grigia",
        "country": "Italy",
        "city": "Turin",
        "skills": [
                    ..., 
                    {
                      "expertise":"APPLIANCES",
                      "level":"SPECIALIST"
                    }, 
                    ...
                  ]
    }
    ```
    - Response: `201 Created` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `409 Conflict` (email already
      exists), `422 Unprocessable Entity` (validation of
      request body failed) or `500 Internal Server Error` (generic error)
    - Response body: An error message in case of error

      ```
      {
          "error": "an expert with the same email already exists"
      }
      ```

### Tickets

- GET `/API/tickets`

    - Description: Allows to obtain all the tickets in the system, optionally filtered by status
    - Permissions allowed:
        - Managers
    - Request body: _None_
    - Query parameters:

        - `status`: Optional parameter that specifies the status of the tickets.

      ```
          GET /API/tickets?status=OPEN
      ```

    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `422 Unprocessable Entity` (
      validation of status failed) or `500 Internal Server Error` (generic
      error)
    - Response body: An array of objects, for each containing ticketId, title, description, productId, productTokenId, customerId,
      expertId, totalExchangedMessages, status, priorityLevel, createdAt and lastModifiedAt.
      An error message in case of error
      ```
      [
          ...,
          {
            "ticketId": 5,
            "title": "Smoke coming out of TV",
            "description": "I was watching TV when I noticed smoke coming out of the back. I immediately unplugged it, but now I'm afraid to turn it back on.",
            "productId": 1,
            "productTokenId": 1,
            "customerId": 1,
            "expertId": 1,
            "totalExchangedMessages": 32,
            "status": "IN PROGRESS",
            "priorityLevel": "CRITICAL",
            "createdAt": 1682087627,
            "lastModifiedAt": 1682091233
          },
          {
            "ticketId": 6,
            "title": "Broken screen",
            "description": "My computer fell and the screen is shattered",
            "productId": 2,
            "productTokenId": 12,
            "customerId": 2,
            "expertId": null,
            "totalExchangedMessages": 0,
            "status": "OPEN",
            "priorityLevel": null,
            "createdAt": 1682087637,
            "lastModifiedAt": 1682087637
          },
          ...
      ]
      ```

- GET `/API/tickets/{ticketId}`

    - Description: Allows to obtain all the information of a ticket
    - Permissions allowed:
        - The Client associated with the ticketId
        - The Expert associated with the ticketId
        - Managers
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (ticketId not
      found), `422 Unprocessable Entity` (validation of
      ticketId failed) or `500 Internal Server Error` (generic error)
    - Response body: An object containing ticketId, description, productId, productTokenId, customerId,
      expertId, totalExchangedMessages, status, priorityLevel, createdAt and lastModifiedAt.
      An error message in case of error
      ```
      {
         "ticketId": 7,
         "title": "Bluetooth earphones not connecting",
         "description": "I recently purchased a pair of Bluetooth earphones, but I'm having trouble connecting them to my phone.
                         I've tried resetting the earphones and my phone's Bluetooth settings, but nothing seems to work.",
         "productId": 4,
         "productTokenId": 4,
         "customerId": 4,
         "expertId": null,
         "totalExchangedMessages": 32,
         "status": "REOPENED",
         "priorityLevel": "LOW",
         "createdAt": 1682087627,
         "lastModifiedAt": 1682101234
      }
      ```

- GET `/API/tickets/{ticketId}/statusChanges`

    - Description: Allows to obtain a list of all status changes for a specific ticket
    - Permissions allowed:
        - The Client associated with the ticketId
        - The Expert associated with the ticketId
        - Managers
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (ticketId not
      found), `422 Unprocessable Entity` (validation of
      ticketId failed) or `500 Internal Server Error` (generic error)
    - Response body: An array of objects, sorted by time descending, for each containing ticketId, currentExpertId,
      oldStatus, newStatus, description, changedBy and time. An error message in case of error
      ```
      [
          ...,
          {
            "ticketId": 49,
            "currentExpertId": 25,
            "oldStatus": "RESOLVED",
            "newStatus": "CLOSED",
            "changedBy":"EXPERT",
            "description":"",
            "time": 1682092244
          },
          ...,
          {
            "ticketId": 49,
            "currentExpertId": 25,
            "oldStatus": "RESOLVED",
            "newStatus": "REOPENED",
            "changedBy":"CUSTOMER",
            "description":"Issue has not been solved",
            "time": 1482293244
          }

          ...
      ]
      ```

- POST `/API/tickets`

    - Description: Allows to create a ticket for a purchased product
    - Permissions allowed:
        - Clients
    - Request body: productTokenId of the related purchase with issues and a
      title and description of the issue

      ```
      {
        "productTokenId": 1,
        "title": "No sound from TV speakers",
        "description": "I've checked all the connections and tried adjusting the settings, but I still can't hear any sound from the TV speakers."
      }
      ```

    - Response: `201 Created` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (productTokenId not
      found), `409 Conflict` (a not closed ticket for the same customer and productToken already
      exists), `422 Unprocessable Entity` (validation of request body failed or customer does not own that productTokenId) or `500 Internal Server Error` (generic
      error)
    - Response body: An object containing the id of the ticket created. An error message in case of error
      ```
      {
        "ticketId": 5
      }
      ```

- PUT `/API/tickets/{ticketId}/start`

    - Description: Allows to start the progress of an "OPEN"/"REOPENED" ticket by assigning it to an expert and setting
      a priority level. Upon successful completion of the request, the ticket status will be "IN_PROGRESS"
    - Permissions allowed:
        - Managers
    - Request body: the id of the assigned expert, the priority level of the ticket and an optional description.

      ```
      {
        "expertId": 1,
        "priorityLevel": "LOW"
        "description": ""
      }
      ```

    - Response: `204 No Content` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (ticketId or
      expertId not found), `422 Unprocessable Entity` (validation of
      request body or ticketId failed or tried to start a not open/reopened ticket)
      or `500 Internal Server Error` (generic error)
    - Response body: An error message in case of error

      ```
      {
        "error": "Could not start the ticket with id 1 because its current status is 'IN_PROGRESS'"
      }
      ```

- PUT `/API/tickets/{ticketId}/stop`

    - Description: Allows to stop an "IN_PROGRESS" ticket. Upon successful completion of the request, the ticket status
      will be "OPEN"
    - Permissions allowed:
        - The Expert associated with the ticketId
        - Managers
    - Request body: an optional description.

      ```
      {
        "description": "I'm not able to solve this issue"
      }
      ```

    - Response: `204 No Content` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (ticketId not
      found), `422 Unprocessable Entity` (validation of
      request body or ticketId failed or ticket is not in progress) or `500 Internal Server Error` (generic error)
    - Response body: An error message in case of error

      ```
      {
        "error": "Could not stop the ticket with id 1 because its current status is 'CLOSED'"
      }
      ```

- PUT `/API/tickets/{ticketId}/resolve`

    - Description: Allows to set a not closed ticket as resolved. Upon successful completion of the request, the ticket
      status will be "RESOLVED"
    - Permissions allowed:
        - The Client associated with the ticketId
        - The Expert associated with the ticketId
        - Managers
    - Request body: an optional description.

      ```
      {
        "description": ""
      }
      ```

    - Response: `204 No Content` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (ticketId not
      found), `422 Unprocessable Entity` (validation of request body or
      ticketId failed
      or ticket is already resolved or ticket is closed) or `500 Internal Server Error` (generic error)
    - Response body: An error message in case of error

      ```
      {
        "error": "Could not resolve the ticket with id 1 because its current status is 'CLOSED'"
      }
      ```

- PUT `/API/tickets/{ticketId}/reopen`

    - Description: Allows to reopen a closed/resolved ticket. Upon successful completion of the request, the ticket
      status will be "REOPENED"
    - Permissions allowed:
        - The Client associated with the ticketId
    - Request body: description.

      ```
      {
        "description": "I encountered the same issue again after the ticket was closed."
      }
      ```

    - Response: `204 No Content` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (ticketId not
      found), `422 Unprocessable Entity` (validation of request body or
      ticketId failed, `409 Conflict` (a not closed ticket for the same customer and productToken already
      exists),
      or ticket is not closed/resolved) or `500 Internal Server Error` (generic error)
    - Response body: An error message in case of error

      ```
      {
        "error": "Could not reopen the ticket with id 1 because its current status is 'IN_PROGRESS'"
      }
      ```

- PUT `/API/tickets/{ticketId}/close`

    - Description: Allows to close any ticket. Upon successful completion of the request, the ticket status will be "
      CLOSED"
    - Permissions allowed:
        - The Expert associated with the ticketId
        - Managers
    - Request body: an optional description.

      ```
      {
        "description": ""
      }
      ```   

    - Response: `204 No Content` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (ticketId not
      found), `422 Unprocessable Entity` (validation of ticketId failed
      or ticket is already closed) or `500 Internal Server Error` (generic error)
    - Response body: An error message in case of error

      ```
      {
        "error": "Could not close the ticket with id 1 because its current status is 'CLOSED'"
      }
      ```

### Chats

- GET `/API/chats/{ticketId}/messages`

    - Description: Allows to obtain all chat messages of a ticket
    - Permissions allowed:
        - The Client associated with the ticketId
        - The Expert associated with the ticketId
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (ticketId not
      found), `422 Unprocessable Entity` (validation of
      ticketId) or `500 Internal Server Error` (generic error)
    - Response body: An array of objects, ordered chronologically, for each containing messageId, sender, expertId (
      expert to whom the ticket was assigned),
      content,
      attachments (array of objects, for each containing attachmentId, name and type) and time. An error message in case
      of error
      ```
      [
          ...,
          {
            "messageId": 16,
            "sender": "CUSTOMER",
            "expertId": 25
            "content": "Hi, I'm having trouble with my device. Can you help me?",
            "attachments": [
                             ...,
                             {
                               "attachmentId": 10,
                               "name": "my_broken_device.jpg",
                               "type": "IMAGE"
                             },
                             ...
                           ],
            "time": 1682091233
          },
          {
            "messageId": 19,
            "sender": "EXPERT",
            "expertId": 25,
            "content": "Sure, I'd be happy to help. What seems to be the problem?",
            "attachments": [],
            "time": 1682095555
          },
          ...
      ]
      ```

- POST `/API/chats/{ticketId}/messages`

    - Description: Allows to send a message and optionally some attachments
    - Permissions allowed:
        - The Client associated with the ticketId
        - The Expert associated with the ticketId
    - Headers: {"Content-Type" : "multipart/form-data"}
    - Request Parameters:
        - content(required) - the content of the message.
    - Request Parts:
        - attachments(optional) - the files to be attached to the message.
    - Response: `201 Created` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (ticketId not
      found), `422 Unprocessable Entity` (validation of request
      body or ticketId failed or ticket is not in progress/resolve) or `500 Internal Server Error` (generic error)
    - Response body: The id of the message sent. An error message in case of error
      ```
      {
        "messageId": 88
      }
      ```

      ```
      {
        "error": "impossible to send the message as the chat is inactive"
      }
      ```

- GET `/API/chats/{ticketId}/messages/{messageId}/attachments/{attachmentId}`

    - Description: Allows to download an attachment related to a message
    - Permissions allowed:
        - The Client associated with the ticketId
        - The Expert associated with the ticketId
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `401 Unauthorized` (not logged in or missing permission(s)), `404 Not Found` (ticketId, messageId
      or attachmentId not found), `422 Unprocessable Entity` (
      validation of ticketId, messageId or attachmentId failed) or `500 Internal Server Error` (generic error)
    - Response body: The binary contents of the attachment file. An error message in case of error
      ```
      {
        "error": "validation of request failed"
      }
      ```
