CREATE TABLE trips (
    id VARCHAR(100) PRIMARY KEY,
    driver_id VARCHAR(100) NOT NULL,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    seats_total INT NOT NULL,
    seats_available INT NOT NULL,
    price DOUBLE PRECISION,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE reservations (
    id VARCHAR(100) PRIMARY KEY,
    trip_id VARCHAR(100) NOT NULL,
    passenger_id VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_trip
      FOREIGN KEY (trip_id)
          REFERENCES trips(id)
);
