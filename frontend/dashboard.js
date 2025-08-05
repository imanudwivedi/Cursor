// Dashboard Analytics JavaScript

// Configuration
const API_CONFIG = {
    AUTH_SERVICE_URL: 'http://localhost:8081/api/auth',
    REWARD_SERVICE_URL: 'http://localhost:8082/api/rewards'
};

// Global state
let currentUser = null;
let authToken = null;
let monthlyChart = null;

// Initialize dashboard
document.addEventListener('DOMContentLoaded', function() {
    checkAuthentication();
    setupEventListeners();
});

function checkAuthentication() {
    const storedToken = localStorage.getItem('authToken');
    const storedUser = localStorage.getItem('currentUser');
    
    if (!storedToken || !storedUser) {
        // Redirect to login page
        window.location.href = 'index.html';
        return;
    }
    
    authToken = storedToken;
    currentUser = JSON.parse(storedUser);
    
    // Set user initials
    const userInitials = document.getElementById('userInitials');
    if (currentUser.firstName && currentUser.lastName) {
        userInitials.textContent = currentUser.firstName.charAt(0) + currentUser.lastName.charAt(0);
    } else {
        userInitials.textContent = 'U';
    }
    
    // Load dashboard data
    loadDashboardData();
}

function setupEventListeners() {
    // Tab navigation
    const tabButtons = document.querySelectorAll('.tab-btn');
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const tabName = this.getAttribute('data-tab');
            switchTab(tabName);
        });
    });
    
    // Settings button
    const settingsBtn = document.querySelector('.settings-btn');
    if (settingsBtn) {
        settingsBtn.addEventListener('click', function() {
            // Add settings functionality later
            console.log('Settings clicked');
        });
    }
}

function switchTab(tabName) {
    // Remove active class from all tabs and content
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
    
    // Add active class to clicked tab and corresponding content
    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
    document.getElementById(tabName).classList.add('active');
}

async function loadDashboardData() {
    showLoading(true);
    
    try {
        // Load all data in parallel
        const [cardsData, pointsData] = await Promise.all([
            loadUserCards(),
            loadUserPoints()
        ]);
        
        // Process and display data
        if (cardsData) {
            displayCards(cardsData);
            updateMetrics(cardsData, pointsData);
        }
        
        // Initialize chart
        initializeChart();
        
    } catch (error) {
        console.error('Error loading dashboard data:', error);
        // Show mock data if API fails
        displayMockData();
    } finally {
        showLoading(false);
    }
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
            return data.cards || [];
        }
    } catch (error) {
        console.error('Error loading cards:', error);
    }
    return null;
}

async function loadUserPoints() {
    try {
        const response = await fetch(`${API_CONFIG.REWARD_SERVICE_URL}/points/${currentUser.mobileNumber}`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.ok) {
            const data = await response.json();
            return data.points || [];
        }
    } catch (error) {
        console.error('Error loading points:', error);
    }
    return null;
}

