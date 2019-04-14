DROP ALL OBJECTS;

CREATE TABLE rent_car
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    car_id uuid NOT NULL,
    booking_number integer,
    pick_up_date date NOT NULL,
    return_date date NOT NULL,
    mileage_at_return integer NOT NULL,
    CONSTRAINT rent_car_pkey PRIMARY KEY (id)
);

CREATE TABLE customers
(
    id uuid NOT NULL,
    date_of_birth date NOT NULL,
    last_four_digits integer NOT NULL,
    CONSTRAINT customers_pkey PRIMARY KEY (id)
);

CREATE TABLE cars
(
    id uuid NOT NULL,
    registration_plate text NOT NULL,
    car_type text NOT NULL,
    mileage integer NOT NULL,
    CONSTRAINT cars_pkey PRIMARY KEY (id)
);