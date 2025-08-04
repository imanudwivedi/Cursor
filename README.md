# 🎁 GenAI Reward Bot

A production-ready GenAI-powered bot that enables users and agents to query reward points, expiry details, and cashback options using natural language.

## 📋 Problem Statement

Customers and agents manually check reward point balances, expiry details, and cashback options, causing:
- ⚠️ Delays in customer service
- ⚠️ Lower redemption rates  
- ⚠️ Customer frustration

## 💡 Solution

A Gen AI-powered bot that enables users to ask questions in plain English:
- "How many rewards points do I have?"
- "What can I get for 5000 points?"
- "When do my points expire?"
- "Show me cashback options"

## 🏗 Architecture

### Microservices Architecture
- **Auth Service** (Port 8081): User authentication via mobile number
- **Reward Service** (Port 8082): Reward points management and mock data
- **GenAI Service** (Port 8083): Natural language processing
- **Frontend**: Simple HTML/CSS/JS interface

### Technology Stack
- **Backend**: Java 17 + Spring Boot 3.2.0
- **Database**: Oracle Database (with H2 fallback for testing)
- **Frontend**: HTML5, CSS3, JavaScript (ES6+)
- **Build Tool**: Maven 3.8+
- **ORM**: Hibernate/JPA

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- Oracle Database (optional - H2 included for testing)
- Modern web browser

### 1. Clone and Build
```bash
git clone <repository-url>
cd genai-reward-bot

# Build all modules
mvn clean install
```

### 2. Database Setup

#### Option A: Oracle Database (Production)
```sql
-- Create user and grant permissions
CREATE USER genai_reward_bot IDENTIFIED BY password123;
GRANT CONNECT, RESOURCE TO genai_reward_bot;
GRANT CREATE TABLE, CREATE SEQUENCE TO genai_reward_bot;

-- Run schema creation
@common/src/main/resources/db/oracle/schema.sql
```

#### Option B: H2 Database (Development)
Edit `application.properties` in each service to use H2:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=password
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true
```

### 3. Start Services

#### Terminal 1 - Auth Service
```bash
cd auth-service
mvn spring-boot:run
```

#### Terminal 2 - Reward Service
```bash
cd reward-service
mvn spring-boot:run
```

#### Terminal 3 - Frontend
```bash
cd frontend
# Serve using any web server, e.g.:
python -m http.server 8080
# or
npx serve .
```

### 4. Access Application
- **Frontend**: http://localhost:8080
- **Auth Service**: http://localhost:8081
- **Reward Service**: http://localhost:8082

## 🔧 Configuration

### Database Configuration
Each service can be configured in `src/main/resources/application.properties`:

```properties
# Oracle Database
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=genai_reward_bot
spring.datasource.password=password123

