// GenAI Reward Bot Frontend JavaScript

// Configuration
const API_CONFIG = {
    AUTH_SERVICE_URL: 'http://localhost:8081/api/auth',
    REWARD_SERVICE_URL: 'http://localhost:8082/api/rewards',
    GENAI_SERVICE_URL: 'http://localhost:8083/api/genai'
};

// Global state
let currentUser = null;
let authToken = null;

// DOM Elements
const loginSection = document.getElementById('loginSection');
const dashboardSection = document.getElementById('dashboardSection');
const loginForm = document.getElementById('loginForm');
const chatForm = document.getElementById('chatForm');
const chatMessages = document.getElementById('chatMessages');
const chatInput = document.getElementById('chatInput');
const userName = document.getElementById('userName');
const cardsContainer = document.getElementById('cardsContainer');
const loginError = document.getElementById('loginError');
const loadingOverlay = document.getElementById('loadingOverlay');

// Initialize app
document.addEventListener('DOMContentLoaded', function() {
    checkExistingSession();
    setupEventListeners();
});

function setupEventListeners() {
    // Login form
    loginForm.addEventListener('submit', handleLogin);
    
    // Chat form
    chatForm.addEventListener('submit', handleChatSubmit);
    
    // Logout button
    document.getElementById('logoutBtn').addEventListener('click', handleLogout);
    
    // Enter key in chat input
    chatInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleChatSubmit(e);
        }
    });
}

async function handleLogin(e) {
    e.preventDefault();
    const mobileNumber = document.getElementById('mobileNumber').value.trim();
    
    if (!mobileNumber) {
        showError('Please enter a valid mobile number');
        return;
    }
    
    showLoading(true);
    clearError();
    
    try {
        // First try to authenticate
        let response = await authenticateUser(mobileNumber);
        
        if (!response.success) {
            // If user doesn't exist, register them
            const firstName = prompt('Welcome! Please enter your first name:') || 'User';
            const lastName = prompt('Please enter your last name:') || '';
            const email = prompt('Please enter your email (optional):') || '';
            
            response = await registerUser(mobileNumber, firstName, lastName, email);
        }
        
        if (response.success) {
            currentUser = response.user;
            authToken = response.token;
            
            // Store session
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            // Generate mock data if needed
            await generateMockData(mobileNumber);
            
            showDashboard();
            await loadUserCards();
        } else {
            showError(response.message || 'Login failed');
        }
    } catch (error) {
        console.error('Login error:', error);
        showError('Connection error. Please try again.');
    } finally {
        showLoading(false);
    }
}

async function authenticateUser(mobileNumber) {
    const response = await fetch(`${API_CONFIG.AUTH_SERVICE_URL}/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ mobileNumber })
    });
    
    return await response.json();
}

async function registerUser(mobileNumber, firstName, lastName, email) {
    const response = await fetch(`${API_CONFIG.AUTH_SERVICE_URL}/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            mobileNumber,
            firstName,
            lastName,
            email
        })
    });
    
    return await response.json();
}

