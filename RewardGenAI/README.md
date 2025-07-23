# GenAI Reward Query Bot 🤖

A production-ready AI-powered chatbot that enables customers and agents to query reward information using natural language. Built with Java 17, Spring Boot 3.2, and OpenAI GPT integration.

## 🎯 Problem Statement

Customers and agents manually check:
- Reward point balances
- Redemption options  
- Expiry details
- Cashback options

**Issues**: Delays, lower redemption rates, customer frustration

## 💡 Solution

AI-powered bot that enables natural language queries like:
- "How many reward points do I have?"
- "What can I get for 5000 points?"
- "When do my points expire?"

## 🏗️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Frontend/     │───▶│   Reward Query   │───▶│   OpenAI GPT    │
│   Agent Portal  │    │      Bot API     │    │     Service     │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │   Microservices  │
                    │   Integration    │
                    └──────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   Rewards    │    │  Redemption  │    │   Customer   │
│   Service    │    │   Service    │    │   Service    │
└──────────────┘    └──────────────┘    └──────────────┘
```

## 🚀 Features

### Core Capabilities
- **Natural Language Processing**: Understands customer queries in plain English
- **Context-Aware Responses**: Provides personalized answers based on customer data
- **Multi-Intent Recognition**: Handles complex queries involving multiple topics
- **Real-time Data Integration**: Fetches live data from microservices

### Production Features
- **Circuit Breaker Pattern**: Resilience4j for fault tolerance
- **Caching**: Redis-backed caching for performance
- **Monitoring**: Health checks and metrics endpoints
- **Security**: Input validation and error handling
- **Testing**: Comprehensive unit and integration tests

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.2**
- **Spring Cloud OpenFeign** - Microservice integration
- **OpenAI GPT-3.5-turbo** - AI processing
- **Resilience4j** - Circuit breaker and retry
- **Caffeine** - Caching
- **Maven** - Build management
- **JUnit 5** - Testing

## 📋 Prerequisites

- Java 17+
- Maven 3.8+
- OpenAI API Key
- Access to reward microservices

## 🚀 Quick Start

### 1. Environment Setup
```bash
export OPENAI_API_KEY="your-openai-api-key"
export REWARDS_SERVICE_URL="http://localhost:8081"
export REDEMPTION_SERVICE_URL="http://localhost:8082"
export CUSTOMER_SERVICE_URL="http://localhost:8083"
```

### 2. Build and Run
```bash
cd RewardGenAI
mvn clean install
mvn spring-boot:run
```

### 3. Test the API
```bash
curl -X POST http://localhost:8080/api/v1/rewards/query \
  -H "Content-Type: application/json" \
  -d '{
    "query": "How many points do I have?",
    "customerId": "customer123",
    "userType": "CUSTOMER"
  }'
```

## 📖 API Documentation

### POST /api/v1/rewards/query

Process a natural language reward query.

**Request:**
```json
{
  "query": "How many points do I have?",
  "customerId": "customer123",
  "sessionId": "session456",
  "userType": "CUSTOMER"
}
```

**Response:**
```json
{
  "response": "You currently have 5,000 reward points available! You also have 2,500 points expiring on Dec 31, 2024.",
  "sessionId": "session456",
  "responseTimeMs": 247,
  "success": true,
  "contextData": {
    "totalPoints": 7500,
    "availablePoints": 5000,
    "nextExpiryDate": "2024-12-31",
    "redemptionOptions": [
      {
        "name": "Gift Card - Amazon",
        "pointsRequired": 5000,
        "cashValue": 50.00,
        "available": true
      }
    ]
  }
}
```

### Supported Query Types

1. **Balance Queries**
   - "How many points do I have?"
   - "What's my current reward balance?"

2. **Redemption Queries**
   - "What can I redeem with 5000 points?"
   - "Show me redemption options"

3. **Expiry Queries**
   - "When do my points expire?"
   - "What points are expiring soon?"

4. **Cashback Queries**
   - "What's my cashback balance?"
   - "How much cash back do I have?"

## 🔧 Configuration

### Application Properties
```yaml
# AI Configuration
ai:
  openai:
    api-key: ${OPENAI_API_KEY}
    model: gpt-3.5-turbo
    max-tokens: 500
    temperature: 0.7

# Microservice URLs
microservices:
  rewards:
    base-url: ${REWARDS_SERVICE_URL:http://localhost:8081}
  redemption:
    base-url: ${REDEMPTION_SERVICE_URL:http://localhost:8082}
  customer:
    base-url: ${CUSTOMER_SERVICE_URL:http://localhost:8083}

# Circuit Breaker
resilience4j:
  circuitbreaker:
    instances:
      rewardService:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000
```

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Integration Testing
```bash
mvn verify
```

### Test Coverage
- Unit tests for all service classes
- Integration tests for API endpoints
- Mock tests for external service integration

## 📊 Monitoring

### Health Check
```bash
curl http://localhost:8080/api/v1/rewards/health
```

### Metrics
- Circuit breaker status: `/actuator/health`
- Application metrics: `/actuator/metrics`
- Prometheus metrics: `/actuator/prometheus`

## 🔒 Security Considerations

- Input validation on all requests
- Rate limiting (recommended)
- API key management for OpenAI
- Secure microservice communication
- Error message sanitization

## 🚀 Deployment

### Docker (Optional)
```dockerfile
FROM openjdk:17-jre-slim
COPY target/reward-query-bot-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
- `OPENAI_API_KEY`: Your OpenAI API key
- `REWARDS_SERVICE_URL`: Rewards microservice URL
- `REDEMPTION_SERVICE_URL`: Redemption microservice URL
- `CUSTOMER_SERVICE_URL`: Customer microservice URL

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new features
4. Ensure all tests pass
5. Submit a pull request

## 📞 Support

For technical support or questions:
- Check the logs for detailed error information
- Review the health check endpoints
- Validate microservice connectivity
- Verify OpenAI API key configuration

## 🔄 Future Enhancements

- Multi-language support
- Voice integration
- Advanced analytics
- Custom reward program rules
- Mobile SDK
- Real-time notifications 