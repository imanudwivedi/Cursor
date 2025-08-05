import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, User } from '../../services/auth.service';
import { RewardService } from '../../services/reward.service';
import { ChatService } from '../../services/chat.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;
  rewardData: any = null;
  isLoading = true;
  showChatbot = false;

  constructor(
    private authService: AuthService,
    private rewardService: RewardService,
    private chatService: ChatService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.loadRewardData();
  }

  private loadUserData(): void {
    this.currentUser = this.authService.getCurrentUser();
  }

  private loadRewardData(): void {
    this.rewardService.loadUserRewardData().subscribe({
      next: (data) => {
        this.rewardData = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading reward data:', error);
        this.isLoading = false;
      }
    });
  }

  onLogout(): void {
    this.authService.logout();
  }

  onNavigateToAnalytics(): void {
    this.router.navigate(['/analytics']);
  }

  onToggleChatbot(): void {
    this.showChatbot = !this.showChatbot;
  }

  onRedeemCategory(category: string): void {
    // Mock redemption by category
    const message = `I want to redeem points for ${category.toLowerCase()}`;
    this.chatService.sendMessage(message).subscribe();
    this.showChatbot = true;
  }

  getUserDisplayName(): string {
    if (this.currentUser?.firstName && this.currentUser?.lastName) {
      return `${this.currentUser.firstName} ${this.currentUser.lastName}`;
    } else if (this.currentUser?.firstName) {
      return this.currentUser.firstName;
    }
    return 'User';
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }

  formatPoints(points: number): string {
    return new Intl.NumberFormat('en-US').format(points);
  }
} 