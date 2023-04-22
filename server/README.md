# Server

## API

- [Products](#products)
- [Profiles](#profiles)
- [Experts](#experts)
- [Tickets](#tickets)
- [Chats](#chats)

### Products

- GET `/API/products/`

    - Description: Allows to obtain all products within the system
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `500 Internal Server Error` (generic error)
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
    - Request parameter: productId of the requested product
    - Response: `200 OK` (success)
    - Error responses: `404 Not Found` (productId not found), `422 Unprocessable Entity` (validation of productId
      failed) or `500 Internal Server Error` (generic error)
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

### Profiles

- GET `/API/profiles/{email}`

    - Description: Allows to obtain all the information of a single profile
    - Request parameter: email of the requested user profile
    - Response: `200 OK` (success)
    - Error responses: `404 Not Found` (email not found), `422 Unprocessable Entity` (validation of email failed) or
      `500 Internal Server Error` (generic error)
    - Response body: An object containing profileId, email, name, surname, phoneNumber, address, city and country of the
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
        "error": "a profile with the same email already exists"
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
        "error": "a profile with the same phoneNumber already exists"
      }
      ```

### Experts

- GET `/API/experts/`

    - Description: Allows to obtain all experts within the system
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `500 Internal Server Error` (generic error)
    - Response body: An array of objects, for each containing expertId, name, surname, email and expertises. An error
      message in case of error

      ```
      [
          ...,
          {
            "expertId": 5,
            "name": "Gino",
            "surname": "Fastdio",
            "email": "gino.fastidio@expert.org",
            "expertises": [..., "COMPUTER", ...]
          },
          ...
      ]
      ```

- GET `/API/experts/{expertId}`

    - Description: Allows to obtain all the information of an expert
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `404 Not Found` (expertId not found), `422 Unprocessable Entity` (validation of expertId
      failed) or `500 Internal Server Error` (generic error)
    - Response body: An object containing expertId, name, surname, email and expertises. An error message in case of
      error

      ```
      {
        "expertId": 5,
        "name": "Gino",
        "surname": "Fastdio",
        "email": "gino.fastidio@expert.org",
        "expertises": [..., "COMPUTER", ...]
      }
      ```

- GET `/API/experts/{expertId}/statusChanges`

    - Description: Allows to obtain a list of all status changes made by a specific expert
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `404 Not Found` (ticketId not found), `422 Unprocessable Entity` (validation of
      ticketId failed) or `500 Internal Server Error` (generic error)
    - Response body:
    ```
    [
        ...,
        {
          "ticketId": 49,
          "previousState": "RESOLVED",
          "newState": "CLOSED",
          "expertId": 25,
          "time": 1682092244
        }
        ...
    ]
    ```

### Tickets

- GET `/API/tickets`

    - Description: Allows to obtain all the tickets in the system, optionally filtered by status
    - Request body: _None_
    - Query parameters:

        - `status`: Optional parameter that specifies the status of the tickets.

      ```
          GET /API/tickets?status=OPEN
      ```

    - Response: `200 OK` (success)
    - Error responses: `422 Unprocessable Entity` (validation of status failed) or `500 Internal Server Error` (generic
      error)
    - Response body: An array of objects, for each containing ticketId, description, productId, customerId,
      expertId, chats (array of chat ids), status, priorityLevel, createdAt and lastModifiedAt.
      An error message in case of error
      ```
      [
          ...,
          {
            "ticketId": 5,
            "title": "Smoke coming out of TV",
            "description": "I was watching TV when I noticed smoke coming out of the back. I immediately unplugged it, but now I'm afraid to turn it back on.",
            "productId": 1,
            "customerId": 1,
            "expertId": 1,
            "chat": 27,
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
            "customerId": 2,
            "expertId": null,
            "chat": null,
            "status": "OPEN",
            "priorityLevel": null,
            "createdAt": 1682087637,
            "lastModifiedAt": 1682087637
          },
          ...
      ]
      ```

- GET `/API/tickets/{expertId}`

    - Description: Allows to obtain the list of tickets assigned to an expert
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `404 Not Found` (expertId not found), `422 Unprocessable Entity` (validation of
      expertId failed) or `500 Internal Server Error` (generic error)
    - Response body: An array of objects, for each containing ticketId, description, productId, customerId,
      expertId, chats (array of chat ids), status, priorityLevel, createdAt and lastModifiedAt.
      An error message in case of error
      ```
      [
          ...,
          {
            "ticketId": 21,
            "title": "Microwave not heating food",
            "description": "I've tried using my microwave multiple times, but it's not heating up my food. The light turns on and the plate rotates, but the food stays cold.",
            "productId": 2,
            "customerId": 3,
            "expertId": 2,
            "chat": 25,
            "status": "RESOLVED",
            "priorityLevel": "MEDIUM",
            "createdAt": 1682092233,
            "lastModifiedAt": 168211444
          }
          ...
      ]
      ```

- GET `/API/tickets/{ticketId}`

    - Description: Allows to obtain all the information of a ticket
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `404 Not Found` (ticketId not found), `422 Unprocessable Entity` (validation of
      ticketId failed) or `500 Internal Server Error` (generic error)
    - Response body: An object containing ticketId, description, productId, customerId,
      expertId, chats (array of chat ids), status, priorityLevel, createdAt and lastModifiedAt.
      An error message in case of error
      ```
      {
         "ticketId": 7,
         "title": "Bluetooth earphones not connecting",
         "description": "I recently purchased a pair of Bluetooth earphones, but I'm having trouble connecting them to my phone.
                         I've tried resetting the earphones and my phone's Bluetooth settings, but nothing seems to work.",
         "productId": 4,
         "customerId": 4,
         "expertId": null,
         "chat": 37,
         "status": "REOPENED",
         "priorityLevel": "LOW",
         "createdAt": 1682087627,
         "lastModifiedAt": 1682101234
      }
      ```

- GET `/API/tickets/{ticketId}/statusChanges`

    - Description: Allows to obtain a list of all status changes for a specific ticket
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `404 Not Found` (ticketId not found), `422 Unprocessable Entity` (validation of
      ticketId failed) or `500 Internal Server Error` (generic error)
    - Response body:
      ```
      [
          ...,
          {
            "ticketId": 50,
            "previousState": "IN PROGRESS",
            "newState": "RESOLVED",
            "expertId": 2,
            "time": 1682092233
          }
          ...
      ]
      ```

- POST `/API/tickets`

    - Description: Allows to create a ticket
    - Request body: customerId of the customer who opened the ticket, productId of the related product with issues and a
      title and description of the issue

      ```
      {
        "customerId": 1,
        "productId": 1,
        "title": "No sound from TV speakers",
        "description": "I've checked all the connections and tried adjusting the settings, but I still can't hear any sound from the TV speakers."
      }
      ```

    - Response: `201 Created` (success)
    - Error responses: `404 Not Found` (customerId or productId not found), `409 Conflict` (a not closed (???)
      ticket for the same customer and product already exists), `422 Unprocessable Entity` (validation of
      request body failed) or `500 Internal Server Error` (generic error)
    - Response body: The id of the ticket created. An error message in case of error
      ```
      {
        "ticketId": 5
      }
      ```

- PUT `/API/tickets/{ticketId}/start`

    - Description: Allows to start the progress of an open/reopened ticket by assigning it to an expert and setting a
      priority level
    - Request body:

      ```
      {
        "expertId": 1,
        "priorityLevel": LOW
      }
      ```

    - Response: `204 No Content` (success)
    - Error responses: `404 Not Found` (ticketId or expertId not found), `422 Unprocessable Entity` (validation of
      request body or ticketId failed or tried to start a not open/reopened ticket)
      or `500 Internal Server Error` (generic error)
    - Response body: An error message in case of error

      ```
      {
        "error": "could not start the progress of the ticket because it is resolved"
      }
      ```

- PUT `/API/tickets/{ticketId}/stop`

    - Description: Allows to stop an in progress ticket
    - Request body: _None_
    - Response: `204 No Content` (success)
    - Error responses: `404 Not Found` (ticketId not found), `422 Unprocessable Entity` (validation of ticketId failed
      or ticket is not in progress) or `500 Internal Server Error` (generic error)
    - Response body: An error message in case of error

      ```
      {
        "error": "impossible to stop the progress of ticket with id 21 because it is closed"
      }
      ```

- PUT `/API/tickets/{ticketId}/resolve`

    - Description: Allows to set a not closed ticket as resolved
    - Request body: _None_
    - Response: `204 No Content` (success)
    - Error responses: `404 Not Found` (ticketId not found), `422 Unprocessable Entity` (validation of ticketId failed
      or ticket is already resolved or ticket is closed) or `500 Internal Server Error` (generic error)
    - Response body: An error message in case of error

      ```
      {
        "error": "impossible set ticket with id 55 as resolved because it is closed"
      }
      ```

- PUT `/API/tickets/{ticketId}/reopen`

    - Description: Allows to reopen a closed/resolved ticket
    - Request body: an optional reason to provide a reason for reopening the ticket.

      ```
      {
        "reason": "I encountered the same issue again after the ticket was closed."
      }
      ```

    - Response: `204 No Content` (success)
    - Error responses: `404 Not Found` (ticketId not found), `422 Unprocessable Entity` (validation of ticketId failed
      or ticket is not closed/resolved) or `500 Internal Server Error` (generic error)
    - Response body: An error message in case of error

      ```
      {
        "error": "impossible to reopen ticket with id 5 because it is in progress"
      }
      ```

- PUT `/API/tickets/{ticketId}/close`

    - Description: Allows to close any ticket
    - Request body: _None_
    - Response: `204 No Content` (success)
    - Error responses: `404 Not Found` (ticketId not found), `422 Unprocessable Entity` (validation of ticketId failed
      or ticket is already closed) or `500 Internal Server Error` (generic error)
    - Response body: An error message in case of error

      ```
      {
        "error": "impossible to close ticket with id 55 because it is already closed"
      }
      ```

### Chats

- GET `/API/chats/{chatId}/messages`

    - Description: Allows to obtain all the messages of a chat
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `404 Not Found` (ticketId or chatId not found), `422 Unprocessable Entity` (validation of
      ticketId or chatId failed) or `500 Internal Server Error` (generic error)
    - Response body:
      ```
      [
          ...,
          {
            "messageId": 16,
            "sender": "CUSTOMER",
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

- GET `/API/chats/{chatId}/messages/{messageId}/attachments/{attachmentId}`

    - Description: Allows to download an attachment related to a message
    - Request body: _None_
    - Response: `200 OK` (success)
    - Error responses: `404 Not Found` (chatId, messageId or attachmentId not found), `422 Unprocessable Entity` (
      validation of chatId, messageId or attachmentId failed) or `500 Internal Server Error` (generic error)
    - Response body: The binary contents of the attachment file. An error message in case of error
      ```
      {
        "error": "validation of request failed"
      }
      ```

- POST `/API/chats`

    - Description: Allows to start a chat relating to an in progress or resolved ticket and to send the first message
    - Request body: The ticketId of the ticket with which the chat is to be associated, a sender field specifying who
      between CUSTOMER and EXPERT initiated the chat and the message to be sent

      ```
      {
        "ticketId": 1,
        "sender": "CUSTOMER",
        "message" "Hi there, I'm having trouble connecting my new wireless headphones to my laptop. 
                   I've followed the instructions in the manual but it's still not working. Can you please help me troubleshoot?"
      }
      ```

    - Response: `201 Created` (success)
    - Error responses: `404 Not Found` (ticketId not found), `409 Conflict` (the ticket already has a
      chat), `422 Unprocessable Entity` (validation of request body failed or ticket is not in progress/resolved)
      or `500 Internal Server Error` (generic error)
    - Response body: The id of the chat created. An error message in case of error
      ```
      {
        "chatId": 27
      }
      ```

- POST `/API/tickets/{ticketId}/chats/{chatId}`

    - Description: Allows to send a message and optionally some attachments
    - Headers: {"Content-Type" : "multipart/form-data"}
    - Request body: a sender field specifying who, between the CUSTOMER and the EXPERT, is sending the message, and the
      message to be sent

      ```
      {
        "sender": "EXPERT",
        "message" "Hi there, I'm sorry to hear that the solution didn't work for you. Let's try some additional troubleshooting steps.
                   Can you please confirm if the headphones are visible in the list of available Bluetooth devices on your laptop?"
      }
      ```

    - Response: `201 Created` (success)
    - Error responses: `404 Not Found` (ticketId or chatId not found), `422 Unprocessable Entity` (validation of request
      body failed or chat is inactive) or `500 Internal Server Error` (generic error)
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