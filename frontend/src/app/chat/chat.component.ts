import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

interface Message {
  id: number;
  contenu: string;
  dateEnvoi: string;
  sender: { id: number; nom: string };
  receiver: { id: number; nom: string };
  lu: boolean;
}

interface Conversation {
  id: number;
  nom: string;
  lastMessage?: string;
  offreTitre?: string;
  unreadCount?: number;
}

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="p-8 max-w-7xl mx-auto">
      <div class="page-header mb-8">
        <h1 class="page-title">Messages</h1>
        <p class="page-subtitle">{{isCandidat() ? 'Chat with recruiters' : isAdmin() ? 'Chat with recruiters' : 'Chat with candidates'}}</p>
      </div>

      <div class="flex gap-6" style="height: 70vh">
        <!-- Conversations List -->
        <div class="w-72 card p-4 overflow-hidden" style="display: flex; flex-direction: column">
          <h3 style="color: var(--text); font-weight: 600; margin-bottom: 1rem">Conversations</h3>
          <div style="overflow-y: auto; flex: 1">
            <div *ngIf="conversations.length === 0" class="text-center" style="color: var(--text-muted); padding: 2rem">
              <p>No conversations yet</p>
              <p style="font-size: 0.875rem">Start by applying to jobs or receiving applications</p>
            </div>
            <div *ngFor="let user of conversations" (click)="selectConversation(user)"
              style="padding: 0.75rem; border-radius: 0.75rem; margin-bottom: 0.5rem; cursor: pointer"
              [style.background]="selectedUser === user.id ? 'var(--surface)' : 'transparent'"
              [style.border]="selectedUser === user.id ? '1px solid var(--accent)' : '1px solid transparent'">
              <div style="display: flex; align-items: center; justify-content: space-between;">
                <p style="color: var(--text); font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap">{{user.nom}}</p>
                <span *ngIf="user.unreadCount && user.unreadCount > 0" style="background: var(--accent); color: white; font-size: 0.65rem; font-weight: 600; padding: 0.1rem 0.4rem; border-radius: 10px; min-width: 18px; text-align: center;">{{user.unreadCount}}</span>
              </div>
              <p *ngIf="user.offreTitre" style="color: var(--text-muted); font-size: 0.75rem; overflow: hidden; text-overflow: ellipsis; white-space: nowrap">
                re: {{user.offreTitre}}
              </p>
            </div>
          </div>
        </div>

        <!-- Chat Area -->
        <div class="flex-1 card p-4" style="display: flex; flex-direction: column">
          <div *ngIf="!selectedUser" class="flex-1 flex flex-col items-center justify-center" style="color: var(--text-muted)">
            <p class="text-lg mb-2">💬 No conversation selected</p>
            <p class="text-sm">Click on a name from left panel to start chatting</p>
            <p class="text-xs mt-4">Candidates: Apply to jobs to chat with recruiters</p>
            <p class="text-xs">Recruiters: Receive applications to start chatting</p>
          </div>
          
          <div *ngIf="selectedUser" class="flex-1 overflow-auto" style="margin-bottom: 1rem">
            <div *ngFor="let msg of messages" 
              [style.background]="msg.sender && msg.sender.id === currentUserId ? 'var(--accent)' : 'var(--surface)'"
              [style.margin-left]="msg.sender && msg.sender.id === currentUserId ? 'auto' : '0'"
              style="max-width: 70%; border-radius: 1rem; padding: 0.75rem 1rem; margin-bottom: 0.75rem; word-wrap: break-word">
              <p style="color: var(--text)">{{msg.contenu}}</p>
            </div>
            <div *ngIf="messages.length === 0" style="color: var(--text-muted); text-align: center; padding: 2rem">
              No messages yet. Say hello!
            </div>
          </div>

          <div *ngIf="selectedUser" style="display: flex; gap: 0.5rem">
            <input [(ngModel)]="newMessage" (keyup.enter)="sendMessage()"
              placeholder="Type a message..."
              class="input" style="flex: 1">
            <button (click)="sendMessage()" class="btn-primary">
              Send
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ChatComponent implements OnInit {
  messages: Message[] = [];
  conversations: Conversation[] = [];
  selectedUser: number | null = null;
  newMessage = '';
  currentUserId = 0;
  userRole = '';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.userRole = localStorage.getItem('userRole') || '';
    this.currentUserId = parseInt(localStorage.getItem('userId') || '0');
    
    // Check if opening specific conversation from Applications
    const chatWithId = localStorage.getItem('chatWithId');
    const chatWithNom = localStorage.getItem('chatWithNom');
    
    if (chatWithId) {
      this.selectedUser = parseInt(chatWithId);
      this.loadMessages(this.selectedUser);
    }
    
    this.loadConversations();
  }

  isCandidat(): boolean {
    return this.userRole === 'CANDIDAT';
  }

  isRecruteur(): boolean {
    return this.userRole === 'RECRUTEUR';
  }

  isAdmin(): boolean {
    return this.userRole === 'ADMINISTRATEUR';
  }

  fetchUnreadCounts(headers: HttpHeaders, userId: number) {
    this.http.get<any>(`http://localhost:8000/api/messages/unread-per-user?userId=${userId}`, { headers }).subscribe({
      next: (counts: any) => {
        // Update unread counts for each conversation
        this.conversations = this.conversations.map((conv: any) => ({
          ...conv,
          unreadCount: counts[conv.id] || 0
        }));
      },
      error: () => {}
    });
  }

  loadConversations() {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    const userId = this.currentUserId;
    
    if (this.isAdmin()) {
      // Admin sees ALL recruiters
      this.http.get<any>('http://localhost:8000/api/stats/users', { headers }).subscribe({
        next: (users: any) => {
          this.conversations = (users.recruteurs || []).map((r: any) => ({
            id: r.id,
            nom: r.nom,
            unreadCount: 0
          }));
          this.fetchUnreadCounts(headers, userId);
        },
        error: () => this.conversations = []
      });
    } 
    else if (this.isRecruteur()) {
      // Recruteur sees hired/interview candidates + ALL admins
      this.http.get<any>('http://localhost:8000/api/stats/users', { headers }).subscribe({
        next: (users: any) => {
          // Get all admins first
          const admins = (users.administrateurs || []).map((a: any) => ({
            id: a.id,
            nom: a.nom
          }));
          
          // Then get candidates via separate call
          this.http.get<any[]>('http://localhost:8000/api/candidatures', { headers }).subscribe({
            next: (candidatures) => {
              const grouped = new Map<number, Conversation>();
              candidatures.forEach((c: any) => {
                if (c.candidat && (c.statut === 'ENTRETIEN' || c.statut === 'RETENU')) {
                  grouped.set(c.candidat.id, {
                    id: c.candidat.id,
                    nom: c.candidat.nom,
                    offreTitre: c.offre?.titre
                  });
                }
              });
              // Combine: admins + candidates
              this.conversations = [...admins, ...Array.from(grouped.values())];
              this.fetchUnreadCounts(headers, userId);
            },
            error: () => {
              this.conversations = admins;
              this.fetchUnreadCounts(headers, userId);
            }
          });
        },
        error: () => this.conversations = []
      });
    } else {
      // Candidat sees ONLY recruiters who've accepted them (no admin)
      this.http.get<any[]>('http://localhost:8000/api/candidatures', { headers }).subscribe({
        next: (candidatures) => {
          const grouped = new Map<number, Conversation>();
          candidatures.forEach((c: any) => {
            if (c.offre && c.offre.recruteur && (c.statut === 'RETENU' || c.statut === 'ENTRETIEN')) {
              grouped.set(c.offre.recruteur.id, {
                id: c.offre.recruteur.id,
                nom: c.offre.recruteur.nom,
                offreTitre: c.offre.titre
              });
            }
          });
          this.conversations = Array.from(grouped.values());
          this.fetchUnreadCounts(headers, userId);
        },
        error: () => this.conversations = []
      });
    }
  }

  selectConversation(user: Conversation) {
    console.log('[DEBUG] selectConversation called:', user.id, user.nom);
    this.selectedUser = user.id;
    this.messages = []; // Clear previous messages
    this.loadMessages(user.id);
  }

  loadMessages(userId: number) {
    console.log('[DEBUG] loadMessages called with userId:', userId, 'currentUserId:', this.currentUserId);
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    this.messages = []; // Clear old messages first
    this.http.get<Message[]>(`http://localhost:8000/api/messages/conversation/${this.currentUserId}/${userId}`, { headers }).subscribe({
      next: (data) => {
        console.log('[DEBUG] Messages loaded:', data.length, data);
        this.messages = data;
        // Mark messages FROM this user as read
        this.http.patch(`http://localhost:8000/api/messages/read-from-user?userId=${this.currentUserId}&fromUserId=${userId}`, {}, { headers }).subscribe({
          next: () => {
            // Update the conversation's unread count to 0
            this.conversations = this.conversations.map((conv: any) => {
              if (conv.id === userId) {
                return { ...conv, unreadCount: 0 };
              }
              return conv;
            });
            window.dispatchEvent(new Event('messagesRead'));
          }
        });
      },
      error: (err) => console.error('[DEBUG] Error loading messages:', err)
    });
  }

  sendMessage() {
    if (!this.newMessage.trim() || !this.selectedUser) return;
    const headers = new HttpHeaders()
      .set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`)
      .set('Content-Type', 'application/json');
    
    this.http.post('http://localhost:8000/api/messages', {
      senderId: this.currentUserId,
      receiverId: this.selectedUser,
      contenu: this.newMessage
    }, { headers }).subscribe({
      next: () => {
        this.loadMessages(this.selectedUser!);
        this.newMessage = '';
      }
    });
  }
}