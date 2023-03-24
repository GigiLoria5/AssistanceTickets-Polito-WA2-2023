create table if not exists products (
    ean varchar(15) primary key,
    name varchar(255),
    brand varchar(255)
);