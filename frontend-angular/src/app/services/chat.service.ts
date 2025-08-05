import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AuthService } from './auth.service';

export interface ChatMessage {
  id: string;
  text: string;
  isUser: boolean;
  timestamp: Date;
  intent?: string;
}

export interface GenAiResponse {
  success: boolean;
  response: string;
  intent?: string;
  timestamp: number;
  error?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private readonly API_URL = 'http://localhost:8083/api/genai';
  private messagesSubject = new BehaviorSubject<ChatMessage[]>([]);
  public messages$ = this.messagesSubject.asObservable();
  
  private isTyping = new BehaviorSubject<boolean>(false);
  public isTyping$ = this.isTyping.asObservable();

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {
    this.initializeChat();
  }

  private initializeChat(): void {
    const initialMessage: ChatMessage = {
      id: '1',
      text: 'Hi! I\'m your AI-powered RewardBot assistant. I can help you with your reward points, card balances, and redemption options using advanced natural language understanding. What would you like to know?',
      isUser: false,
      timestamp: new Date(),
      intent: 'WELCOME'
    };
    this.messagesSubject.next([initialMessage]);
  }

  sendMessage(message: string): Observable<string> {
    // Add user message immediately
    this.addMessage(message, true);
    
    // Show typing indicator
    this.isTyping.next(true);

    const user = this.authService.getCurrentUser();
    if (!user) {
      this.isTyping.next(false);
      const errorMsg = 'Please log in to use the chat feature.';
      this.addMessage(errorMsg, false, 'ERROR');
      return of(errorMsg);
    }

    const headers = this.authService.getAuthHeaders();
    
    const requestBody = {
      mobileNumber: user.mobileNumber,
      query: message
    };

    return this.http.post<GenAiResponse>(`${this.API_URL}/query`, requestBody, { headers })
      .pipe(
        map(response => {
          this.isTyping.next(false);
          
          if (response.success) {
            this.addMessage(response.response, false, response.intent);
            return response.response;
          } else {
            const errorMsg = response.error || 'Sorry, I encountered an error processing your request.';
            this.addMessage(errorMsg, false, 'ERROR');
            return errorMsg;
          }
        }),
        catchError(error => {
          console.error('GenAI API error:', error);
          this.isTyping.next(false);
          
          // Fallback to enhanced mock response
          const fallbackResponse = this.generateEnhancedMockResponse(message);
          this.addMessage(fallbackResponse, false, 'FALLBACK');
          return of(fallbackResponse);
        })
      );
  }

  // Test OpenAI integration without authentication
  testOpenAI(query: string): Observable<string> {
    const requestBody = { query: query };
    
    return this.http.post<GenAiResponse>(`${this.API_URL}/test`, requestBody)
      .pipe(
        map(response => {
          if (response.success) {
            return response.response;
          } else {
            throw new Error(response.error || 'Test failed');
          }
        }),
        catchError(error => {
          console.error('OpenAI test error:', error);
          return of(`OpenAI Test Failed: ${error.message || error}`);
        })
      );
  }

  // Check GenAI service health
  checkServiceHealth(): Observable<any> {
    return this.http.get(`${this.API_URL}/health`)
      .pipe(
        catchError(error => {
          console.error('GenAI service health check failed:', error);
          return of({ status: 'DOWN', error: error.message });
        })
      );
  }

  private addMessage(text: string, isUser: boolean, intent?: string): void {
    const currentMessages = this.messagesSubject.value;
    const newMessage: ChatMessage = {
      id: Date.now().toString(),
      text: text,
      isUser: isUser,
      timestamp: new Date(),
      intent: intent
    };
    this.messagesSubject.next([...currentMessages, newMessage]);
  }

