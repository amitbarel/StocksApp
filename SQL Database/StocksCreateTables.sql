USE StockTrade;

CREATE TABLE portfolio
(portfolio_id INT NOT NULL AUTO_INCREMENT,
balance DOUBLE NOT NULL DEFAULT 0,
CONSTRAINT chk_balance CHECK(balance>=0),
PRIMARY KEY (portfolio_id));

CREATE TABLE trader
(trader_id INT NOT NULL AUTO_INCREMENT,
username VARCHAR(50) NOT NULL UNIQUE,
portfolio_id INT NOT NULL,
PRIMARY KEY (trader_id),
FOREIGN KEY (portfolio_id) 		REFERENCES portfolio(portfolio_id)
ON UPDATE CASCADE
ON DELETE NO ACTION);

CREATE TABLE company
(company_id INT NOT NULL AUTO_INCREMENT,
symbol VARCHAR(5) NOT NULL UNIQUE,
company_name VARCHAR(64) NOT NULL UNIQUE,
price DOUBLE NOT NULL
CONSTRAINT chk_price_company CHECK(price>=0),
price_change DOUBLE,
ipo_year YEAR,
volume BIGINT UNSIGNED,
industry VARCHAR(64),
stock_inventory BIGINT UNSIGNED,
PRIMARY KEY (company_id));

CREATE TABLE company_price
(company_id INT NOT NULL,
date_price DATE NOT NULL,
price DOUBLE NOT NULL
CONSTRAINT chk_price_company_price CHECK(price>=0),
FOREIGN KEY (company_id) 		REFERENCES company(company_id)
ON UPDATE CASCADE
ON DELETE NO ACTION);

CREATE TABLE stock_market_inventory
(company_id INT NOT NULL,
quantity BIGINT NOT NULL,
CONSTRAINT chk_stock_inventory_quantity CHECK(quantity>=0),
PRIMARY KEY (company_id),
FOREIGN KEY (company_id) 		REFERENCES company(company_id)
ON UPDATE CASCADE
ON DELETE NO ACTION);

CREATE TABLE stock_portfolio
(portfolio_id INT NOT NULL,
company_id INT NOT NULL,
quantity BIGINT NOT NULL
CONSTRAINT chk_stock_portfolio_quantity CHECK(quantity>0),
PRIMARY KEY (portfolio_id, company_id),
FOREIGN KEY (portfolio_id) 	REFERENCES portfolio(portfolio_id)
ON UPDATE CASCADE
ON DELETE NO ACTION,
FOREIGN KEY (company_id) 	REFERENCES company(company_id)
ON UPDATE CASCADE
ON DELETE NO ACTION);

CREATE TABLE transaction_type
(transaction_type_id INT NOT NULL AUTO_INCREMENT,
type_name VARCHAR(50) NOT NULL,
PRIMARY KEY (transaction_type_id));

INSERT INTO transaction_type (type_name) VALUES 
('Buy'),
('Sell');

CREATE TABLE stock_transaction
(portfolio_id INT NOT NULL,
company_id INT NOT NULL,
transaction_type_id INT NOT NULL,
quantity BIGINT NOT NULL
CONSTRAINT chk_stock_transaction_quantity CHECK (quantity>0),
price DOUBLE NOT NULL
CONSTRAINT chk_stock_transaction_price CHECK (price>=0),
transaction_date DATETIME NOT NULL,
FOREIGN KEY (portfolio_id) 	REFERENCES portfolio(portfolio_id)
ON UPDATE CASCADE
ON DELETE NO ACTION,
FOREIGN KEY (company_id) 	REFERENCES company(company_id)
ON UPDATE CASCADE
ON DELETE NO ACTION,
FOREIGN KEY (transaction_type_id) 	REFERENCES transaction_type(transaction_type_id)
ON UPDATE CASCADE
ON DELETE NO ACTION);

CREATE TABLE stock_watchlist
(portfolio_id INT NOT NULL,
company_id INT NOT NULL,
PRIMARY KEY (portfolio_id, company_id),
FOREIGN KEY (portfolio_id) 	REFERENCES portfolio(portfolio_id)
ON UPDATE CASCADE
ON DELETE NO ACTION,
FOREIGN KEY (company_id) 	REFERENCES company(company_id)
ON UPDATE CASCADE
ON DELETE NO ACTION);
