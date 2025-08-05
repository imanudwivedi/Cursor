# 🔑 OpenAI API Key Setup Guide

## 🚨 Issues Fixed:
1. ✅ **Datasource Error**: Disabled unnecessary database auto-configuration
2. ✅ **API Key Setup**: Multiple secure methods provided

## 🔐 Method 1: Environment Variable (RECOMMENDED)

### Windows (PowerShell)
```powershell
# Set for current session
$env:OPENAI_API_KEY="sk-your-actual-openai-key-here"

# Set permanently (requires restart)
[Environment]::SetEnvironmentVariable("OPENAI_API_KEY", "sk-your-actual-openai-key-here", "User")
```

### Windows (Command Prompt)
```cmd
set OPENAI_API_KEY=sk-your-actual-openai-key-here
```

### macOS/Linux (Terminal)
```bash
# Set for current session
export OPENAI_API_KEY="sk-your-actual-openai-key-here"

# Set permanently (add to ~/.bashrc or ~/.zshrc)
echo 'export OPENAI_API_KEY="sk-your-actual-openai-key-here"' >> ~/.bashrc
source ~/.bashrc
```

### Verify Environment Variable
```bash
# Windows PowerShell
echo $env:OPENAI_API_KEY

# Windows Command Prompt  
echo %OPENAI_API_KEY%

# macOS/Linux
echo $OPENAI_API_KEY
```

## 🔐 Method 2: Direct in Properties File (DEV ONLY)

**⚠️ WARNING: Not recommended for production or version control**

Edit `genai-service/src/main/resources/application.properties`:
```properties
# Uncomment and replace with your actual key
openai.api-key=sk-your-actual-openai-key-here
```

## 🔐 Method 3: External Properties File (PRODUCTION)

Create a separate properties file outside your project:

### Create External File
```bash
# Create secure directory
mkdir ~/app-config

# Create properties file
echo "openai.key=sk-your-actual-openai-key-here" > ~/app-config/openai.properties
```

### Update Application Properties
```properties
# In genai-service/src/main/resources/application.properties
spring.config.import=optional:file:${user.home}/app-config/openai.properties
openai.api-key=${openai.key:default-key}
```

## 🔐 Method 4: Command Line Argument

Start the service with the key as an argument:
```bash
cd genai-service
mvn spring-boot:run -Dspring-boot.run.arguments="--openai.api-key=sk-your-actual-openai-key-here"
```

## 🔐 Method 5: IDE Configuration

### IntelliJ IDEA
1. Edit Run Configuration
2. Add Environment Variable: `OPENAI_API_KEY=sk-your-key-here`
3. Run the application

### VS Code
Create `.vscode/launch.json`:
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "GenAI Service",
            "request": "launch",
            "mainClass": "com.genai.rewardbot.genai.GenAiServiceApplication",
            "env": {
                "OPENAI_API_KEY": "sk-your-actual-openai-key-here"
            }
        }
    ]
}
```

## 🧪 Testing Your Setup

### 1. Check Service Health
```bash
curl http://localhost:8083/api/genai/health
```

Expected response (with OpenAI working):
```json
{
  "status": "UP",
  "service": "genai-service",
  "openai_status": true
}
```

### 2. Test OpenAI Integration
```bash
curl -X POST http://localhost:8083/api/genai/test \
  -H "Content-Type: application/json" \
  -d '{"query": "How many points do I have?"}'
```

Expected response:
```json
{
  "success": true,
  "response": "You have a total of **47,910 reward points** across all your cards! 🎉...",
  "query": "How many points do I have?"
}
```

## 🚨 Troubleshooting

### Error: "Invalid OpenAI API Key"
```
✅ Solutions:
1. Verify key starts with "sk-"
2. Check for extra spaces or quotes
3. Ensure environment variable is set correctly
4. Restart your terminal/IDE after setting env var
```

### Error: "DataSource URL not specified"
```
✅ Solution: Already fixed!
The application.properties now excludes database auto-configuration:
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

### Error: "OpenAI service unavailable"
```
✅ Solutions:
1. Check internet connection
2. Verify OpenAI API key is valid and has credits
3. Check OpenAI status page
4. Try enabling fallback mode: openai.enabled=false
```

## 🔒 Security Best Practices

### ✅ DO:
- Use environment variables for production
- Keep keys in external config files
- Add API keys to .gitignore
- Rotate keys regularly
- Monitor usage in OpenAI dashboard

### ❌ DON'T:
- Commit keys to version control
- Share keys in chat/email
- Use same key across environments
- Hardcode keys in source code

## 📂 .gitignore Configuration

Add these to your `.gitignore`:
```gitignore
# API Keys and Secrets
**/application-local.properties
**/openai.properties
**/.env
**/secrets/

# IDE specific
.vscode/
.idea/
*.iml
```

## 🚀 Quick Start Commands

Once you've set your API key using **Method 1** (Environment Variable):

```bash
# 1. Verify environment variable is set
echo $OPENAI_API_KEY  # Should show your key

# 2. Start all services
cd auth-service && mvn spring-boot:run &
cd reward-service && mvn spring-boot:run &
cd genai-service && mvn spring-boot:run &

# 3. Start frontend
cd frontend-angular && ng serve

# 4. Test in browser
# Navigate to http://localhost:4200
# Login with: +1 5551234567
# Verification: 123456
# Ask: "How many points do I have?"
```

## 💰 OpenAI Usage Monitoring

### Check Usage
1. Visit [OpenAI Usage Dashboard](https://platform.openai.com/usage)
2. Monitor daily/monthly costs
3. Set usage limits if needed

### Expected Costs
- **Per Query**: ~$0.0003 (150 tokens)
- **Daily** (100 queries): ~$0.03
- **Monthly** (3000 queries): ~$0.90

## 🎯 Fallback Mode (No API Key Needed)

If you want to test without OpenAI first:

```properties
# In application.properties
openai.enabled=false
```

This will use enhanced mock responses that still provide intelligent-looking answers!

---

## ✅ Final Checklist

- [ ] OpenAI API key obtained from platform.openai.com
- [ ] Environment variable `OPENAI_API_KEY` set
- [ ] GenAI service starts without datasource errors  
- [ ] Health check returns `"openai_status": true`
- [ ] Test query returns AI-generated response
- [ ] Frontend chat works with natural language

🎉 **You're all set!** Your AI-powered reward bot is ready to understand natural language and provide intelligent responses! 