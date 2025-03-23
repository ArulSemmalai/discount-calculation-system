# 💱 Currency Exchange & Discount Calculation API

A Spring Boot 3.4.3 microservice that calculates the **net payable amount** for a customer’s bill by applying **dynamic discounts** and **real-time currency conversion**. It integrates with a third-party currency exchange API and follows modern software development practices, including secure, scalable, and maintainable code design.

---

## 🚀 Features

- **User Discount Calculation**
    - 30% discount for store employees
    - 10% discount for store affiliates
    - 5% discount for customers with tenure over 2 years
    - $5 discount for every $100 on the bill (applies to all)
    - Percentage-based discounts **exclude groceries**
    - Only **one percentage-based** discount applies at a time

- **Real-Time Currency Conversion**
    - Integrates with an external currency exchange API (e.g., Open Exchange Rates)
    - Converts the final payable amount from original to target currency
    - Caching implemented to reduce API calls

- **Secure REST API**
    - Endpoint: `/api/calculate`
    - Accepts bill details, user type, customer tenure, and currency preferences
    - Returns the **net payable amount** in the specified target currency
    - JWT/Basic Authentication (configurable)

- **Modern Software Practices**
    - Object-oriented design and Domain-Driven Design (DDD)
    - Implemented based on SOLID principles
    - Strategy Design Pattern - Behaviour Design Pattern
    - Unit and integration tests with high coverage (JUnit 5 & Mockito)
    - Static code analysis using SonarQube
    - Resilience4j for fault tolerance (Circuit Breaker, Retry)
    - Caching with Redis
    - Discount values are configured from application.yml

---

## 🛠️ Tech Stack

| Tech             | Version            |
|------------------|--------------------|
| Java             | 17                 |
| Spring Boot      | 3.4.3              |
| Resilience4j     | 2.2.0              |
| Maven            | 3.8+               |
| JUnit / Mockito  | 5 / 4              |
| SonarQube        | Optional           |
| Caching          | Redis              |
| Security         | Basic Auth / JWT   |
| Postman          | For API Testing    |

---

### 🔧 How to Run Locally

### Prerequisites
- Java 17 or higher installed
- Maven 3.8+ installed
- (Optional) Docker for SonarQube (if applicable)



### Running the Application

1. **Clone the repository**
   ```bash
   
   git clone https://github.com/your-username/currency-discount-api.git
   cd currency-discount-api
---
## Build the application

mvn clean install

## Run the application

mvn spring-boot:run

## Run Tests

mvn test

Test reports can be found at:

target/surefire-reports

Code coverage reports are at:

target/site/jacoco/index.html

---
### 📝 Assumptions & Design Decisions

Discounts are mutually exclusive; the highest applies.

Customers over 2 years get loyalty discounts

Percentage discounts do not apply to grocery items.

Currency exchange rates are cached to reduce API calls.

Resilience4j used for Circuit Breaker and Retry in external API calls.

Authentication defaults to Basic Auth, but JWT can be enabled if needed.

---
## 🔗 API Endpoint

## `POST /api/calculate`

## Request Body Example:
```` Json
{
  "billAmount": 2000.00,
  "userType": "EMPLOYEE",
  "customerTenure": 5,
  "originalCurrency": "EUR",
  "targetCurrency": "INR",
  "items": [
    {
      "category": "Electronics",
      "price": 1500.00
    },
    {
      "category": "Groceries",
      "price": 100.00
    }
  ]
}
````

### 👨‍💻 Author
ArulKumar Semmalai
https://www.linkedin.com/in/arul-semmalai/

