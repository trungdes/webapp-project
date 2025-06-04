CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message VARCHAR(255),
    is_read BIT,
    created_at VARCHAR(255),
    apartment_number INT,
    CONSTRAINT fk_apartment FOREIGN KEY (apartment_number) REFERENCES apartment(apartment_number)
); 