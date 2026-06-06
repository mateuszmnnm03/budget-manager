# Budget Manager
Account management system allowing tracking expenses and incomes across accounts.

## Tech Stack
- Java 21
- React + TypeScript
- PostgreSQL 17
- Docker

## How to run

1. Clone the repo
```bash
git clone https://github.com/mateuszmnnm03/budget-manager
```
2. Build and run the environment for development
```bash
docker compose up -d --build
```
3. The application will be available at:
- **API**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **Adminer** (database): `http://localhost:8082`

Database migrations run automatically on startup via Flyway.

## API Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/accounts` | List all accounts |
| POST | `/accounts` | Create account |
| GET | `/accounts/{id}` | Get account details |
| DELETE | `/accounts/{id}` | Delete account (only if no transactions) |
| GET | `/transactions` | List transactions (filters: `?from=&to=&category=`) |
| POST | `/transactions` | Add transaction (updates account balance) |
| DELETE | `/transactions/{id}` | Delete transaction (reverts balance) |
| GET | `/summary` | Total income, expenses and breakdown by category |
| GET | `/accounts/{id}/limits` | List budget limits for account |
| POST | `/accounts/{id}/limits` | Set budget limit for category |
| DELETE | `/accounts/{id}/limits/{category}` | Remove budget limit |
