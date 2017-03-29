DROP TABLE IF EXISTS users;

CREATE TABLE users
(
  id int NOT NULL AUTO_INCREMENT,
  username varchar(255) NOT NULL,
  email varchar(255) NOT NULL,
  role varchar(12) NOT NULL,
  saltedPassword varchar(255),
  salt varchar(255),
  resetToken varchar(255)
);

INSERT INTO users (username, email, role)
  VALUES ('admin', 'admin@localhost', 'ADMIN');
INSERT INTO users (username, email, role)
  VALUES ('beheerder', 'beheer@localhost', 'CONTROLLER');
INSERT INTO users (username, email, role)
  VALUES ('user', 'user@localhost', 'NORMAL');
COMMIT;