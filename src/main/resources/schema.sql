CREATE TABLE IF NOT EXISTS restaurants
(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR (50) NOT NULL,
   image_name VARCHAR (255),
   category VARCHAR (50),
   description VARCHAR (255) NOT NULL,
   price INT NOT NULL,
   capacity INT NOT NULL,
   postal_code VARCHAR (50) NOT NULL,
   address VARCHAR (255) NOT NULL,
   phone_number VARCHAR (50) NOT NULL,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS roles
(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   membership VARCHAR (50) NOT NULL
);
CREATE TABLE IF NOT EXISTS users
(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR (50) NOT NULL,
   furigana VARCHAR (50) NOT NULL,
   postal_code VARCHAR (50) NOT NULL,
   address VARCHAR (255) NOT NULL,
   phone_number VARCHAR (50) NOT NULL,
   email VARCHAR (255) NOT NULL UNIQUE,
   password VARCHAR (255) NOT NULL,
   role_id INT NOT NULL,
   enabled BOOLEAN NOT NULL,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (role_id) REFERENCES roles (id)
);
CREATE TABLE IF NOT EXISTS verification_tokens
(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   user_id INT NOT NULL UNIQUE,
   token VARCHAR (255) NOT NULL,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE TABLE IF NOT EXISTS reservations
(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   restaurant_id INT NOT NULL,
   user_id INT NOT NULL,
   checkin_date DATE NOT NULL,
   checkin_time TIME NOT NULL,
   number_of_people INT NOT NULL,
   amount INT NOT NULL,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
   FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE TABLE IF NOT EXISTS reviews
(
   id INT PRIMARY KEY AUTO_INCREMENT,
   restaurant_id INT NOT NULL,
   user_id INT NOT NULL,
   rating INT CHECK
   (
      rating BETWEEN 1
      AND
         5
   ),
   review TEXT,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
   FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE TABLE IF NOT EXISTS favorites
(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   restaurant_id INT NOT NULL,
   user_id INT NOT NULL,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
   FOREIGN KEY (user_id) REFERENCES users (id)
);