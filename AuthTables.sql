CREATE TABLE WS_email (
  id INT IDENTITY(1,1) PRIMARY KEY,
  userID varchar(16) NOT NULL,
  requestTime varchar(50) NOT NULL,
  accessToken varchar(64) NOT NULL
);

CREATE TABLE WS_auth (
  id INT IDENTITY(1,1) PRIMARY KEY,
  userID varchar(16) NOT NULL,
  requestTime varchar(50) NOT NULL,
  accessToken varchar(64) NOT NULL
);

CREATE TABLE WS_member (
  id INT IDENTITY(1,1) PRIMARY KEY,
  userID varchar(16) NOT NULL,
  userPWHash varchar(64) NOT NULL,
  userName varchar(50) NOT NULL,
  userEmail text NOT NULL,
  enable INT NOT NULL
);

CREATE TABLE WS_resetpw (
  id INT IDENTITY(1,1) PRIMARY KEY,
  userID varchar(16) NOT NULL,
  requestTime varchar(50) NOT NULL,
  resetToken varchar(64) NOT NULL
);

CREATE TABLE WS_log (
  id INT IDENTITY(1,1) PRIMARY KEY,
  userID varchar(16) NOT NULL,
  actionTime varchar(50) NOT NULL,
  userAgent text NOT NULL,
  userAction text NOT NULL
);