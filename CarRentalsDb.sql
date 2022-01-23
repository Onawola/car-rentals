DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS car;
DROP TABLE IF EXISTS brand;
DROP TABLE IF EXISTS car_brand;
DROP TABLE IF EXISTS cust_car;

CREATE TABLE customer (
    customerid integer DEFAULT 0 NOT NULL,
    l_name character(15) NOT NULL,
    f_name character(15),
    city character(15),
    CONSTRAINT customer_city CHECK ((((city = 'Lagos'::bpchar) OR (city = 'Abuja'::bpchar)) OR (city = 'Oyo'::bpchar) OR (city = 'Taraba'::bpchar))),
    CONSTRAINT customer_customerid CHECK ((customerid >= 0))
);

CREATE TABLE car (
    plate_number integer DEFAULT 0 NOT NULL,
    title character(60) NOT NULL,
    edition_no smallint DEFAULT 1,
    numofcop smallint DEFAULT 1 NOT NULL,
    numleft smallint DEFAULT 1 NOT NULL,
    CONSTRAINT car_edition_no CHECK ((edition_no > 0)),
    CONSTRAINT car_plate_number CHECK ((plate_number >= 0))
);

CREATE TABLE brand (
    brandid integer DEFAULT 0 NOT NULL,
    name character(15),
    surname character(15) NOT NULL,
    CONSTRAINT car_brandid CHECK ((brandid >= 0))
);

CREATE TABLE car_brand (
    plate_number integer DEFAULT 0 NOT NULL,
    brandid integer DEFAULT 0 NOT NULL,
    brandseqno smallint DEFAULT 1,
    CONSTRAINT car_brand_brandseqno CHECK ((brandseqno > 0)),
    CONSTRAINT car_brand_brandid CHECK ((brandid >= 0)),
    CONSTRAINT car_brand_plate_number CHECK ((plate_number >= 0))
);

CREATE TABLE cust_car (
    plate_number integer DEFAULT 0 NOT NULL,
    duedate date,
    customerid integer DEFAULT 0 NOT NULL,
    CONSTRAINT cust_order_customerid CHECK ((customerid >= 0)),
    CONSTRAINT cust_order_plate_number CHECK ((plate_number >= 0))
);

INSERT INTO customer VALUES (0, 'Default        ', 'Customer           ', NULL);
INSERT INTO customer VALUES (1, 'Jackson        ', 'Kirk            ', 'Lagos     ');
INSERT INTO customer VALUES (2, 'Leow           ', 'May-N           ', 'Lagos     ');
INSERT INTO customer VALUES (3, 'Andreae        ', 'Peter           ', 'Oyo     ');
INSERT INTO customer VALUES (4, 'Noble          ', 'James           ', 'Abuja     ');
INSERT INTO customer VALUES (5, 'Tempero        ', 'Ewan            ', 'Abuja     ');
INSERT INTO customer VALUES (6, 'Anderson       ', 'Svend           ', 'Oyo     ');
INSERT INTO customer VALUES (7, 'Nickson        ', 'Ray             ', 'Abuja     ');
INSERT INTO customer VALUES (8, 'Dobbie         ', 'Gill            ', 'Abuja     ');
INSERT INTO customer VALUES (9, 'Martin         ', 'Paul            ', 'Lagos     ');
INSERT INTO customer VALUES (10, 'Barmouta       ', 'Alex           ', 'Abuja     ');
INSERT INTO customer VALUES (11, 'Xu             ', 'Gang           ', 'Oyo     ');
INSERT INTO customer VALUES (12, 'McMurray       ', 'Linda          ', 'Lagos     ');
INSERT INTO customer VALUES (13, 'Somerfield     ', 'Nigel          ', 'Lagos     ');
INSERT INTO customer VALUES (14, 'Anslow         ', 'Craig          ', 'Lagos     ');
INSERT INTO customer VALUES (15, 'Gandhi         ', 'Amit           ', 'Abuja     ');
INSERT INTO customer VALUES (16, 'Yi             ', 'Shusen         ', 'Oyo     ');
INSERT INTO customer VALUES (17, 'Zhou           ', 'Daisy          ', 'Oyo     ');
INSERT INTO customer VALUES (18, 'Chui           ', 'Chang          ', 'Lagos     ');
INSERT INTO customer VALUES (19, 'Wojnar         ', 'Maciej         ', 'Lagos     ');
INSERT INTO customer VALUES (20, 'Dolman         ', 'Jerome         ', 'Lagos     ');
INSERT INTO customer VALUES (21, 'Devrukhaker    ', 'Guruprasad     ', 'Abuja     ');
INSERT INTO customer VALUES (22, 'Thompson       ', 'Wayne          ', 'Abuja     ');
INSERT INTO customer VALUES (23, 'Horner         ', 'Edmund         ', 'Taraba   ');
INSERT INTO customer VALUES (24, 'Ma             ', 'Qian           ', 'Taraba   ');

