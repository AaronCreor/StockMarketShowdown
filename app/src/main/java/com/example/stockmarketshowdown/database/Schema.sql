CREATE SCHEMA SMS;

CREATE TABLE Users (
    UserID VARCHAR(100) PRIMARY KEY,
    DisplayName VARCHAR(100),
    Email VARCHAR(100),
    Biography TEXT,
    Tagline VARCHAR(255),
    Cash DECIMAL(10, 2),
    Picture VARCHAR(255)
);

CREATE TABLE TransactionHistory (
    TransactionID INT PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(100),
    TradeType VARCHAR(50),
    TradeValue DECIMAL(10, 2),
    TradeDate DATE,
    TradeCompany VARCHAR(100),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

CREATE TABLE Portfolio (
    PortfolioID INT PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(100),
    Company VARCHAR(100),
    Ownership INT,
    Cost DECIMAL(10, 2),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

CREATE TABLE Score (
      ScoreID INT PRIMARY KEY AUTO_INCREMENT,
      UserID VARCHAR(100),
      Score INT,
      FOREIGN KEY (UserID) REFERENCES Users(UserID)
);