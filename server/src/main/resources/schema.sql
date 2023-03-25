CREATE TABLE IF NOT EXISTS products
(
    product_id          SERIAL PRIMARY KEY,
    asin                VARCHAR(10)    NOT NULL,
    brand               VARCHAR(255)   NOT NULL,
    category            VARCHAR(255)   NOT NULL,
    manufacturer_number VARCHAR(30)    NOT NULL,
    name                VARCHAR(255)   NOT NULL,
    price               NUMERIC(10, 2) NOT NULL,
    weight              NUMERIC(8, 2)  NOT NULL
);