CREATE TABLE team (
   id          BIGINT          NOT NULL    AUTO_INCREMENT,
   name        VARCHAR(50)     NOT NULL,
   PRIMARY KEY (id),
   UNIQUE (name)
);

CREATE TABLE tournament (
   id           BIGINT          NOT NULL    AUTO_INCREMENT,
   name         VARCHAR(50)     NOT NULL,
   address      VARCHAR(255)    NOT NULL,
   start_time   DATETIME,
   end_time     DATETIME,
   PRIMARY KEY (id),
   UNIQUE (name)
);

CREATE TABLE coach (
   id     BIGINT       NOT NULL AUTO_INCREMENT,
   name   VARCHAR(50)  NOT NULL,
   email  VARCHAR(100) NOT NULL UNIQUE,
   PRIMARY KEY (id)
);