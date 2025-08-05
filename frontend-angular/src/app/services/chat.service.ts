import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';

export interface ChatMessage {
  id: string;
  text: string;
  isUser: boolean;
  timestamp: Date;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private readonly API_URL = 'http://localhost:8083/api/genai';
  private messagesSubject = new BehaviorSubject<ChatMessage[]>([]);
  public messages$ = this.messagesSubject.asObservable();

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {
    this.initializeChat();
  }

  private initializeChat(): void {
    const initialMessage: ChatMessage = {
      id: '1',
      text: 'Hi! I\'m your RewardBot assistant. I can help you with your reward points, card balances, and redemption options. What would you like to know?',
      isUser: false,
      timestamp: new Date()
    };
    this.messagesSubject.next([initialMessage]);
  }

  sendMessage(message: string): Observable<string> {
    // Add user message
    this.addMessage(message, true);

    // Process with AI (mock implementation)
    return this.processWithAI(message).pipe(
      catchError(error => {
        console.error('Chat error:', error);
        return of('I apologize, but I\'m experiencing some technical difficulties. Please try again later.');
      })
    );
  }

  private processWithAI(query: string): Observable<string> {
    // Mock AI responses based on query content
    const response = this.generateMockResponse(query);
    
    // Add bot response
    setTimeout(() => {
      this.addMessage(response, false);
    }, 1000);

    return of(response);
  }

  private addMessage(text: string, isUser: boolean): void {
    const currentMessages = this.messagesSubject.value;
    const newMessage: ChatMessage = {
      id: Date.now().toString(),
      text: text,
      isUser: isUser,
      timestamp: new Date()
    };
    this.messagesSubject.next([...currentMessages, newMessage]);
  }

  private generateMockResponse(query: string): string {
    const queryLower = query.toLowerCase();

    if (queryLower.includes('points') && (queryLower.includes('how many') || queryLower.includes('total'))) {
      return `You have a total of <strong>47,910 reward points</strong> across all your cards:
      <br><br>• HDFC Bank: 15,420 points
      <br>• Amazon Pay: 18,750 points  
      <br>• Flipkart Axis Bank: 13,740 points
      <br><br>You have <strong>1,700 points expiring within 30 days</strong>. Consider redeeming them soon!`;
    }

    if (queryLower.includes('expir')) {
      return `Here are your points expiry details:
      <br><br>⚠️ <strong>Expiring Soon (within 30 days):</strong>
      <br>• HDFC Bank: 500 points (expires in 15 days)
      <br>• Flipkart Axis Bank: 1,200 points (expires in 28 days)
      <br><br>📅 <strong>Future Expiries:</strong>
      <br>• Amazon Pay: 18,750 points (expires in 8 months)
      <br>• HDFC Bank: 14,920 points (expires in 11 months)
      <br><br>I recommend redeeming the expiring points first!`;
    }

    if (queryLower.includes('balance')) {
      return `Your current card balances:
      <br><br>💳 <strong>Available Balances:</strong>
      <br>• HDFC Bank: ₹25,000.50
      <br>• Amazon Pay: ₹1,250.75
      <br>• Flipkart Axis Bank: ₹8,750.25
      <br><br><strong>Total Available: ₹7,401.50</strong>
      <br><br>All cards are active and ready to use!`;
    }

    if (queryLower.includes('cashback')) {
      return `Here are your cashback options by card:
      <br><br>💳 <strong>Current Cashback Rates:</strong>
      <br>• Amazon Pay: 5.0% (highest rate!)
      <br>• Flipkart Axis Bank: 4.0%
      <br>• HDFC Bank: 2.5%
      <br><br>💰 <strong>Estimated cashback value:</strong>
      <br>Your 47,910 points are worth approximately ₹23,955 in cashback value.
      <br><br>Would you like me to show you the best redemption options?`;
    }

    if (queryLower.includes('redeem') || queryLower.includes('redemption')) {
      return `Based on your 47,910 total points, here are my top recommendations:
      <br><br>🏆 <strong>Best Value Options:</strong>
      <br>1. Amazon Gift Voucher: ₹28,746 (20% bonus)
      <br>2. Flight Booking Credit: ₹26,000 value
      <br>3. Direct Bank Transfer: ₹23,955
      <br>4. Shopping Vouchers: ₹25,500-29,000
      <br><br>⚠️ <strong>Priority:</strong> Redeem your 1,700 expiring points first!
      <br><br>Would you like me to help you start the redemption process?`;
    }

    if (queryLower.includes('vacation') || queryLower.includes('travel')) {
      return `Great choice for vacation redemptions! 🏖️
      <br><br>🎫 <strong>Travel Options with your points:</strong>
      <br>• Flight vouchers: Up to ₹26,000 value
      <br>• Hotel bookings: 15-20% bonus on redemptions
      <br>• Travel packages: Special deals available
      <br>• Airport lounge access: 2,000 points per visit
      <br><br>💡 <strong>Pro tip:</strong> Amazon Pay offers the best travel booking bonuses!`;
    }

    if (queryLower.includes('dining') || queryLower.includes('food')) {
      return `Delicious dining options with your rewards! 🍽️
      <br><br>🍴 <strong>Dining Redemptions:</strong>
      <br>• Zomato vouchers: 10% bonus (₹26,346 value)
      <br>• Swiggy credits: 15% bonus (₹27,596 value)
      <br>• Restaurant vouchers: Premium dining experiences
      <br>• Food delivery credits: Instant redemption
      <br><br>🔥 <strong>Special offer:</strong> Use Flipkart Axis for extra dining rewards!`;
    }

    if (queryLower.includes('shopping')) {
      return `Shop smart with your reward points! 🛍️
      <br><br>🛒 <strong>Shopping Options:</strong>
      <br>• Amazon vouchers: ₹28,746 value (20% bonus)
      <br>• Flipkart vouchers: ₹26,346 value (10% bonus)
      <br>• Brand vouchers: Nike, Adidas, Apple, Samsung
      <br>• Electronics: Direct purchase discounts
      <br><br>🎯 <strong>Best deal:</strong> Amazon vouchers give you maximum value!`;
    }

    if (queryLower.includes('hello') || queryLower.includes('hi')) {
      return `Hello! 👋 I'm your RewardBot assistant. I can help you with:
      <br><br>• Check your reward points balance
      <br>• Find points expiry dates
      <br>• Suggest best redemption options
      <br>• Show cashback opportunities
      <br>• Compare card benefits
      <br><br>What would you like to know about your rewards today?`;
    }

    // Default response
    return `I understand you're asking about "${query}". Here's what I can help you with:
    <br><br>• <strong>Points Balance:</strong> "How many points do I have?"
    <br>• <strong>Card Balances:</strong> "What are my card balances?"
    <br>• <strong>Expiry Info:</strong> "When do my points expire?"
    <br>• <strong>Cashback:</strong> "Show me cashback options"
    <br>• <strong>Redemption:</strong> "Best redemption options"
    <br><br>Try one of these questions for detailed information!`;
  }

  clearChat(): void {
    this.initializeChat();
  }

  getMessages(): ChatMessage[] {
    return this.messagesSubject.value;
  }
} 