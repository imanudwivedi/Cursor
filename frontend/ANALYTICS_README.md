# Analytics Dashboard - Frontend Documentation

## Overview

The Analytics Dashboard is a modern, dark-themed frontend that replicates the spending analytics UI design. It provides comprehensive insights into reward points, credit card usage, and transaction analytics.

## Features

### 🎯 Key Metrics Cards
- **Total Earned**: Lifetime reward points earned
- **Total Redeemed**: Lifetime reward points redeemed  
- **This Month**: Points earned in current month
- **Transactions**: Total number of transactions

### 📊 Interactive Analytics
- **Overview Tab**: Monthly points activity chart with earned vs redeemed comparison
- **Categories Tab**: Spending breakdown by category (Shopping, Dining, Fuel, Entertainment)
- **Trends Tab**: Analytics trends and insights

### 💳 Credit Card Display
- Real-time credit card information
- Usage bars showing credit utilization
- Available credit amounts
- Reward points balances
- Progress tracking to next reward

### 🎨 Modern Design
- Dark theme with blue accent colors
- Glassmorphism effects with backdrop blur
- Animated cards and hover effects
- Responsive design for all devices
- Chart.js integration for data visualization

## File Structure

```
frontend/
├── dashboard.html          # Main analytics dashboard page
├── dashboard.css           # Styling for analytics dashboard  
├── dashboard.js            # JavaScript functionality and API integration
├── index.html              # Updated main page with analytics link
├── script.js               # Updated with navigation function
└── ANALYTICS_README.md     # This documentation
```

## API Integration

The dashboard integrates with your existing backend services:

### Auth Service (Port 8081)
- User authentication and session management
- Token validation

### Reward Service (Port 8082)
- `/api/rewards/cards/{mobileNumber}` - Fetch user's credit cards
- `/api/rewards/points/{mobileNumber}` - Fetch reward points data

### Data Processing
- Automatically calculates metrics from API responses
- Falls back to mock data if APIs are unavailable
- Real-time updates when data changes

## Getting Started

### 1. Prerequisites
Ensure your backend services are running:
```bash
# Terminal 1 - Auth Service
cd auth-service
mvn spring-boot:run

# Terminal 2 - Reward Service  
cd reward-service
mvn spring-boot:run
```

### 2. Start Frontend
```bash
cd frontend
# Using Python
python -m http.server 8080

# Or using Node.js
npx serve .

# Or using any other web server
```

### 3. Access the Application
1. Open http://localhost:8080
2. Login with any mobile number (e.g., +919876543210)
3. Click on **📊 Spending Analytics** button to access the new dashboard

## Navigation

### From Main Dashboard
- Click the "📊 Spending Analytics" button in the Quick Actions section

### From Analytics Dashboard  
- Click "← Back to Dashboard" in the header to return to the main chat interface

## Mock Data

If backend APIs are unavailable, the dashboard displays realistic mock data:

### Sample Cards
- **CitiBank Premium Rewards**: $12,549.50/$15,000.00 used, 15,750 points
- **Chase Travel Elite**: $3,249.75/$5,000.00 used, 8,320 points  
- **American Express Gold**: $6,799.25/$10,000.00 used, 12,450 points

### Sample Metrics
- Total Earned: 6,745 points
- Total Redeemed: 1,800 points
- This Month: 0 points
- Transactions: 30

## Customization

### Card Themes
Cards automatically get themed based on vendor:
- **CitiBank**: Blue gradient (`citibank` class)
- **Chase**: Green gradient (`chase` class)  
- **American Express**: Purple gradient (`amex` class)

### Chart Configuration
Monthly chart data can be customized in `dashboard.js`:
```javascript
const chartData = {
    labels: ['Sep', 'Oct', 'Nov', 'Dec', 'Jan', 'Feb', 'Mar'],
    datasets: [
        {
            label: 'Earned',
            data: [1150, 850, 1050, 1290, 1200, 1100, 950],
            backgroundColor: '#3b82f6'
        },
        {
            label: 'Redeemed', 
            data: [450, 300, 200, 350, 400, 250, 150],
            backgroundColor: '#ef4444'
        }
    ]
};
```

### Color Scheme
Main colors used:
- **Background**: Dark gradient (#1a1a2e to #0f0f23)
- **Primary**: Blue (#3b82f6)
- **Success**: Green (#10b981)  
- **Danger**: Red (#ef4444)
- **Warning**: Yellow (#fbbf24)

## Responsive Design

The dashboard is fully responsive:

### Desktop (>768px)
- 4-column metrics grid
- 2-3 column card grid
- Full chart height (400px)

### Tablet (768px)
- 2-column metrics grid
- 2-column card grid
- Reduced chart height (300px)

### Mobile (<480px)  
- Single column layout
- Stacked card elements
- Vertical tab navigation

## Browser Support

- **Chrome**: 80+
- **Firefox**: 75+
- **Safari**: 13+
- **Edge**: 80+

## Dependencies

### External Libraries
- **Chart.js**: Data visualization
- **Font Awesome**: Icons
- **Modern CSS**: Grid, Flexbox, backdrop-filter

### No Build Process Required
- Pure HTML/CSS/JavaScript
- No compilation or bundling needed
- Works directly in any modern web server

## Performance

### Optimizations
- Lazy loading of chart data
- Debounced API calls
- CSS animations with hardware acceleration
- Minimal DOM manipulation

### Loading States
- Loading overlay during data fetch
- Skeleton states for cards
- Graceful fallback to mock data

## Security

### Authentication
- JWT token validation
- Automatic redirect to login if not authenticated
- Secure storage in localStorage

### API Calls
- Authorization headers included
- Error handling for failed requests
- No sensitive data exposed in frontend

## Troubleshooting

### Common Issues

1. **Blank Dashboard**
   - Check if backend services are running
   - Verify JWT token in localStorage
   - Check browser console for errors

2. **Charts Not Loading**
   - Ensure Chart.js CDN is accessible
   - Check network connectivity
   - Verify canvas element exists

3. **API Errors**
   - Check CORS configuration on backend
   - Verify API endpoints are accessible
   - Check authentication token validity

### Debug Mode
Enable console logging by setting:
```javascript
// In dashboard.js
const DEBUG_MODE = true;
```

## Future Enhancements

### Planned Features
- Real-time notifications
- Export analytics data
- Custom date range selection
- Advanced filtering options
- Mobile app integration

### API Enhancements
- WebSocket connections for real-time updates
- Batch API calls for better performance
- Caching strategies
- Offline mode support

## Contributing

### Code Style
- Use ES6+ features
- Follow existing naming conventions
- Add comments for complex logic
- Maintain responsive design patterns

### Testing
- Test on multiple browsers
- Verify mobile responsiveness  
- Check API integration
- Validate with real backend data

---

For questions or issues, please refer to the main project documentation or contact the development team. 