import { Component, OnInit, OnDestroy, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ChatService, ChatMessage } from '../../services/chat.service';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit, OnDestroy {
  @ViewChild('messagesContainer') messagesContainer!: ElementRef;
  
  chatForm: FormGroup;
  messages: ChatMessage[] = [];
  isLoading = false;
  private subscription = new Subscription();

  quickQuestions = [
    'How many reward points do I have?',
    'When do my points expire?',
    'Show me cashback options',
    'What can I get for 5000 points?'
  ];

  constructor(
    private fb: FormBuilder,
    private chatService: ChatService
  ) {
    this.chatForm = this.fb.group({
      message: ['', [Validators.required, Validators.minLength(1)]]
    });
  }

  ngOnInit(): void {
    this.subscribeToMessages();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  private subscribeToMessages(): void {
    this.subscription.add(
      this.chatService.messages$.subscribe(messages => {
        this.messages = messages;
        setTimeout(() => this.scrollToBottom(), 100);
      })
    );
  }

  onSendMessage(): void {
    if (this.chatForm.valid && !this.isLoading) {
      const message = this.chatForm.value.message.trim();
      if (message) {
        this.isLoading = true;
        this.chatForm.reset();
        
        this.chatService.sendMessage(message).subscribe({
          next: () => {
            this.isLoading = false;
          },
          error: () => {
            this.isLoading = false;
          }
        });
      }
    }
  }

  onQuickQuestion(question: string): void {
    if (!this.isLoading) {
      this.isLoading = true;
      this.chatService.sendMessage(question).subscribe({
        next: () => {
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
        }
      });
    }
  }

  private scrollToBottom(): void {
    try {
      this.messagesContainer.nativeElement.scrollTop = 
        this.messagesContainer.nativeElement.scrollHeight;
    } catch (err) {
      console.error('Error scrolling to bottom:', err);
    }
  }

  formatTime(date: Date): string {
    return new Intl.DateTimeFormat('en-US', {
      hour: '2-digit',
      minute: '2-digit'
    }).format(date);
  }
} 