# H2 Database (for testing)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=password
```

### JWT Configuration
```properties
jwt.secret=your-secret-key-must-be-256-bits-long
jwt.expiration=86400000
```

## 📱 Usage

### 1. User Login
- Enter your mobile number (e.g., +91XXXXXXXXXX)
- System will create a new user if not exists
- Mock reward data is automatically generated

### 2. Query Examples
- **Points Balance**: "How many reward points do I have?"
- **Expiry Check**: "When do my points expire?"
- **Cashback Options**: "Show me cashback options"
- **Redemption**: "What can I get for 5000 points?"

### 3. Quick Actions
Use the quick action buttons for common queries:
- 💰 Total Points
- ⏰ Expiry Dates  
- 💳 Cashback Options
- 🎁 Redemption Options

## 🗃 Database Schema

### Users Table
```sql
CREATE TABLE users (
    id NUMBER(19) PRIMARY KEY,
    mobile_number VARCHAR2(15) UNIQUE NOT NULL,
    first_name VARCHAR2(50),
    last_name VARCHAR2(50),
    email VARCHAR2(100),
    is_active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Cards Table
```sql
CREATE TABLE cards (
    id NUMBER(19) PRIMARY KEY,
    card_number VARCHAR2(20) UNIQUE NOT NULL,
    card_type VARCHAR2(50),
    vendor_name VARCHAR2(100) NOT NULL,
    vendor_code VARCHAR2(10),
    card_balance NUMBER(15,2) DEFAULT 0,
    currency VARCHAR2(3) DEFAULT 'INR',
    is_active NUMBER(1) DEFAULT 1,
    expiry_date TIMESTAMP,
    cashback_rate NUMBER(5,2),
    user_id NUMBER(19) NOT NULL
);
```

### Reward Points Table
```sql
CREATE TABLE reward_points (
    id NUMBER(19) PRIMARY KEY,
    points_earned NUMBER(10) NOT NULL,
    points_used NUMBER(10) DEFAULT 0,
    points_available NUMBER(10) NOT NULL,
    earning_date TIMESTAMP NOT NULL,
    expiry_date TIMESTAMP,
    source VARCHAR2(100),
    description VARCHAR2(255),
    point_value NUMBER(10,2),
    card_id NUMBER(19) NOT NULL
);
```

## 🔌 API Endpoints

### Auth Service (Port 8081)
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/validate` - Token validation
- `GET /api/auth/user/{mobileNumber}` - Get user by mobile

### Reward Service (Port 8082)
- `GET /api/rewards/cards/{mobileNumber}` - Get user cards
- `GET /api/rewards/points/{mobileNumber}` - Get reward points
- `POST /api/rewards/mock-data/generate` - Generate mock data

### GenAI Service (Port 8083)
- `POST /api/genai/query` - Process natural language query

## 🎯 Mock Data

The system automatically generates realistic mock data including:

### Sample Cards
- **HDFC Bank Credit Card**: ₹25,000 balance, 2.5% cashback
- **Amazon Pay Loyalty Card**: ₹1,250 balance, 5.0% cashback
- **Flipkart Axis Bank Card**: ₹8,750 balance, 4.0% cashback
- **SBI Debit Card**: ₹15,430 balance, 1.0% cashback

### Sample Reward Points
- 3-5 reward point entries per card
- Points ranging from 500-10,000 per transaction
- Various sources: PURCHASE, CASHBACK, BONUS, REFERRAL
- Realistic expiry dates and point values

## 🧪 Testing

### Manual Testing
1. Start all services
2. Open frontend in browser
3. Login with any mobile number
4. Try various natural language queries
5. Check cards summary and point details

### API Testing
```bash
# Test auth service
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"mobileNumber": "+919876543210"}'

# Test reward service
curl -X GET http://localhost:8082/api/rewards/cards/+919876543210 \
  -H "Authorization: Bearer <token>"
```

## 📦 Deployment

### Docker Deployment (Future Enhancement)
```dockerfile
# Dockerfile example for each service
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Production Considerations
- Use external Oracle database
- Configure proper JWT secrets
- Set up load balancing
- Enable HTTPS
- Add monitoring and logging
- Implement rate limiting

## 🔒 Security Features

- JWT-based authentication
- Mobile number as unique identifier
- Token expiration handling
- Input validation and sanitization
- CORS configuration
- SQL injection prevention via JPA

## 🛠 Development

### Project Structure
```
genai-reward-bot/
├── common/                 # Shared entities and DTOs
├── auth-service/          # Authentication microservice
├── reward-service/        # Reward management microservice
├── genai-service/         # GenAI processing microservice
├── frontend/              # HTML/CSS/JS frontend
└── pom.xml               # Parent Maven configuration
```

### Adding New Features
1. Create feature branch
2. Add/modify entities in `common` module
3. Update service logic
4. Add new API endpoints
5. Update frontend if needed
6. Test thoroughly

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the API documentation

## 🚀 Future Enhancements

- Integration with real banking APIs
- Advanced GenAI with GPT/Claude integration
- Mobile app development
- Real-time notifications
- Analytics dashboard
- Multi-language support
- Voice interface
- Blockchain-based loyalty points 