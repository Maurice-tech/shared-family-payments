# shared-family-payments


## 📘 Project Overview
A secure real-time payment processing system for family use. Supports shared children between parents, atomic balance updates, and dynamic payment logic.

## 🚀 How to Run the Project

### Prerequisites
- Java 17+
- Maven
- H2 in-memory database.


### Steps
```bash
# Clone the repo
git clone https://github.com/Maurice-tech/shared-family-payments.git

# Navigate into the project
cd shared-family-payments

# Run tests
mvn test

# Run the app
mvn(Global Maven):  spring-boot:run
                OR
mvn(Maven Wrapper): ./mvnw spring-boot:run
```
🔐 Security Design Decisions
- JWT for authentication
- Role-based access control
- Input validation and exception handling

## Security Overview
This project uses JWT (JSON Web Tokens) for authentication and authorization. The JWT tokens are generated upon user login and contain the user's username and roles. These tokens are used for authenticating users in subsequent requests.
* JWT Token Generation: After login, a token is generated containing the user's username and roles, signed with a secret key, and given an expiration time.
* Token Validation: Incoming requests are checked for a valid JWT token in the ```Authorization``` header. If valid, the user is authenticated and authorized based on their roles.
* Role-Based Access Control: Specific endpoints are restricted based on user roles. For example, only ```ADMIN``` users can access the ```/api/v1/payments``` endpoint.
* Password Security: User passwords are securely stored using BCryptPasswordEncoder.

## Data Initialization
This application includes a data initialization process that runs on startup to seed essential data into the database. The following data is seeded:
* Roles: ```ROLE_ADMIN``` and ```ROLE_USER``` roles are seeded into the system to manage user access.
* Payment Rate: A default payment rate of ```0.05``` is seeded to define the rate used for payment calculations.
* Parents and Students: Sample Parent and Student records are seeded to demonstrate the parent-student relationship in the system, with default amounts for both parents and students.

### Authentication and User Management
The application provides a secure user authentication and registration system. The key features are:
* User Registration: Users can be registered by providing a username, password, and role. The system ensures that the username is unique and associates the user with the appropriate role (e.g., ```ROLE_ADMIN, ROLE_USER```).
* Login: Users can log in using their username and password. Upon successful authentication, a JWT (JSON Web Token) is generated and returned to the user for subsequent requests.
* Security: Passwords are encrypted using BCrypt, ensuring secure storage and validation. The JWT generated for authenticated users includes role information and is used for subsequent authorization.
* Error Handling: The system handles errors such as missing role names during registration, invalid credentials, and duplicate usernames.


## 🧮 Payment Logic
- Dynamic fee calculation
- Shared payments update both parents atomically
- Uses transaction management to ensure rollback on failure
- Transaction Recording
- Adding Funds to Parent:
- Error Handling

The Payment Service handles the processing of payments, ensuring secure and authorized transactions between parents and students. Key features include:
* Payment Processing: Payments are processed based on the amount, adjusting for dynamic payment rates. Payments can either be unique (for a single parent) or shared (when multiple parents are involved). The service checks for sufficient balance before proceeding with the payment and updates the balances of parents and students accordingly.
* Transaction Recording: Every payment transaction is logged, recording details such as the payer, student, payment amount, transaction type, and any fees applied.
* Adding Funds to Parent: Allows adding funds to a parent’s account. The service ensures that the new balance does not go below zero. 
* Error Handling: The service validates inputs and checks for authentication, ensuring only authorized users can perform transactions. It also handles scenarios like insufficient funds or invalid requests.


