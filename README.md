# Crypto Wallet REST API

A Spring Boot 3 application to manage a user's crypto wallet, fetch live token prices, and evaluate performance over time.

## ✨ Features

- Create wallets using unique email addresses
- Add crypto assets to wallets with live price validation (CoinCap API)
- View wallet details (total USD value, token breakdown)
- Evaluate wallet value over time (best/worst performance)
- Scheduled price updates (configurable frequency)
- Thread-safe price fetching (max 3 threads)
- RESTful JSON APIs
- Docker & GitHub Actions ready

## 📦 Tech Stack

- Java 21, Spring Boot 3
- Gradle Kotlin DSL
- CoinCap API v3
- JPA + Hibernate
- JUnit & Mockito
- Docker & docker-compose

## 🛠️ Getting Started

### Prerequisites
- Java 21
- Docker & Docker Compose

### Running Locally

#### Environment variable 
Create a .env file in the root project with value for COINCAP_API_KEY

```bash
  git clone https://github.com/yourusername/walletrest.git
cd walletrest
docker-compose up
```

Documentation can be accessed at
http://localhost:8080/swagger-ui/index.html