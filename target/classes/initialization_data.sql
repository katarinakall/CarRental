DROP ALL OBJECTS;

CREATE TABLE rent_car
(
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    training_type text NOT NULL,
    training_date date NOT NULL,
    training_length_min integer NOT NULL,
    CONSTRAINT training_session_pkey PRIMARY KEY (id)
);

CREATE TABLE customers
(
    id uuid NOT NULL,
    dateOfBirth date NOT NULL,
    lastFourDigits integer NOT NULL,
    bookingNumber integer,
    CONSTRAINT customers_pkey PRIMARY KEY (id)
);

CREATE TABLE cars
(
    id uuid NOT NULL,
    registrationPlate text NOT NULL,
    carType text NOT NULL,
    mileage integer NOT NULL,
    CONSTRAINT cars_pkey PRIMARY KEY (id)
);