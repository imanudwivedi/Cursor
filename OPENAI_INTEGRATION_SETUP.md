# 🤖 OpenAI Integration Setup Guide

## Overview

This guide explains how to integrate OpenAI's GPT-3.5-turbo model into the GenAI Reward Bot to provide intelligent semantic understanding and natural language responses.

## 🏗 Architecture

```
Frontend (Angular) 
    ↓ HTTP Request
GenAI Service (Spring Boot)
    ↓ API Call
OpenAI GPT-3.5-turbo
    ↓ Intelligent Analysis
Mock Data Service
    ↓ Structured Response
User Interface
```

## 🚀 Quick Setup

### 1. Get OpenAI API Key

1. Visit [OpenAI Platform](https://platform.openai.com)
2. Create account or sign in
3. Navigate to API Keys section
4. Create new secret key
5. Copy the key (starts with `sk-...`)

### 2. Configure Environment Variables

#### Option A: Environment Variable (Recommended)
```bash
export OPENAI_API_KEY="sk-your-actual-openai-api-key-here"
```

#### Option B: Application Properties
```properties
# genai-service/src/main/resources/application.properties
openai.api-key=sk-your-actual-openai-api-key-here
```

### 3. Directory Structure Setup

Create the proper Maven directory structure:

```bash
mkdir -p genai-service/src/main/java/com/genai/rewardbot/genai/{controller,service}
mkdir -p genai-service/src/main/resources
```

Move files to correct locations:
```bash
# Main Application
genai-service/src/main/java/com/genai/rewardbot/genai/GenAiServiceApplication.java

# Controllers
genai-service/src/main/java/com/genai/rewardbot/genai/controller/GenAiController.java

# Services  
genai-service/src/main/java/com/genai/rewardbot/genai/service/OpenAiIntegrationService.java
genai-service/src/main/java/com/genai/rewardbot/genai/service/GenAiProcessingService.java
genai-service/src/main/java/com/genai/rewardbot/genai/service/MockDataService.java
```

### 4. Build and Run

```bash
# Build all services
mvn clean install

# Start GenAI Service
cd genai-service
mvn spring-boot:run

# Start other services in separate terminals
cd auth-service && mvn spring-boot:run
cd reward-service && mvn spring-boot:run

# Start Angular Frontend
cd frontend-angular
ng serve
```

## 🧠 How It Works

### 1. Intent Analysis
When a user asks: *"How many reward points do I have?"*

**OpenAI System Prompt:**
```
You are RewardBot, an AI assistant for reward points management.
User Context: Name: Alex, Points: 47,910, Cards: 3, Balance: $7,401.50
Classify intent: POINTS_BALANCE, POINTS_EXPIRY, CASHBACK_INFO, etc.
```

**OpenAI Response:**
```
INTENT: POINTS_BALANCE
CONFIDENCE: 0.95
```

### 2. Response Generation
**OpenAI System Prompt:**
```
Generate helpful response using user's actual data:
- Total Points: 47,910
- Cards: HDFC Bank (15,420), Amazon Pay (18,750), Flipkart (13,740)
- Expiring: 1,700 points in 30 days
```

**Generated Response:**
```
You have 47,910 reward points! Here's the breakdown:
• HDFC Bank: 15,420 points
• Amazon Pay: 18,750 points  
• Flipkart Axis: 13,740 points

⚠️ 1,700 points expire soon - redeem them first!
```

### 3. Fallback System
If OpenAI is unavailable:
- Service automatically falls back to enhanced rule-based responses
- Maintains full functionality without API dependency
- User experience remains smooth

## 🎯 Key Features

### Intelligent Understanding
- **Context Awareness**: AI understands user's complete reward profile
- **Intent Recognition**: Classifies queries into 6+ categories
- **Semantic Understanding**: Handles complex, conversational queries

### Personalized Responses
- **Real Data Integration**: Uses actual points, balances, expiry dates
- **Smart Recommendations**: AI suggests optimal redemption strategies
- **Proactive Alerts**: Warns about expiring points

### Enhanced User Experience
- **Natural Conversation**: No need for exact command syntax
- **Rich Formatting**: Structured responses with emojis and highlights
- **Quick Actions**: Follow-up suggestions for user convenience

## 📊 Example Interactions

### Complex Query Understanding
**User:** *"I'm planning a vacation next month and want to know the best way to use my points for travel while making sure I don't lose any that are about to expire"*

**AI Analysis:**
- Primary Intent: REDEMPTION_OPTIONS (travel)
- Secondary Intent: POINTS_EXPIRY (urgency)
- Context: Vacation planning, time-sensitive

**AI Response:**
```
🏖️ Perfect timing for your vacation planning! Here's your personalized travel strategy:

⚠️ URGENT - Handle Expiring Points First:
• 1,700 points expire in 30 days
• Redeem these for travel vouchers immediately

✈️ Vacation Redemption Strategy:
• Flight Vouchers: Up to $26,000 value available
• Your remaining 46,210 points = Premium travel experience
• Amazon Pay offers 20% bonus on travel bookings

🎯 Recommended Action Plan:
1. Redeem expiring 1,700 points → $1,020 travel credit
2. Use 35,000 main points → International flight vouchers  
3. Save remaining 10,210 points → Hotel upgrades

💡 Pro Tip: Book travel packages with Amazon Pay for maximum bonus value!
```

## 🔧 Configuration Options

### OpenAI Settings
```properties
# Enable/disable OpenAI
openai.enabled=true

# Model configuration
openai.model=gpt-3.5-turbo
openai.max-tokens=300
openai.temperature=0.7
openai.timeout=30

# For demo without API key
openai.enabled=false
```

### Custom Prompts
Modify system prompts in `OpenAiIntegrationService.java`:

```java
private String buildSystemPrompt(UserContext userContext) {
    return String.format("""
        You are RewardBot, specialized in [YOUR CUSTOM INSTRUCTIONS]
        User Context: %s
        
        Response Guidelines:
        - Be conversational and friendly
        - Use specific numbers from user data
        - Provide actionable recommendations
        """, userContext.getDetailedInfo());
}
```

## 🧪 Testing

### 1. Test OpenAI Connection
```bash
curl -X POST http://localhost:8083/api/genai/test \
  -H "Content-Type: application/json" \
  -d '{"query": "How many points do I have?"}'
```

### 2. Test Full Integration
```bash
curl -X POST http://localhost:8083/api/genai/query \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "mobileNumber": "+1 5551234567",
    "query": "What are my best redemption options?"
  }'
```

### 3. Health Check
```bash
curl http://localhost:8083/api/genai/health
```

Expected response:
```json
{
  "status": "UP",
  "service": "genai-service", 
  "openai_status": true
}
```

## 🎨 Frontend Integration

The Angular frontend automatically uses the new AI-powered responses:

### Enhanced Chat Features
- **Typing Indicators**: Shows when AI is processing
- **Intent Display**: Shows detected intent for transparency  
- **Fallback Handling**: Graceful degradation if API fails
- **Rich Formatting**: HTML rendering of AI responses

### Smart Query Suggestions
```javascript
// Frontend suggests intelligent queries
const smartQueries = [
  "Optimize my cashback strategy",
  "What's the smartest way to redeem my expiring points?", 
  "Compare my cards and recommend best usage",
  "Create a redemption plan for maximum value"
];
```

## 💰 Cost Management

### OpenAI Usage Optimization
- **Efficient Prompts**: Optimized for minimal token usage
- **Response Caching**: Common queries cached locally
- **Fallback Strategy**: Reduces API calls during failures
- **Context Limits**: Structured prompts for consistent results

### Estimated Costs
- **Average Query**: ~150 tokens = $0.0003
- **Daily Usage** (100 queries): ~$0.03
- **Monthly Cost** (3000 queries): ~$0.90

## 🔒 Security

### API Key Protection
- **Environment Variables**: Never commit keys to code
- **Request Validation**: All inputs sanitized
- **Rate Limiting**: Prevents API abuse
- **Error Handling**: Secure error messages

### Data Privacy
- **No PII Storage**: OpenAI doesn't store conversation data
- **Local Processing**: User context built from local mock data
- **JWT Protection**: All requests authenticated

## 🚨 Troubleshooting

### Common Issues

#### 1. OpenAI API Key Invalid
```
Error: Invalid OpenAI API key
Solution: Verify key format (starts with sk-) and account status
```

#### 2. Service Unavailable
```
Error: OpenAI service timeout
Solution: Check internet connection, increase timeout in config
```

#### 3. Token Limit Exceeded
```
Error: Maximum context length exceeded
Solution: Reduce prompt size, optimize context building
```

#### 4. Rate Limit Hit
```
Error: Rate limit exceeded
Solution: Implement request queuing, upgrade OpenAI plan
```

### Debug Mode
Enable detailed logging:
```properties
logging.level.com.genai.rewardbot=DEBUG
logging.level.com.theokanning.openai=DEBUG
```

## 🎯 Advanced Features

### Custom Intent Recognition
Add new intents in `OpenAiIntegrationService.java`:

```java
// Add to system prompt
- INVESTMENT_ADVICE: Questions about point investment strategies
- FAMILY_MANAGEMENT: Multi-user family reward optimization
- BUSINESS_EXPENSE: Corporate card reward management
```

### Multi-language Support
Configure language-specific prompts:

```java
private String getLanguagePrompt(String language) {
    return switch(language) {
        case "es" -> "Responde en español con información de recompensas...";
        case "fr" -> "Répondez en français avec des informations de récompenses...";
        default -> "Respond in English with reward information...";
    };
}
```

### Integration with Real APIs
Replace mock data with real reward service calls:

```java
@Service
public class RealDataService {
    @Autowired
    private RewardServiceClient rewardClient;
    
    public UserContext getUserContext(String mobileNumber) {
        // Call actual reward service APIs
        return rewardClient.getUserRewardData(mobileNumber);
    }
}
```

## 📈 Monitoring

### Key Metrics to Track
- **API Response Time**: OpenAI call latency
- **Success Rate**: Successful vs failed queries  
- **Intent Accuracy**: Manual validation of intent detection
- **User Satisfaction**: Feedback on AI responses
- **Cost Tracking**: Monthly OpenAI usage

### Analytics Dashboard
Implement usage tracking:

```java
@Component
public class ChatAnalytics {
    public void trackQuery(String intent, double confidence, long responseTime) {
        // Log metrics for analysis
    }
}
```

## 🔮 Future Enhancements

### Planned Features
1. **GPT-4 Integration** - Enhanced reasoning capabilities
2. **Voice Interface** - Speech-to-text with OpenAI Whisper
3. **Image Analysis** - Receipt scanning for reward tracking
4. **Predictive Analytics** - AI-powered spending recommendations
5. **Multi-modal Chat** - Text, voice, and image interactions

### Scalability
- **Microservice Architecture** - Independent scaling
- **Caching Layer** - Redis for frequent queries
- **Load Balancing** - Multiple GenAI service instances
- **Circuit Breaker** - Resilient external API calls

## 📞 Support

### Getting Help
1. **Check Logs**: Review application logs for detailed errors
2. **Test Endpoints**: Use provided curl commands for testing
3. **Verify Configuration**: Ensure all environment variables set
4. **Fallback Mode**: Test with `openai.enabled=false`

### Contact Information
- Technical Issues: Check console logs and API responses
- OpenAI Problems: Verify API key and account status
- Integration Help: Review this guide and example code

---

🎉 **Congratulations!** You now have an AI-powered reward bot that understands natural language and provides intelligent, personalized responses using your actual reward data! 