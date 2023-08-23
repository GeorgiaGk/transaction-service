# transaction-service

This microservice is designed to handle financial transactions between bank accounts. It provides a RESTful API to facilitate money transfers, considering various acceptance criteria outlined below.

## Criteria

### Case 1 - Happy path for money transfer between two accounts
- Given source account and target account exist
- Source account has a balance greater or equal to the transaction amount
- When a transaction request is received then, the balance of the source account should be debited and the balance of the target account should be credited.

### Case 2 - Insufficient balance to process money transfer
- Given source account and target account exist
- Source account has a balance less than the transaction amount
- When a transaction request is received then the balance of the source account as well as the target account, should remain the same. The client of the API should receive an error.

### Case 3 - Transfer between the same account
- Given source account exists and both source and target accounts are the same
- When a transaction request is received then the balance of the source account should remain the same and the client of the API should receive an error.

### Case 4 - One or more of the accounts does not exist
- Given source or target account does not exist
- When a transaction request is received then the balance of the existing account should remain the same and the client of the API should receive an error.

### Case 5 - Erroneous currency
- Given the presence of both source and target accounts
- Either the source or target account is denominated in 'EUR', while the other bears a 'USD' currency
- Upon receiving a transaction request from the 'EUR'-designated account, an error is triggered. This error is a result of the second account exclusively accepting transactions in 'USD', and vice versa.
- The API should receive an error.

## Data Model

### Account
Represents a bank account with the following attributes:
- id: Unique identifier of the account
- balance: Current balance in the account (positive decimal)
- currency: Currency of the account
- createdAt: Date when the account was created

### Transaction
Represents a financial transaction with the following attributes:
- id: Unique identifier of the transaction
- sourceAccountId: ID of the account sending funds
- targetAccountId: ID of the account receiving funds
- amount: Amount being transferred (positive decimal)
- currency: Currency of the transaction
- transactionDate: Date of the transaction 

##  Documentation
This project includes extensive documentation using JavaDoc comments. The documentation provides detailed information about the classes, methods, and parameters used in the implementation.

To view the JavaDoc documentation:

Package the project (mvn clean package)
Open target/site/apidocs/index.html using any web browser to access the JavaDoc documentation.

## How to run transaction-service

To run the Transaction Service, follow these steps:

1. Clone the repository or download the source code.
2. Open the project in your preferred Java IDE.
3. Build the project using Maven (```mvn clean package```) or your IDE.
4. Run `TransactionServiceApplication.main()` to start the transaction-service.

## How to run Unit Tests

The transactions logic is accompanied by unit tests implemented using the JUnit and Mockito frameworks. The tests ensure that the money transfer logic is working correctly and producing the expected outcomes for different scenarios. To run the tests,
follow these steps:

1. Open the project in your Java IDE.
2. Run the test classes located in the `src/test/java` directory (`mvn test`).
