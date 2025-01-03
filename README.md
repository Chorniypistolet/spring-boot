# 📚 Book Store Application

## 🌟 Introduction

The Book Store Application is a backend system designed to manage books, categories, users, and orders efficiently. It provides secure authentication, scalable design, and clean separation of concerns using modern Java development practices. This project was built to demonstrate skills in developing enterprise-grade applications with a focus on modularity, testability, and maintainability.

## 🛠️ Technologies and Tools
 
Java 17: Core programming language.

Spring Boot: Framework for WEB application development.

Spring Security: For authentication and authorization with Bearer tokens.

Spring Data JPA: For seamless database interactions using repositories.

MapStruct: To map between entities and DTOs.

Liquibase: For database version control and migrations.

MySQL: Relational database for persistent storage.

Swagger/OpenAPI: For API documentation and testing.

Docker & Docker Compose: For containerization and deployment.

JUnit & Mockito: For testing services, controllers, and repository layers.

## 🚀 Core Design Principles
    
### Service and Implementation Layers

Each business logic function is implemented as a service with a clean interface:

#### Example:

*ShoppingCartService* interface defines operations like adding a book to the cart, updating quantities, or clearing the cart.
    
*ShoppingCartServiceImpl* provides the actual implementation, allowing for dependency injection and separation of concerns.

### Repository Layer with Spring Data JPA

Repositories are designed as interfaces extending *JpaRepository* for simple and efficient database access.

#### Example:

*findAllByCategoryId*

*findByIdWithItems*

In cases where complex queries are needed or performance optimization is required, custom JPQL or native SQL queries are used. This allows for greater flexibility and fine-tuned control over the database operations.

#### Example of a Custom Query:

    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.id = :categoryId AND b.isDeleted = false")
    List<Book> findAllByCategoryId(@Param("categoryId")Long categoryId);

### Mapping with MapStruct

The project utilizes MapStruct for converting between entities and DTOs, ensuring clean and concise code.

#### Example:

**ShoppingCartMapper** maps *ShoppingCart* entities to *ShoppingCartDto*.

**BookMapper** maps *Book* entities to *BookDto*.

### Use of DTOs

Data Transfer Objects (DTOs) are employed to transfer data between layers, ensuring that only necessary fields are exposed to the API consumers.

#### Example:

*CartItemRequestDto*: Contains bookId and quantity for cart item requests.

### Custom Exceptions

Custom exception handling improves error clarity and user experience.

#### Example:

*EntityNotFoundException*

*OrderProcessingException*

*RegistrationException*

### Exception Handling with CustomGlobalExceptionHandler

The project includes a *CustomGlobalExceptionHandler* for centralized exception handling. This ensures that all exceptions are managed in a unified way, enhancing code maintainability and providing consistent error responses to clients.

For example, if a requested resource is not found, the handler returns a structured error message with an appropriate HTTP status code.

### Security and Access Control

#### Authentication with Bearer Tokens:

The project uses Spring Security with Bearer tokens for secure authentication and role-based access control.

#### Publicly Accessible Endpoints:

Only the endpoints for */login* and */registration* are accessible without authentication.

#### Role-Based Endpoint Access:

*Admin-Only* Endpoints: Certain endpoints are restricted exclusively to users with the ROLE_ADMIN. These include:
Managing books (add, update, delete).
Viewing or managing user information.

*User-Only* Endpoints: Regular users have access to endpoints for managing their shopping cart, placing orders, and browsing the book catalog.

#### Protected Endpoints:

All endpoints, except for login and registration, require users to be authenticated with a valid Bearer token.

### User Context with @AuthenticationPrincipal

#### Accessing Authenticated User Details:

The project leverages the @AuthenticationPrincipal annotation to retrieve the currently authenticated user. This simplifies the process of associating actions (like adding items to the cart or placing an order) with the user who performed them.

#### Example Usage:

    @PostMapping
    public ShoppingCartDto addBookToCart(@AuthenticationPrincipal User user, @RequestBody @Valid CartItemRequestDto cartItemRequestDto) {
    return shoppingCartService.addBookToCart(user.getId(), cartItemRequestDto);
    }
Here, the User object provided by @AuthenticationPrincipal contains all necessary details about the authenticated user, such as their ID, email, and roles.

#### Benefits:

Reduces boilerplate code for retrieving the user context.
Ensures that actions are securely tied to the authenticated user, enhancing both security and code maintainability.

### Hibernate and JPA Features

The project leverages Hibernate, the default JPA provider in Spring Boot, to simplify database interactions. Hibernate automatically manages:

Entity Relationships: Complex mappings such as @OneToMany, @ManyToOne, and @ManyToMany are handled seamlessly.
Lazy and Eager Loading: By default, lazy loading is used for collections, with custom JPQL queries (e.g., LEFT JOIN FETCH) employed where necessary to avoid performance issues.
Hibernate’s robust caching and SQL generation capabilities ensure efficient interaction with the MySQL database.

