# AI Code Review Assistant
A backend system that provides automated, AI-powered code reviews via a REST API.
Developers submit code snippets and receive structured feedback on bugs, security
vulnerabilities, performance issues, and best practice violations — with an overall
quality score. Built with a two-service async architecture to handle slow AI
processing without blocking the API.

## Architecture
Client → Core API (port 8080)
├── Redis: rate limit check (10 req/hour)
├── Redis: cache check (return instantly if same code reviewed before)
├── Saves review as PENDING
├── Publishes to Kafka topic: code-review-requests
└── Returns 202 Accepted immediately

Kafka → AI Worker (internal)
├── Consumes from code-review-requests topic
├── Calls Spring AI with structured output
├── Maps response to CodeReview entity
└── Updates DB status to COMPLETED

Client polls GET /reviews/{id} until status = COMPLETED


## Tech Stack

- **Language:** Java 23
- **Framework:** Spring Boot 4.1
- **AI Integration:** Spring AI (OpenAI)
- **Security:** Spring Security + JWT
- **Database:** PostgreSQL (Spring Data JPA)
- **Caching + Rate Limiting:** Redis
- **Messaging:** Apache Kafka
- **Containerization:** Docker + Docker Compose
- **Cloud:** AWS ECS (Fargate), ECR, RDS
- **CI/CD:** GitHub Actions
- **Build Tool:** Maven

## Key Features

- **Async processing** — Core API returns 202 immediately, AI Worker processes independently via Kafka
- **Redis caching** — identical code submissions served from cache (SHA-256 hash key, 24hr TTL)
- **Rate limiting** — 10 requests per user per hour enforced via Redis sliding window counter
- **Structured AI output** — Spring AI maps model response directly to typed Java object, no free-text parsing
- **JWT authentication** — all review endpoints secured, user identity extracted from token
- **Analytics dashboard** — per-user stats: total reviews, average score, score trend, language breakdown
- **Global exception handling** — structured error responses with HTTP status codes
- **Input validation** — request body validated before processing
- **CI/CD pipeline** — GitHub Actions builds and pushes Docker images to ECR on every push to main

## API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /users/register | Register new user | No |
| POST | /users/login | Login, returns JWT token | No |
| POST | /reviews/submit | Submit code for review | Yes |
| GET | /reviews/{id} | Get review by id (poll for result) | Yes |
| GET | /reviews/history | Get all reviews for logged-in user | Yes |
| GET | /reviews/dashboard | Get analytics summary | Yes |

## How to Run Locally

### Prerequisites
- Java 23
- Maven
- Docker + Docker Compose

### Steps

1. Clone the repository
```bash
git clone https://github.com/RobinSingh2869/ai-code-review-assistant.git
cd ai-code-review-assistant
```

2. Build both services
```bash
cd core-api && mvn clean package -DskipTests && cd ..
cd ai-worker && mvn clean package -DskipTests && cd ..
```

3. Set environment variables in docker-compose.yml or create a .env file

4. Run everything
```bash
docker-compose up --build
```

5. API available at `http://localhost:8080`

## Environment Variables

| Variable | Service | Description |
|----------|---------|-------------|
| SPRING_DATASOURCE_URL | Both | PostgreSQL connection URL |
| SPRING_DATASOURCE_USERNAME | Both | Database username |
| SPRING_DATASOURCE_PASSWORD | Both | Database password |
| SPRING_KAFKA_BOOTSTRAP_SERVERS | Both | Kafka broker address |
| SPRING_DATA_REDIS_HOST | core-api | Redis host |
| SPRING_AI_OPENAI_API_KEY | ai-worker | OpenAI API key |
| JWT_SECRET | core-api | Base64 encoded JWT secret |

## CI/CD Pipeline

GitHub Actions workflow triggers on every push to `main`:
1. Checkout code
2. Set up Java 23
3. Build core-api JAR (`mvn clean package -DskipTests`)
4. Build ai-worker JAR (`mvn clean package -DskipTests`)
5. Authenticate with AWS
6. Login to Amazon ECR
7. Build and push core-api Docker image (tagged with commit SHA + latest)
8. Build and push ai-worker Docker image (tagged with commit SHA + latest)

## Repository

GitHub: https://github.com/RobinSingh2869/ai-code-review-assistant