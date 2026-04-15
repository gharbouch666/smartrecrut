import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ThemeService } from '../../core/theme.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit, OnDestroy {
  userRole: string = '';
  unreadCount: number = 0;
  private pollInterval: any;

  constructor(private router: Router, public theme: ThemeService, private http: HttpClient) {
    this.userRole = localStorage.getItem('userRole') || '';
  }

  ngOnInit() {
    this.loadUnreadCount();
    // Poll every 30 seconds for new messages
    this.pollInterval = setInterval(() => this.loadUnreadCount(), 30000);
    // Listen for messages read event from chat
    window.addEventListener('messagesRead', () => this.loadUnreadCount());
  }

  ngOnDestroy() {
    if (this.pollInterval) {
      clearInterval(this.pollInterval);
    }
  }

  loadUnreadCount() {
    const userId = localStorage.getItem('userId');
    if (!userId) return;
    
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    this.http.get<any>(`http://localhost:8000/api/messages/unread-count?userId=${userId}`, { headers }).subscribe({
      next: (res) => {
        this.unreadCount = res.count || 0;
      },
      error: () => this.unreadCount = 0
    });
  }

  toggleTheme() {
    this.theme.toggle();
  }

  isAdmin(): boolean {
    return this.userRole === 'ADMIN' || this.userRole === 'ADMINISTRATEUR';
  }

  isRecruteur(): boolean {
    return this.userRole === 'RECRUTEUR';
  }

  isCandidat(): boolean {
    return this.userRole === 'CANDIDAT';
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}