async function generateMockData(mobileNumber) {
    try {
        await fetch(`${API_CONFIG.REWARD_SERVICE_URL}/mock-data/generate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ mobileNumber })
        });
    } catch (error) {
        console.log('Mock data generation skipped:', error.message);
    }
}

function showDashboard() {
    loginSection.classList.add('hidden');
    dashboardSection.classList.remove('hidden');
    userName.textContent = currentUser.firstName || currentUser.mobileNumber;
}

async function loadUserCards() {
    try {
        const response = await fetch(`${API_CONFIG.REWARD_SERVICE_URL}/cards/${currentUser.mobileNumber}`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.ok) {
            const data = await response.json();
            displayCards(data.cards || []);
        }
    } catch (error) {
        console.error('Error loading cards:', error);
        // Show mock cards if API fails
        displayMockCards();
    }
}

function displayCards(cards) {
    if (!cards || cards.length === 0) {
        displayMockCards();
        return;
    }
    
    cardsContainer.innerHTML = cards.map(card => `
        <div class="reward-card">
            <h4>${card.vendorName}</h4>
            <div class="card-info">
                <span>Type: ${card.cardType}</span>
                <span>Balance: ₹${card.cardBalance}</span>
            </div>
            <div class="card-info">
                <span>Card: ${card.maskedCardNumber}</span>
                <span>Cashback: ${card.cashbackRate}%</span>
            </div>
            <div class="points-info">
                <strong>Reward Points: ${card.totalRewardPoints || 0}</strong>
                ${card.expiringSoonPoints ? `<br><small>⚠️ ${card.expiringSoonPoints} points expiring soon</small>` : ''}
            </div>
        </div>
    `).join('');
}

function displayMockCards() {
    const mockCards = [
        {
            vendorName: 'HDFC Bank',
            cardType: 'CREDIT',
            cardBalance: '25,000.50',
            maskedCardNumber: '**** **** **** 1234',
            cashbackRate: '2.5',
            totalRewardPoints: 12500,
            expiringSoonPoints: 500
        },
        {
            vendorName: 'Amazon Pay',
            cardType: 'LOYALTY',
            cardBalance: '1,250.75',
            maskedCardNumber: '**** **** **** 5678',
            cashbackRate: '5.0',
            totalRewardPoints: 8750,
            expiringSoonPoints: null
        },
        {
            vendorName: 'Flipkart Axis Bank',
            cardType: 'CREDIT',
            cardBalance: '8,750.25',
            maskedCardNumber: '**** **** **** 9012',
            cashbackRate: '4.0',
            totalRewardPoints: 15200,
            expiringSoonPoints: 1200
        }
    ];
    
    displayCards(mockCards);
}

async function handleChatSubmit(e) {
    e.preventDefault();
    const query = chatInput.value.trim();
    
    if (!query) return;
    
    // Add user message
    addMessage(query, 'user');
    chatInput.value = '';
    
    // Show loading
    const sendBtn = document.querySelector('#chatForm button');
    sendBtn.classList.add('loading');
    
    try {
        const response = await processQuery(query);
        addMessage(response, 'bot');
    } catch (error) {
        console.error('Chat error:', error);
        addMessage('Sorry, I encountered an error processing your request. Please try again.', 'bot');
    } finally {
        sendBtn.classList.remove('loading');
    }
}

async function processQuery(query) {
    try {
        // Try to use GenAI service
        const response = await fetch(`${API_CONFIG.GENAI_SERVICE_URL}/query`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({
                mobileNumber: currentUser.mobileNumber,
                query: query
            })
        });
        
        if (response.ok) {
            const data = await response.json();
            return data.response || 'I apologize, but I couldn\'t process your request at the moment.';
        }
    } catch (error) {
        console.log('GenAI service unavailable, using mock responses');
    }
    
    // Fallback to mock responses
    return generateMockResponse(query);
}

function generateMockResponse(query) {
    const queryLower = query.toLowerCase();
    
    if (queryLower.includes('points') && (queryLower.includes('how many') || queryLower.includes('total'))) {
        return `Based on your current cards, you have a total of <strong>36,450 reward points</strong> across all your cards:
        <br><br>• HDFC Bank: 12,500 points
        <br>• Amazon Pay: 8,750 points  
        <br>• Flipkart Axis Bank: 15,200 points
        <br><br>You have <strong>1,700 points expiring within 30 days</strong>. Consider redeeming them soon!`;
    }
    
    if (queryLower.includes('expir')) {
        return `Here are your points expiry details:
        <br><br>⚠️ <strong>Expiring Soon (within 30 days):</strong>
        <br>• HDFC Bank: 500 points (expires in 15 days)
        <br>• Flipkart Axis Bank: 1,200 points (expires in 28 days)
        <br><br>📅 <strong>Future Expiries:</strong>
        <br>• Amazon Pay: 8,750 points (expires in 8 months)
        <br>• HDFC Bank: 12,000 points (expires in 11 months)
        <br><br>I recommend redeeming the expiring points first!`;
    }
    
    if (queryLower.includes('cashback')) {
        return `Here are your cashback options by card:
        <br><br>💳 <strong>Current Cashback Rates:</strong>
        <br>• Amazon Pay: 5.0% (highest rate!)
        <br>• Flipkart Axis Bank: 4.0%
        <br>• HDFC Bank: 2.5%
        <br><br>💰 <strong>Estimated cashback value:</strong>
        <br>Your 36,450 points are worth approximately ₹18,225 in cashback value.
        <br><br>Would you like me to show you the best redemption options?`;
    }
    
    if (queryLower.includes('5000') && queryLower.includes('points')) {
        return `With 5,000 points, here's what you can get:
        <br><br>🎁 <strong>Redemption Options:</strong>
        <br>• ₹2,500 direct cashback to bank account
        <br>• ₹3,000 Amazon gift voucher (20% bonus!)
        <br>• ₹2,750 Flipkart voucher (10% bonus)
        <br>• 5,000 frequent flyer miles
        <br>• Electronic items worth ₹4,000-5,000
        <br><br>💡 <strong>Recommendation:</strong> Amazon gift voucher gives you the best value with 20% bonus!`;
    }
    
    if (queryLower.includes('redeem') || queryLower.includes('redemption')) {
        return `Based on your 36,450 total points, here are my top recommendations:
        <br><br>🏆 <strong>Best Value Options:</strong>
        <br>1. Amazon Gift Voucher: ₹21,870 (20% bonus)
        <br>2. Flight Booking Credit: ₹20,000 value
        <br>3. Direct Bank Transfer: ₹18,225
        <br>4. Shopping Vouchers: ₹19,500-22,000
        <br><br>⚠️ <strong>Priority:</strong> Redeem your 1,700 expiring points first!
        <br><br>Would you like me to help you start the redemption process?`;
    }
    
    if (queryLower.includes('hello') || queryLower.includes('hi')) {
        return `Hello! 👋 I'm your GenAI Reward Bot. I can help you with:
        <br><br>• Check your reward points balance
        <br>• Find points expiry dates
        <br>• Suggest best redemption options
        <br>• Show cashback opportunities
        <br>• Compare card benefits
        <br><br>What would you like to know about your rewards today?`;
    }
    
    return `I understand you're asking about "${query}". Here's what I can help you with:
    <br><br>• <strong>Points Balance:</strong> Ask "How many points do I have?"
    <br>• <strong>Expiry Info:</strong> Ask "When do my points expire?"
    <br>• <strong>Cashback:</strong> Ask "Show me cashback options"
    <br>• <strong>Redemption:</strong> Ask "What can I get for X points?"
    <br><br>Please try one of these questions, and I'll provide detailed information about your rewards!`;
}

function addMessage(message, sender) {
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${sender}-message`;
    messageDiv.innerHTML = `
        <div class="message-content">
            <strong>${sender === 'user' ? 'You' : 'Reward Bot'}:</strong> ${message}
        </div>
    `;
    
    chatMessages.appendChild(messageDiv);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

function askQuestion(question) {
    chatInput.value = question;
    handleChatSubmit(new Event('submit'));
}

function checkExistingSession() {
    const storedToken = localStorage.getItem('authToken');
    const storedUser = localStorage.getItem('currentUser');
    
    if (storedToken && storedUser) {
        authToken = storedToken;
        currentUser = JSON.parse(storedUser);
        showDashboard();
        loadUserCards();
    }
}

function handleLogout() {
    currentUser = null;
    authToken = null;
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    
    dashboardSection.classList.add('hidden');
    loginSection.classList.remove('hidden');
    
    // Clear form
    document.getElementById('mobileNumber').value = '';
    clearError();
    
    // Clear chat
    chatMessages.innerHTML = `
        <div class="message bot-message">
            <div class="message-content">
                <strong>Reward Bot:</strong> Hi! I'm here to help you with your reward points. 
                What would you like to know?
            </div>
        </div>
    `;
}

function showLoading(show) {
    if (show) {
        loadingOverlay.classList.remove('hidden');
    } else {
        loadingOverlay.classList.add('hidden');
    }
}

function showError(message) {
    loginError.textContent = message;
    loginError.style.display = 'block';
}

function clearError() {
    loginError.textContent = '';
    loginError.style.display = 'none';
}

// Utility function to format numbers
function formatNumber(num) {
    return new Intl.NumberFormat('en-IN').format(num);
}

// Utility function to format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR'
    }).format(amount);
} 