### Database Migrations with Liquibase

All database schema changes are managed using Liquibase YAML files.
Ensures consistent database structure across environments.

#### Example:
    databaseChangeLog:
        - changeSet:
            id: 3
            author: ChorniyPistolet
            changes:
                - createTable:
                    tableName: roles
                    columns:
                        - column:
                            name: id
                            type: BIGINT
                            autoIncrement: true
                            constraints:
                            primaryKey: true
                            nullable: false
                        - column:
                            name: name
                            type: ENUM('ROLE_USER', 'ROLE_ADMIN')
                            constraints:
                            nullable: false
                            unique: true

## 🚀 Core Functionalities

### Controllers Overview

#### AuthenticationController

Handles user authentication and login.

POST /registration

    @PostMapping("/registration")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

#### BookController

Manages book-related operations.

GET /books
    
    @GetMapping
    @Operation(summary = "Get all books")
    public List<BookDto> getAll(@ParameterObject @PageableDefault Pageable pageable) {
        return bookService.findAll(pageable);
    }

POST /books (admin only)

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create new book")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

#### CategoryController

Manages book categories.

GET /categories/{id}
    
    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

PUT /categories/{id} (admin only)

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto updateCategory(@PathVariable Long id,
                                      @RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        return categoryService.update(id, categoryRequestDto);
    }

#### ShoppingCartController

Implements shopping cart functionality.

POST /cart

    @PostMapping
    public ShoppingCartDto addBookToCart(@AuthenticationPrincipal User user,
                                         @RequestBody @Valid
                                         CartItemRequestDto cartItemRequestDto) {
        return shoppingCartService.addBookToCart(user.getId(), cartItemRequestDto);
    }

DELETE /cart/items/{id}

    @DeleteMapping("/items/{cartItemId}")
    public void deleteCartItem(@AuthenticationPrincipal User user,
                               @PathVariable("cartItemId") Long id) {
        shoppingCartService.deleteCartItem(user.getId(), id);
    }

#### OrderController

Handles order placement and retrieval.

POST /orders

    @PostMapping
    public OrderDto makeAnOrder(@AuthenticationPrincipal User user,
                                @RequestBody @Valid OrderRequestDto orderRequestDto) {
        return orderService.createOrder(user.getId(), orderRequestDto);
    }

GET /orders/{id}

    @GetMapping
    public List<OrderDto> getAll(@AuthenticationPrincipal User user, Pageable pageable) {
        return orderService.getAllOrdersByUserId(user.getId(), pageable);
    }

## 🛠️ Setting Up the Project

### Prerequisites

Java 17 or later.

Maven.

Docker & Docker Compose.
### Running Locally

#### Clone the repository:

    git clone https://github.com/Chorniypistolet/spring-boot.git
    cd spring-boot

#### Configure the .env file for Docker:

    # MySQL Configuration
    MYSQL_ROOT_PASSWORD=your_root_password  # The password for the MySQL root user
    MYSQL_DATABASE=your_database_name       # The name of the MySQL database
    MYSQL_LOCAL_PORT=3307                   # Local port to access MySQL
    MYSQL_DOCKER_PORT=3306                  # MySQL port inside the Docker container

    # Spring Boot Configuration
    SPRING_LOCAL_PORT=8088                  # Local port for accessing the Spring Boot application
    SPRING_DOCKER_PORT=8080                 # Spring Boot port inside the Docker container

    # Debugging Configuration
    DEBUG_PORT=5005                         # Port for remote debugging (Java Debug Wire Protocol)

#### Build and run with Docker Compose:

    docker-compose up --build

#### Access the application:

    API: http://localhost:8088
    Swagger UI: http://localhost:8088/swagger-ui.html

#### Run tests:

    mvn test

## ⚡ Challenges Faced and Solutions

### Entity Relationships and Lazy Loading:

*Challenge*: Ensuring proper loading of relationships like cartItems.

*Solution*: Used @Query annotations with JOIN FETCH and tested thoroughly.

### Database Schema Management:

*Challenge*: Maintaining a consistent schema across environments.

*Solution*: Adopted Liquibase for robust schema versioning.

### Secure API Access:

*Challenge*: Implementing secure access with minimal overhead.

*Solution*: Used Spring Security with JWT for token-based authentication.

### Mapping Complexity:

*Challenge*: Converting complex entities to DTOs efficiently.

*Solution*: Used MapStruct to automate mappings, reducing boilerplate code.

## ✨ Conclusion

This Book Store Application demonstrates a production-grade Spring Boot backend with a modular architecture and modern development practices. It is a showcase of my skills in developing secure, scalable, and testable applications.