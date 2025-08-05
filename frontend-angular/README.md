# 🎁 GenAI Reward Bot - Angular Frontend

A modern, responsive Angular frontend for the GenAI Reward Bot application with a beautiful dark theme UI and AI-powered chat interface.

## 🚀 Features

- **🔐 Mobile Number Authentication** with verification code
- **📊 Real-time Dashboard** with reward points and card balances
- **🤖 AI-Powered Chat Interface** for natural language queries
- **📈 Analytics Dashboard** with charts and insights
- **💳 Card Management** with multiple vendor support
- **📱 Responsive Design** for mobile and desktop
- **🎨 Modern UI** with dark theme and smooth animations

## 🛠 Setup Instructions

### Prerequisites
- Node.js 18+ and npm
- Angular CLI 17+

### 1. Install Dependencies
```bash
cd frontend-angular
npm install
```

### 2. Install Angular CLI (if not installed)
```bash
npm install -g @angular/cli@17
```

### 3. Development Server
```bash
ng serve
```
The app will be available at `http://localhost:4200`

### 4. Build for Production
```bash
ng build --prod
```

## 🏗 Project Structure

```
src/
├── app/
│   ├── components/
│   │   ├── login/              # Mobile verification login
│   │   ├── dashboard/          # Main dashboard with stats
│   │   ├── analytics/          # Analytics and charts
│   │   ├── chat/              # AI chat interface
│   │   └── cards/             # Card management
│   ├── services/
│   │   ├── auth.service.ts    # Authentication & JWT
│   │   ├── reward.service.ts  # Reward data management
│   │   └── chat.service.ts    # AI chat functionality
│   ├── guards/
│   │   └── auth.guard.ts      # Route protection
│   └── app.module.ts          # Main module
└── styles/                    # Global styles
```

## 📱 Usage

### 1. Login Process
1. Enter your mobile number (+1 5551234567)
2. Click "Send Verification Code"
3. Enter verification code: **123456** (demo)
4. Access your dashboard

### 2. Dashboard Features
- **Total Reward Points**: 47,910 across all cards
- **Total Balance**: $7,401.50 available to spend
- **Active Cards**: 3 cards from different vendors
- **Redemption Categories**: Vacation, Dining, Shopping, General

### 3. AI Chat Interface
Ask natural language questions:
- "How many reward points do I have?"
- "When do my points expire?"
- "Show me cashback options"
- "What can I get for 5000 points?"

## 🎨 UI Design Features

### Login Screen
- Dark gradient background
- Glassmorphism card design
- Mobile number verification flow
- Loading states and animations

### Dashboard
- Blue gradient header
- Stats cards with hover effects
- Redemption category cards
- Credit card-style reward cards
- Responsive grid layouts

### Chat Interface
- Overlay modal design
- Message bubbles
- Real-time AI responses
- Smart query suggestions

## 🔧 Configuration

### Backend Integration
Update API URLs in services:

```typescript
// src/app/services/auth.service.ts
private readonly API_URL = 'http://localhost:8081/api/auth';

// src/app/services/reward.service.ts
private readonly API_URL = 'http://localhost:8082/api/rewards';

// src/app/services/chat.service.ts
private readonly API_URL = 'http://localhost:8083/api/genai';
```

### Environment Configuration
```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  authServiceUrl: 'http://localhost:8081',
  rewardServiceUrl: 'http://localhost:8082',
  genaiServiceUrl: 'http://localhost:8083'
};
```

## 🚀 Deployment

### Development
```bash
ng serve --host 0.0.0.0 --port 4200
```

### Production Build
```bash
ng build --configuration=production
```

### Docker Deployment
```dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN ng build --prod

FROM nginx:alpine
COPY --from=builder /app/dist/* /usr/share/nginx/html/
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 📊 Mock Data

The frontend includes comprehensive mock data for demo purposes:

### Sample User
- Name: Alex Johnson
- Mobile: +1 5551234567
- Verification Code: 123456

### Sample Cards
1. **HDFC Bank** - Credit Card - 15,420 points
2. **Amazon Pay** - Loyalty Card - 18,750 points  
3. **Flipkart Axis Bank** - Credit Card - 13,740 points

### Analytics Data
- Monthly activity charts
- Earning vs redemption trends
- Category-wise spending
- Transaction summaries

## 🔒 Security Features

- JWT token management
- Route guards for protected pages
- Input validation and sanitization
- Secure storage of authentication data
- CORS configuration

## 🎯 Performance Optimizations

- Lazy loading for routes
- OnPush change detection strategy
- Optimized bundle size
- Service worker for caching
- Progressive Web App features

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📞 Support

For issues and questions:
- Check the console for error messages
- Verify backend services are running
- Ensure correct API URLs in configuration
- Test with provided mock data first 