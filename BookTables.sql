
CREATE TABLE BK_books (
  id INT IDENTITY(1,1) PRIMARY KEY,
  bookName varchar(100) NOT NULL,
  bookAbstract text NOT NULL,
  bookCover text NOT NULL,
  bookContent text NOT NULL,
  bookCost INT NOT NULL,
  bookCreator varchar(16) NOT NULL
);

CREATE TABLE BK_owners (
  id INT IDENTITY(1,1) PRIMARY KEY,
  bookID INT NOT NULL,
  userID varchar(16) NOT NULL
);

CREATE TABLE BK_rate (
  id INT IDENTITY(1,1) PRIMARY KEY,
  bookID INT NOT NULL,
  userID varchar(16) NOT NULL,
  rate INT NOT NULL,
  review text NOT NULL
);

CREATE TABLE BK_users (
  id INT IDENTITY(1,1) PRIMARY KEY,
  userID varchar(16) NOT NULL,
  point INT NOT NULL
);