function displayCards(cards) {
    const cardsContainer = document.getElementById('cardsContainer');
    
    if (!cards || cards.length === 0) {
        displayMockCards();
        return;
    }
    
    cardsContainer.innerHTML = cards.map((card, index) => {
        const cardClass = getCardClass(card.vendorName);
        const maskedNumber = maskCardNumber(card.cardNumber);
        const usagePercentage = card.cardBalance && card.creditLimit ? 
            (parseFloat(card.cardBalance) / parseFloat(card.creditLimit) * 100).toFixed(1) : 0;
        
        return `
            <div class="credit-card ${cardClass}">
                <div class="card-header">
                    <div class="card-brand">
                        <div class="brand-logo">
                            ${getCardLogo(card.vendorName)}
                        </div>
                        <div class="brand-info">
                            <h3>${card.vendorName}</h3>
                            <p>${card.cardType === 'CREDIT' ? 'Premium Rewards' : card.cardType}</p>
                        </div>
                    </div>
                    <div class="card-status">active</div>
                </div>
                
                <div class="card-number">
                    Card Number
                    <div>${maskedNumber}</div>
                </div>
                
                <div class="card-details">
                    <div class="detail-group">
                        <h4>Credit Usage</h4>
                        <p>$${formatNumber(card.cardBalance || 0)} of $${formatNumber(card.creditLimit || 50000)} used</p>
                        <div class="usage-bar">
                            <div class="usage-fill" style="width: ${usagePercentage}%"></div>
                        </div>
                    </div>
                    <div class="detail-group">
                        <h4>Available</h4>
                        <p class="available-amount">$${formatNumber((card.creditLimit || 50000) - (card.cardBalance || 0))}</p>
                    </div>
                </div>
                
                <div class="card-footer">
                    <div class="reward-points">
                        <div class="points-label">Reward Points</div>
                        <div class="points-value">${formatNumber(card.totalRewardPoints || 0)}</div>
                        <div class="points-expiry">Expires: ${getExpiryDate()}</div>
                    </div>
                    <div class="progress-section">
                        <div class="progress-label">
                            <span>Progress to next reward</span>
                            <span>${getProgressPercentage()}%</span>
                        </div>
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: ${getProgressPercentage()}%"></div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function displayMockCards() {
    const cardsContainer = document.getElementById('cardsContainer');
    cardsContainer.innerHTML = `
        <div class="credit-card citibank">
            <div class="card-header">
                <div class="card-brand">
                    <div class="brand-logo">C</div>
                    <div class="brand-info">
                        <h3>CitiBank</h3>
                        <p>Premium Rewards</p>
                    </div>
                </div>
                <div class="card-status">active</div>
            </div>
            
            <div class="card-number">
                Card Number
                <div>****3456</div>
            </div>
            
            <div class="card-details">
                <div class="detail-group">
                    <h4>Credit Usage</h4>
                    <p>$12549.50 of $15000.00 used</p>
                    <div class="usage-bar">
                        <div class="usage-fill" style="width: 83.7%"></div>
                    </div>
                </div>
                <div class="detail-group">
                    <h4>Available</h4>
                    <p class="available-amount">$2450.50</p>
                </div>
            </div>
            
            <div class="card-footer">
                <div class="reward-points">
                    <div class="points-label">Reward Points</div>
                    <div class="points-value">15,750</div>
                    <div class="points-expiry">Expires: 12/31/2025</div>
                </div>
                <div class="progress-section">
                    <div class="progress-label">
                        <span>Progress to next reward</span>
                        <span>100%</span>
                    </div>
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: 100%"></div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="credit-card chase">
            <div class="card-header">
                <div class="card-brand">
                    <div class="brand-logo">C</div>
                    <div class="brand-info">
                        <h3>Chase</h3>
                        <p>Travel Elite</p>
                    </div>
                </div>
                <div class="card-status">active</div>
            </div>
            
            <div class="card-number">
                Card Number
                <div>****7654</div>
            </div>
            
            <div class="card-details">
                <div class="detail-group">
                    <h4>Credit Usage</h4>
                    <p>$3249.75 of $5000.00 used</p>
                    <div class="usage-bar">
                        <div class="usage-fill" style="width: 65%"></div>
                    </div>
                </div>
                <div class="detail-group">
                    <h4>Available</h4>
                    <p class="available-amount">$1750.25</p>
                </div>
            </div>
            
            <div class="card-footer">
                <div class="reward-points">
                    <div class="points-label">Reward Points</div>
                    <div class="points-value">8,320</div>
                    <div class="points-expiry">Expires: 6/30/2025</div>
                </div>
                <div class="progress-section">
                    <div class="progress-label">
                        <span>Progress to next reward</span>
                        <span>83%</span>
                    </div>
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: 83%"></div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="credit-card amex">
            <div class="card-header">
                <div class="card-brand">
                    <div class="brand-logo">A</div>
                    <div class="brand-info">
                        <h3>American Express</h3>
                        <p>Gold Membership</p>
                    </div>
                </div>
                <div class="card-status">active</div>
            </div>
            
            <div class="card-number">
                Card Number
                <div>****2222</div>
            </div>
            
            <div class="card-details">
                <div class="detail-group">
                    <h4>Credit Usage</h4>
                    <p>$6799.25 of $10000.00 used</p>
                    <div class="usage-bar">
                        <div class="usage-fill" style="width: 68%"></div>
                    </div>
                </div>
                <div class="detail-group">
                    <h4>Available</h4>
                    <p class="available-amount">$3200.75</p>
                </div>
            </div>
            
            <div class="card-footer">
                <div class="reward-points">
                    <div class="points-label">Reward Points</div>
                    <div class="points-value">12,450</div>
                    <div class="points-expiry">Expires: 9/15/2025</div>
                </div>
                <div class="progress-section">
                    <div class="progress-label">
                        <span>Progress to next reward</span>
                        <span>92%</span>
                    </div>
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: 92%"></div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function displayMockData() {
    // Update metrics with mock data
    document.getElementById('totalEarned').textContent = '6,745';
    document.getElementById('totalRedeemed').textContent = '1,800';
    document.getElementById('thisMonth').textContent = '0';
    document.getElementById('totalTransactions').textContent = '30';
    
    // Display mock cards
    displayMockCards();
    
    // Initialize chart with mock data
    initializeChart();
}

function updateMetrics(cardsData, pointsData) {
    let totalEarned = 0;
    let totalRedeemed = 0;
    let thisMonth = 0;
    let totalTransactions = 0;
    
    if (pointsData && pointsData.length > 0) {
        pointsData.forEach(point => {
            totalEarned += point.pointsEarned || 0;
            totalRedeemed += point.pointsUsed || 0;
            
            // Check if this month (simple approximation)
            const earningDate = new Date(point.earningDate);
            const now = new Date();
            if (earningDate.getMonth() === now.getMonth() && earningDate.getFullYear() === now.getFullYear()) {
                thisMonth += point.pointsEarned || 0;
            }
        });
        
        totalTransactions = pointsData.length;
    }
    
    // Update UI
    document.getElementById('totalEarned').textContent = formatNumber(totalEarned);
    document.getElementById('totalRedeemed').textContent = formatNumber(totalRedeemed);
    document.getElementById('thisMonth').textContent = formatNumber(thisMonth);
    document.getElementById('totalTransactions').textContent = formatNumber(totalTransactions);
}

function initializeChart() {
    const ctx = document.getElementById('monthlyChart');
    if (!ctx) return;
    
    // Destroy existing chart if it exists
    if (monthlyChart) {
        monthlyChart.destroy();
    }
    
    // Mock data for the chart (matching the image)
    const chartData = {
        labels: ['Sep', 'Oct', 'Nov', 'Dec', 'Jan', 'Feb', 'Mar'],
        datasets: [
            {
                label: 'Earned',
                data: [1150, 850, 1050, 1290, 1200, 1100, 950],
                backgroundColor: '#3b82f6',
                borderColor: '#3b82f6',
                borderWidth: 0,
                borderRadius: 6,
                borderSkipped: false,
            },
            {
                label: 'Redeemed',
                data: [450, 300, 200, 350, 400, 250, 150],
                backgroundColor: '#ef4444',
                borderColor: '#ef4444',
                borderWidth: 0,
                borderRadius: 6,
                borderSkipped: false,
            }
        ]
    };
    
    const chartConfig = {
        type: 'bar',
        data: chartData,
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        color: '#94a3b8',
                        usePointStyle: true,
                        pointStyle: 'rect',
                        font: {
                            size: 12
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        display: false
                    },
                    ticks: {
                        color: '#64748b',
                        font: {
                            size: 12
                        }
                    }
                },
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    },
                    ticks: {
                        color: '#64748b',
                        font: {
                            size: 12
                        }
                    }
                }
            },
            elements: {
                bar: {
                    borderRadius: 6
                }
            }
        }
    };
    
    monthlyChart = new Chart(ctx, chartConfig);
}

