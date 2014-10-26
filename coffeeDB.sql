-- sqlite3 coffeeDB.db < coffeeDB.sql
-- 


DROP TABLE  CoffeeOrder;
DROP TABLE  Payment;
DROP TABLE  CardDetails;
DROP TABLE  OrderNextLinks;
DROP TABLE  CoffeeCost;
DROP TABLE  AdditionCost;
DROP TABLE  Barista;
DROP TABLE  Customer;

CREATE TABLE CoffeeCost
(
ID INTEGER PRIMARY KEY NOT NULL, 
Coffee TEXT NOT NULL,
Cost REAL NOT NULL
);

INSERT INTO CoffeeCost(Coffee,Cost)  VALUES ("macchiato", 4.50);
INSERT INTO CoffeeCost(Coffee,Cost)  VALUES ("espresso", 3.00);
INSERT INTO CoffeeCost(Coffee,Cost)  VALUES ("cappuccino", 3.50);


CREATE TABLE AdditionCost
(
ID INTEGER PRIMARY KEY NOT NULL, 
Addition TEXT NOT NULL,
Cost REAL NOT NULL
);


INSERT INTO AdditionCost(Addition,Cost)  VALUES ("skimMilk", 0.50);
INSERT INTO AdditionCost(Addition,Cost)  VALUES ("extraShot", 0.75);


CREATE TABLE CoffeeOrder
(
ID INTEGER PRIMARY KEY NOT NULL,
CoffeeType TEXT NOT NULL,      -- "cappuccino", "espresso", "macchiato"
Cost TEXT NOT NULL,
Addition TEXT,							-- "skimMilk", "extraShot"
Status TEXT NOT NULL,    	        -- "started", "unstarted", "cancelled", "released"

FOREIGN KEY (CoffeeType) REFERENCES CoffeeCost(Coffee),
FOREIGN KEY (Addition) REFERENCES AdditionCost(Addition)
);

CREATE TABLE Payment
(
PaymentID INTEGER PRIMARY KEY NOT NULL,
Amount TEXT NOT NULL,
PaymentType TEXT NOT NULL,    -- "cash" , "card", "pending"

FOREIGN KEY (PaymentID) REFERENCES CoffeeOrder(ID)
);


CREATE TABLE CardDetails
(
ID INTEGER PRIMARY KEY NOT NULL, 
Name TEXT NOT NULL,
CardNo TEXT NOT NULL,
Expires TEXT NOT NULL,



FOREIGN KEY (ID) REFERENCES Payment(PaymentID)
);

CREATE TABLE OrderNextLinks
(
ID INTEGER PRIMARY KEY NOT NULL, 
OrderID INTEGER NOT NULL,
Link TEXT,

FOREIGN KEY (OrderID) REFERENCES CoffeeOrder(ID)
);

CREATE TABLE Barista
(
ID INTEGER PRIMARY KEY NOT NULL, 
BaristaKey TEXT NOT NULL
);

INSERT INTO Barista(BaristaKey)  VALUES ("barista-123");


CREATE TABLE Customer
(
ID INTEGER PRIMARY KEY NOT NULL, 
CustomerKey TEXT NOT NULL
);

INSERT INTO Customer(CustomerKey)  VALUES ("customer-123");


