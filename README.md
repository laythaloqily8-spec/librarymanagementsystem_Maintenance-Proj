# Library Management System (Refactored)

This project is a refactored version of a Library Management System developed using Spring Boot. The main goal of this project is to improve code quality, readability, and maintainability by applying software maintenance and refactoring techniques.

## Features
- Book Management (CRUD operations)
- Author Management
- Category Management
- Publisher Management
- Search functionality
- Admin login system

## Refactoring Improvements
- Replaced generic annotations with specific mappings (@GetMapping, @PostMapping)
- Removed dead code and unnecessary operations
- Improved variable naming for better readability
- Replaced magic numbers with constants
- Extracted reusable methods (pagination logic)
- Reduced repeated database calls
- Removed debug code from production
- Added documentation comments

## Technologies Used
- Java
- Spring Boot
- Maven
- H2 Database

## How to Run
```bash
mvn spring-boot:run
