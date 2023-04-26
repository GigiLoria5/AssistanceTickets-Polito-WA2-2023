CREATE TABLE IF NOT EXISTS products
(
    product_id          SERIAL PRIMARY KEY,
    asin                VARCHAR(10) UNIQUE NOT NULL,
    brand               VARCHAR(255)       NOT NULL,
    category            VARCHAR(255)       NOT NULL,
    manufacturer_number VARCHAR(30) UNIQUE NOT NULL,
    name                VARCHAR(255)       NOT NULL,
    price               FLOAT4             NOT NULL,
    weight              FLOAT4             NOT NULL
);

CREATE TABLE IF NOT EXISTS profiles
(
    profile_id   SERIAL PRIMARY KEY,
    email        VARCHAR(255) UNIQUE NOT NULL,
    name         VARCHAR(255)        NOT NULL,
    surname      VARCHAR(255)        NOT NULL,
    phone_number VARCHAR(15)         NOT NULL,
    address      VARCHAR(255)        NOT NULL,
    city         VARCHAR(255)        NOT NULL,
    country      VARCHAR(255)        NOT NULL
);