INSERT INTO car(plate_number, title, edition_no) VALUES (0, 'Default car Name                                           ', NULL);
INSERT INTO car VALUES (7777, 'Toyota 1000                                ', 1, 1, 1);
INSERT INTO car VALUES (9009, 'Toyota 2000GT                                   ', 2, 5, 5);
INSERT INTO car VALUES (1928, 'Toyota 4Runner                                 ', 3, 2, 2);
INSERT INTO car VALUES (1010, 'Toyota AA                                                 ', 1, 1, 1);
INSERT INTO car VALUES (2222, 'Toyota Allex                                         ', 1, 1, 1);
INSERT INTO car VALUES (3333, 'Toyota Allion           ', 1, 2, 2);
INSERT INTO car VALUES (8888, 'Toyota Alphard                                 ', 1, 5, 5);
INSERT INTO car VALUES (1001, 'Lexus LS 400                                  ', 1, 1, 1);
INSERT INTO car VALUES (1111, 'Lexus GS 200t                            ', 3, 3, 3);
INSERT INTO car VALUES (4444, 'Lexus SC 430                              ', 2, 5, 5);
INSERT INTO car VALUES (9999, 'Lexus LFA                               ', 1, 10, 10);
INSERT INTO car VALUES (5555, 'Lexus RX 300                ', 1, 4, 4);

INSERT INTO brand VALUES (0, 'Default Name   ', 'Default Surname');
INSERT INTO brand VALUES (2, 'Camry          ', 'Toyota          ');
INSERT INTO brand VALUES (3, 'Avalon          ', 'Toyota        ');
INSERT INTO brand VALUES (4, 'Century       ', 'Toyota        ');
INSERT INTO brand VALUES (5, 'Corolla           ', 'Toyota        ');
INSERT INTO brand VALUES (6, 'Crown	           ', 'Toyota     ');
INSERT INTO brand VALUES (7, 'Etios        ', 'Toyota    ');
INSERT INTO brand VALUES (8, 'Mirai         ', 'Toyota          ');
INSERT INTO brand VALUES (9, 'Prius        ', 'Toyota          ');
INSERT INTO brand VALUES (1, 'CT           ', 'Lexus         ');
INSERT INTO brand VALUES (22, 'ES         ', 'Lexus          ');
INSERT INTO brand VALUES (10, 'IS         ', 'Lexus         ');
INSERT INTO brand VALUES (11, 'LC         ', 'Lexus        ');
INSERT INTO brand VALUES (12, 'RC         ', 'Lexus          ');
INSERT INTO brand VALUES (13, 'NX          ', 'Lexus   ');
INSERT INTO brand VALUES (14, 'RX       ', 'Lexus         ');

INSERT INTO car_brand VALUES (2222, 5, 2);
INSERT INTO car_brand VALUES (1111, 4, 2);
INSERT INTO car_brand VALUES (1111, 3, 1);
INSERT INTO car_brand VALUES (2222, 2, 1);

INSERT INTO car_brand VALUES (5555, 8, 2);
INSERT INTO car_brand VALUES (5555, 7, 1);
INSERT INTO car_brand VALUES (7777, 7, 1);
INSERT INTO car_brand VALUES (8888, 9, 1);
INSERT INTO car_brand VALUES (9999, 5, 2);
INSERT INTO car_brand VALUES (9999, 6, 3);
INSERT INTO car_brand VALUES (1001, 1, 1);
INSERT INTO car_brand VALUES (9009, 11, 1);
INSERT INTO car_brand VALUES (9009, 12, 2);
INSERT INTO car_brand VALUES (3333, 10, 1);
INSERT INTO car_brand VALUES (4444, 10, 1);
INSERT INTO car_brand VALUES (9999, 2, 1);
INSERT INTO car_brand VALUES (1928, 14, 2);
INSERT INTO car_brand VALUES (1928, 13, 1);

ALTER TABLE ONLY customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (customerid);

ALTER TABLE ONLY car
    ADD CONSTRAINT car_pkey PRIMARY KEY (plate_number);

ALTER TABLE ONLY brand
    ADD CONSTRAINT brand_pkey PRIMARY KEY (brandid);

ALTER TABLE ONLY car_brand
    ADD CONSTRAINT car_brand_pkey PRIMARY KEY (plate_number, brandid);

ALTER TABLE ONLY cust_car
    ADD CONSTRAINT cust_car_pkey PRIMARY KEY (plate_number, customerid);