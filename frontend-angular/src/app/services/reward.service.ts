import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';

export interface Card {
  id: number;
  cardNumber: string;
  maskedCardNumber: string;
  cardType: string;
  vendorName: string;
  vendorCode: string;
  cardBalance: number;
  currency: string;
  isActive: boolean;
  cashbackRate: number;
  totalRewardPoints: number;
  expiringSoonPoints?: number;
}

export interface RewardSummary {
  totalPointsAvailable: number;
  pointsExpiringSoon: number;
  totalPointsValue: number;
  numberOfActiveCards: number;
  estimatedCashbackValue: number;
}

@Injectable({
  providedIn: 'root'
})
export class RewardService {
  private readonly API_URL = 'http://localhost:8082/api/rewards';
  private rewardDataSubject = new BehaviorSubject<any>(null);
  public rewardData$ = this.rewardDataSubject.asObservable();

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  loadUserRewardData(): Observable<any> {
    // Return mock data for demo
    const mockData = this.getMockRewardData();
    this.rewardDataSubject.next(mockData);
    return of(mockData);
  }

  private getMockRewardData(): any {
    return {
      totalRewardPoints: 47910,
      totalBalance: 7401.50,
      activeCards: 3,
      cards: [
        {
          id: 1,
          vendorName: 'HDFC Bank',
          cardType: 'CREDIT',
          cardBalance: 25000.50,
          maskedCardNumber: '**** **** **** 9012',
          cashbackRate: 2.5,
          totalRewardPoints: 15420,
          expiringSoonPoints: 500
        },
        {
          id: 2,
          vendorName: 'Amazon Pay',
          cardType: 'LOYALTY',
          cardBalance: 1250.75,
          maskedCardNumber: '**** **** **** 1098',
          cashbackRate: 5.0,
          totalRewardPoints: 18750,
          expiringSoonPoints: 0
        },
        {
          id: 3,
          vendorName: 'Flipkart Axis Bank',
          cardType: 'CREDIT',
          cardBalance: 8750.25,
          maskedCardNumber: '**** **** **** 3456',
          cashbackRate: 4.0,
          totalRewardPoints: 13740,
          expiringSoonPoints: 1200
        }
      ]
    };
  }

  getAnalyticsData(): Observable<any> {
    return of({
      totalEarned: 6745,
      totalRedeemed: 1800,
      thisMonth: 0,
      totalTransactions: 30,
      monthlyActivity: [
        { month: 'Jul', earned: 1150, redeemed: 450 },
        { month: 'Aug', earned: 850, redeemed: 300 },
        { month: 'Sep', earned: 1050, redeemed: 200 },
        { month: 'Oct', earned: 1300, redeemed: 350 },
        { month: 'Nov', earned: 1200, redeemed: 150 },
        { month: 'Dec', earned: 1100, redeemed: 320 }
      ]
    });
  }
} 