// Utility functions
function getCardClass(vendorName) {
    const vendor = vendorName.toLowerCase();
    if (vendor.includes('citi')) return 'citibank';
    if (vendor.includes('chase')) return 'chase';
    if (vendor.includes('american') || vendor.includes('amex')) return 'amex';
    return '';
}

function getCardLogo(vendorName) {
    const vendor = vendorName.toLowerCase();
    if (vendor.includes('citi')) return 'C';
    if (vendor.includes('chase')) return 'C';
    if (vendor.includes('american') || vendor.includes('amex')) return 'A';
    if (vendor.includes('hdfc')) return 'H';
    if (vendor.includes('sbi')) return 'S';
    return vendorName.charAt(0).toUpperCase();
}

function maskCardNumber(cardNumber) {
    if (!cardNumber) return '****';
    const lastFour = cardNumber.slice(-4);
    return `****${lastFour}`;
}

function formatNumber(num) {
    if (!num) return '0';
    return new Intl.NumberFormat('en-US').format(num);
}

function getExpiryDate() {
    const date = new Date();
    date.setFullYear(date.getFullYear() + 2);
    return date.toLocaleDateString('en-US', { month: '2-digit', day: '2-digit', year: 'numeric' });
}

function getProgressPercentage() {
    return Math.floor(Math.random() * 100) + 1; // Random for demo
}

function showLoading(show) {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (show) {
        loadingOverlay.classList.remove('hidden');
    } else {
        loadingOverlay.classList.add('hidden');
    }
}

function goToDashboard() {
    window.location.href = 'index.html';
}

// Navigation helper
function goBack() {
    window.history.back();
} 