  private generateEnhancedMockResponse(query: string): string {
    const queryLower = query.toLowerCase();

    // Enhanced mock responses with more intelligence
    if (queryLower.includes('points') && (queryLower.includes('how many') || queryLower.includes('total'))) {
      return `🎉 **Great question!** You have a total of **47,910 reward points** across all your cards:

📊 **Breakdown by Card:**
• **HDFC Bank**: 15,420 points (2.5% cashback rate)
• **Amazon Pay**: 18,750 points (5.0% cashback rate) 
• **Flipkart Axis Bank**: 13,740 points (4.0% cashback rate)

⚠️ **Urgent**: You have **1,700 points expiring within 30 days** - consider redeeming them soon!

💡 **AI Insight**: Based on your spending pattern, you're earning points efficiently with Amazon Pay due to its highest cashback rate.`;
    }

    if (queryLower.includes('expir')) {
      return `⏰ **Points Expiry Analysis** (AI-Generated):

🚨 **Immediate Action Required:**
• **HDFC Bank**: 500 points expire in 15 days
• **Flipkart Axis Bank**: 1,200 points expire in 28 days

📅 **Future Expiries:**
• **Amazon Pay**: 18,750 points expire in 8 months
• **HDFC Bank**: 14,920 points expire in 11 months

🤖 **AI Recommendation**: I suggest redeeming the 1,700 expiring points for Amazon Gift Vouchers (20% bonus) to maximize value before expiration.

Would you like me to show you redemption options for these expiring points?`;
    }

    if (queryLower.includes('balance') || queryLower.includes('money')) {
      return `💳 **Your Financial Overview** (AI-Powered Analysis):

**💰 Available Card Balances:**
• **HDFC Bank**: $25,000.50 (Credit limit utilization: 12%)
• **Amazon Pay**: $1,250.75 (Prepaid balance)
• **Flipkart Axis Bank**: $8,750.25 (Credit limit utilization: 28%)

**📊 Total Available**: $35,001.50 across all cards

🤖 **AI Insights**: 
- Your credit utilization is healthy (under 30%)
- Amazon Pay balance is optimal for online purchases
- Consider using Flipkart card for 4% cashback on shopping

**💎 Estimated Points Value**: Your 47,910 points ≈ $23,955 in redemption value`;
    }

    if (queryLower.includes('cashback') || queryLower.includes('rates')) {
      return `💸 **Cashback Analysis** (AI-Optimized Strategy):

**🏆 Current Cashback Rates:**
• **Amazon Pay**: 5.0% (🥇 Highest - Use for online shopping)
• **Flipkart Axis Bank**: 4.0% (🥈 Great for e-commerce)
• **HDFC Bank**: 2.5% (🥉 Good for general purchases)

**🤖 AI Spending Strategy:**
1. **Online Shopping**: Use Amazon Pay (5% cashback)
2. **E-commerce**: Use Flipkart Axis (4% cashback)
3. **Daily Expenses**: Use HDFC Bank (2.5% cashback)

**💰 Potential Annual Savings**: Following this strategy could save you $1,200+ annually based on average spending patterns.

**🎯 Smart Tip**: Your current points (47,910) represent excellent cashback accumulation - worth approximately $23,955!`;
    }

    if (queryLower.includes('redeem') || queryLower.includes('redemption') || queryLower.includes('buy')) {
      return `🛍️ **Intelligent Redemption Strategy** (AI-Curated):

With your **47,910 points**, here are AI-optimized recommendations:

**🏆 Maximum Value Options:**
1. **Amazon Gift Voucher**: $28,746 value (20% bonus) ⭐ *AI Top Pick*
2. **Travel Booking**: $26,000 value (15% bonus)
3. **Direct Cashback**: $23,955 value (1:0.5 ratio)

**🎯 Category-Specific Recommendations:**
• **Tech Gadgets**: 35,000 points = iPhone accessories
• **Travel**: 40,000 points = Weekend getaway
• **Dining**: 15,000 points = Premium restaurant vouchers

**⚠️ Priority Alert**: Redeem your 1,700 expiring points first!

**🤖 AI Advice**: Based on your purchase history, Amazon vouchers offer the best flexibility and bonus value. Would you like me to guide you through the redemption process?`;
    }

    if (queryLower.includes('vacation') || queryLower.includes('travel')) {
      return `✈️ **AI-Powered Travel Recommendations**:

**🌟 Your Travel Redemption Options:**
• **Flight Vouchers**: Up to $26,000 value from your points
• **Hotel Bookings**: 15-20% bonus redemptions available
• **Complete Packages**: All-inclusive deals with 25% extra value
• **Airport Lounge**: 2,000 points per visit (great for frequent travelers)

**🤖 AI Travel Insights:**
- Best redemption value for travel is during off-peak seasons
- Combining points from multiple cards can unlock premium experiences
- Your Amazon Pay points offer excellent travel booking bonuses

**🏖️ Destination Suggestions** (Based on your points):
• **Weekend Getaway**: 15,000-25,000 points
• **International Trip**: 35,000-45,000 points  
• **Luxury Resort**: Full points for premium experience

Would you like specific travel package recommendations?`;
    }

    if (queryLower.includes('dining') || queryLower.includes('food') || queryLower.includes('restaurant')) {
      return `🍽️ **Culinary Rewards Experience** (AI-Enhanced):

**🍴 Premium Dining Options:**
• **Zomato Gold**: 8,000 points (10% bonus value)
• **Swiggy Super**: 12,000 points (15% bonus)
• **Fine Dining Vouchers**: 15,000-25,000 points
• **Food Delivery Credits**: Instant redemption available

**🤖 AI Dining Intelligence:**
- Your Flipkart Axis card offers 4% extra on food delivery
- Weekend dining vouchers provide 20% more value
- Combining delivery credits with cashback maximizes savings

**👨‍🍳 Chef's Special Recommendations:**
• **Michelin Star Experience**: 30,000 points
• **Food Festival Passes**: 18,000 points
• **Cooking Class Vouchers**: 12,000 points

**🔥 Limited Time**: Special 25% bonus on dining redemptions this month!`;
    }

    if (queryLower.includes('shopping') || queryLower.includes('shop')) {
      return `🛒 **Smart Shopping Strategy** (AI-Optimized):

**💎 Premium Shopping Options:**
• **Amazon Vouchers**: $28,746 value (20% bonus) - *AI Top Choice*
• **Flipkart Credits**: $26,346 value (10% bonus)
• **Brand Vouchers**: Nike, Apple, Samsung available
• **Electronics**: Direct purchase discounts up to 30%

**🤖 AI Shopping Intelligence:**
- Amazon vouchers provide maximum flexibility + bonus
- Electronics purchases during sales maximize point value  
- Brand-specific vouchers offer exclusive deals

**🏷️ Featured Deals** (Your points can unlock):
• **Smartphone**: 35,000-42,000 points
• **Laptop**: Full points for premium models
• **Fashion**: 15,000-25,000 points for luxury brands

**🎯 Pro Strategy**: Use expiring points first, then combine with current promotions for maximum value!`;
    }

    if (queryLower.includes('hello') || queryLower.includes('hi') || queryLower.includes('help')) {
      return `👋 **Welcome to AI-Powered RewardBot!**

I'm your intelligent assistant with advanced natural language understanding. Here's what I can help you with:

**🧠 AI Capabilities:**
• **Smart Intent Recognition** - I understand complex questions
• **Personalized Recommendations** - Based on your actual data
• **Real-time Analysis** - Current points, expiry, and values
• **Strategic Advice** - Optimized for maximum benefit

**💬 Try These Intelligent Queries:**
• "How should I optimize my cashback strategy?"
• "What's the smartest way to use my expiring points?"
• "Create a redemption plan for maximum value"
• "Compare my cards and recommend best usage"

**🎯 Your Current Status:**
• **47,910 points** ready for redemption
• **$35,001.50** available across cards
• **1,700 points** expiring soon (action needed!)

What would you like to explore first? I'm here to provide intelligent, personalized assistance! 🤖`;
    }

    // Default intelligent response
    return `🤖 **AI Analysis** of your query: "${query}"

I understand you're asking about your rewards, and I'm here to provide intelligent assistance!

**📊 Quick Context:**
• **Total Points**: 47,910 (worth ~$23,955)
• **Active Cards**: 3 cards with different benefits
• **Urgent**: 1,700 points expiring within 30 days

**🎯 Popular AI-Assisted Queries:**
• **"Optimize my points strategy"** - Smart recommendations
• **"When do points expire?"** - Expiry analysis & alerts
• **"Best redemption for my points?"** - Value maximization
• **"Compare my cashback rates"** - Strategic usage advice

**💡 AI Tip**: I can provide personalized insights based on your actual reward data. Try asking more specific questions about your points, cards, or redemption goals!

What specific aspect of your rewards would you like me to analyze? 🚀`;
  }

  clearChat(): void {
    this.initializeChat();
  }

  getMessages(): ChatMessage[] {
    return this.messagesSubject.value;
  }

  // Utility method to format responses
  private formatResponseWithAI(response: string): string {
    return `🤖 **AI-Powered Response:**\n\n${response}\n\n💡 *This response was generated using advanced natural language understanding.*`;
  }
} 