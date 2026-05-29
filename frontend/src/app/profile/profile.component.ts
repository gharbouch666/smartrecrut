import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

interface Tag {
  id: number;
  libelle: string;
  categorie: string;
}

interface Candidat {
  id: number;
  nom: string;
  email: string;
  dateNaissance: string;
  telephone: string;
  cvUrl: string;
  lettreMotivationUrl: string;
  niveauScolaire: string;
  experience: string;
  permisDeConduire: string;
  ville: string;
  linkedin: string;
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  styleUrls: ['./profile.component.scss'],
  template: `
    <div style="max-width: 1200px; margin: 0 auto;">
      <!-- Header -->
      <div class="page-header">
        <div>
          <h1 class="page-title">My Profile</h1>
          <p class="page-subtitle">Manage your information and documents</p>
        </div>
        <button (click)="configureMode = !configureMode" class="btn-primary">
          {{configureMode ? 'View Profile' : 'Configure Profile'}}
        </button>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" class="card" style="text-align: center; padding: 3rem;">
        <p class="mono" style="color: var(--text-muted);">Loading...</p>
      </div>

      <!-- Basic Profile View -->
      <div *ngIf="!configureMode && !loading" class="grid" style="grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap: 1.5rem;">
        <!-- Profile Card -->
        <div class="card">
          <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1.5rem;">
            <div class="avatar" style="width: 64px; height: 64px; font-size: 1.5rem;">
              {{candidat?.nom?.charAt(0)?.toUpperCase() || 'U'}}
            </div>
            <div>
              <h2 class="card-heading">{{candidat?.nom || 'Your Name'}}</h2>
              <p class="mono" style="font-size: 0.875rem; color: var(--text-muted);">{{candidat?.email}}</p>
            </div>
          </div>
          
          <div style="display: flex; flex-direction: column; gap: 0.75rem;">
            <div style="padding: 0.75rem; background: var(--surface); border-radius: 8px;">
              <p class="mono" style="font-size: 0.6875rem; color: var(--text-muted); text-transform: uppercase;">Phone</p>
              <p style="font-weight: 500; color: var(--text);">{{candidat?.telephone || 'Not set'}}</p>
            </div>
            <div style="padding: 0.75rem; background: var(--surface); border-radius: 8px;">
              <p class="mono" style="font-size: 0.6875rem; color: var(--text-muted); text-transform: uppercase;">City</p>
              <p style="font-weight: 500; color: var(--text);">{{candidat?.ville || 'Not set'}}</p>
            </div>
            <div style="padding: 0.75rem; background: var(--surface); border-radius: 8px;">
              <p class="mono" style="font-size: 0.6875rem; color: var(--text-muted); text-transform: uppercase;">Education</p>
              <p style="font-weight: 500; color: var(--text);">{{candidat?.niveauScolaire || 'Not set'}}</p>
            </div>
            <div style="padding: 0.75rem; background: var(--surface); border-radius: 8px;">
              <p class="mono" style="font-size: 0.6875rem; color: var(--text-muted); text-transform: uppercase;">Experience</p>
              <p style="font-weight: 500; color: var(--text);">{{candidat?.experience || 'Not set'}}</p>
            </div>
          </div>
        </div>

        <!-- Documents Card -->
        <div class="card">
          <h2 class="card-heading">Documents</h2>
          
          <div style="display: flex; flex-direction: column; gap: 1rem;">
            <div *ngIf="candidat?.cvUrl" style="padding: 1rem; background: rgba(22,163,74,0.1); border: 1px solid var(--lime); border-radius: 8px; display: flex; justify-content: space-between; align-items: center;">
              <div>
                <p style="font-weight: 600; color: var(--lime);">CV uploaded</p>
                <p class="mono" style="font-size: 0.75rem; color: var(--text-muted);">{{candidat?.cvUrl}}</p>
              </div>
               <a [href]="'http://localhost:8000/api/files/cv/' + (candidat?.cvUrl || '')" target="_blank" class="btn-primary" style="background: var(--lime);">View</a>
            </div>
            
            <div *ngIf="!candidat?.cvUrl" style="padding: 1rem; background: rgba(217,119,6,0.1); border: 1px solid var(--amber); border-radius: 8px;">
              <p style="font-weight: 600; color: var(--amber);">No CV yet</p>
              <p class="mono" style="font-size: 0.875rem; color: var(--text-muted);">Click Configure to upload</p>
            </div>

              <div *ngIf="candidat?.lettreMotivationUrl" style="padding: 1rem; background: rgba(22,163,74,0.1); border: 1px solid var(--lime); border-radius: 8px; display: flex; justify-content: space-between; align-items: center;">
              <div>
                <p style="font-weight: 600; color: var(--lime);">Cover Letter uploaded</p>
              </div>
               <a [href]="'http://localhost:8000/api/files/lettre/' + (candidat?.lettreMotivationUrl || '')" target="_blank" class="btn-primary" style="background: var(--lime);">View</a>
            </div>
            
            <div *ngIf="!candidat?.lettreMotivationUrl" style="padding: 1rem; background: rgba(217,119,6,0.1); border: 1px solid var(--amber); border-radius: 8px;">
              <p style="font-weight: 600; color: var(--amber);">No Cover Letter</p>
              <p class="mono" style="font-size: 0.875rem; color: var(--text-muted);">Click Configure to upload</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Configure Mode -->
      <div *ngIf="configureMode && !loading">
        <div style="display: flex; justify-content: flex-end; margin-bottom: 1.5rem; gap: 1rem;">
          <button (click)="saveProfile()" [disabled]="saving" class="btn-primary">
            {{saving ? 'Saving...' : 'Save All Changes'}}
          </button>
          <span *ngIf="saved" class="pill pill-green">Saved!</span>
        </div>

        <!-- Tabs -->
        <div style="display: flex; gap: 0.25rem; margin-bottom: 1.5rem; background: var(--card); border: 1px solid var(--border); border-radius: 8px; padding: 0.25rem;">
          <button (click)="activeTab = 'skills'" 
            style="flex: 1; padding: 0.75rem 1rem; border-radius: 6px; font-weight: 500; border: none; cursor: pointer;"
            [style.background]="activeTab === 'skills' ? 'var(--accent)' : 'transparent'"
            [style.color]="activeTab === 'skills' ? 'white' : 'var(--text-muted)'">
            Skills
          </button>
          <button (click)="activeTab = 'docs'" 
            style="flex: 1; padding: 0.75rem 1rem; border-radius: 6px; font-weight: 500; border: none; cursor: pointer;"
            [style.background]="activeTab === 'docs' ? 'var(--accent)' : 'transparent'"
            [style.color]="activeTab === 'docs' ? 'white' : 'var(--text-muted)'">
            CV & Documents
          </button>
          <button (click)="activeTab = 'info'" 
            style="flex: 1; padding: 0.75rem 1rem; border-radius: 6px; font-weight: 500; border: none; cursor: pointer;"
            [style.background]="activeTab === 'info' ? 'var(--accent)' : 'transparent'"
            [style.color]="activeTab === 'info' ? 'white' : 'var(--text-muted)'">
            Personal Info
          </button>
        </div>

      <!-- Skills Tab -->
      <div *ngIf="activeTab === 'skills'" style="display: flex; flex-direction: column; gap: 1.5rem;">
        <div *ngFor="let cat of categories" class="card">
          <h2 class="card-heading" style="text-transform: capitalize;">{{cat}}</h2>
          <div style="display: flex; flex-wrap: wrap; gap: 0.5rem;">
            <button *ngFor="let tag of tagsByCategory[cat]" type="button"
              (click)="toggleTag(tag.id)"
              style="padding: 0.5rem 0.75rem; border-radius: 6px; font-size: 0.8125rem; border: 1px solid var(--border); background: transparent; color: var(--text); cursor: pointer;"
              [style.background]="isSelected(tag.id) ? 'var(--accent)' : 'transparent'"
              [style.color]="isSelected(tag.id) ? 'white' : 'var(--text)'"
              [style.border-color]="isSelected(tag.id) ? 'var(--accent)' : 'var(--border)'">
              {{tag.libelle}}
              <span *ngIf="isSelected(tag.id)" style="margin-left: 0.25rem; opacity: 0.75;">({{getNiveau(tag.id)}})</span>
            </button>
          </div>
        </div>

        <!-- Selected Skills Panel -->
        <div *ngIf="hasSelectedSkills()" class="card">
          <h2 class="card-heading">Set Expertise Level</h2>
          <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); gap: 1rem;">
            <div *ngFor="let tagId of getSelectedTagIds()" style="display: flex; flex-direction: column; gap: 0.25rem;">
              <label class="mono" style="font-size: 0.6875rem; color: var(--text-muted); text-transform: uppercase;">{{getTagName(tagId)}}</label>
              <select (change)="setNiveau(tagId, $event)" [value]="getNiveau(tagId)" class="form-select">
                <option value="DEBUTANT">Beginner</option>
                <option value="INTERMEDIAIRE">Intermediate</option>
                <option value="EXPERT">Expert</option>
              </select>
            </div>
          </div>
        </div>

        <div *ngIf="categories.length === 0" class="card" style="text-align: center; padding: 3rem;">
          <p class="mono" style="color: var(--text-muted);">No skills available. Contact admin to add skills.</p>
        </div>
      </div>

<!-- CV & Documents Tab -->
      <div *ngIf="activeTab === 'docs'" style="display: flex; flex-direction: column; gap: 1.5rem;">
        <!-- CV Upload -->
        <div class="card">
          <h2 class="card-heading">CV (Curriculum Vitae)</h2>
          <div *ngIf="candidat && candidat.cvUrl" style="margin-bottom: 1rem; padding: 0.75rem; background: var(--surface); border-radius: 8px; display: flex; justify-content: space-between; align-items: center; gap: 0.75rem;">
            <div style="display: flex; align-items: center; gap: 0.5rem;">
              <svg style="width: 20px; height: 20px; color: var(--lime);" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              <span style="color: var(--text);">CV uploaded</span>
            </div>
            <div style="display: flex; gap: 0.5rem;">
              <a [href]="'http://localhost:8000/api/files/cv/' + candidat.cvUrl" target="_blank" class="btn-primary" style="background: var(--lime);">View CV</a>
              <button (click)="deleteCv()" [disabled]="deletingCv" class="btn-primary" style="background: #ef4444;">{{ deletingCv ? 'Deleting...' : 'Delete' }}</button>
            </div>
          </div>
          <input type="file" (change)="onCvSelected($event)" accept=".pdf"
            style="width: 100%; padding: 0.75rem; background: var(--surface); border: 1px solid var(--border); border-radius: 8px; color: var(--text);"/>
          <p class="mono" style="font-size: 0.6875rem; color: var(--text-muted); margin-top: 0.5rem;">Only PDF files accepted</p>
          <button (click)="uploadCv()" [disabled]="uploadingCv || !cvFile" class="btn-primary" style="margin-top: 0.75rem;">
            {{uploadingCv ? 'Uploading...' : 'Upload CV'}}
          </button>
        </div>

        <!-- Cover Letter Upload -->
        <div class="card">
          <h2 class="card-heading">Cover Letter (Lettre de motivation)</h2>
          <div *ngIf="candidat && candidat.lettreMotivationUrl" style="margin-bottom: 1rem; padding: 0.75rem; background: var(--surface); border-radius: 8px; display: flex; justify-content: space-between; align-items: center; gap: 0.75rem;">
            <div style="display: flex; align-items: center; gap: 0.5rem;">
              <svg style="width: 20px; height: 20px; color: var(--lime);" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              <span style="color: var(--text);">Cover letter uploaded</span>
            </div>
            <div style="display: flex; gap: 0.5rem;">
              <a [href]="'http://localhost:8000/api/files/lettre/' + candidat.lettreMotivationUrl" target="_blank" class="btn-primary" style="background: var(--lime);">View Letter</a>
              <button (click)="deleteLettre()" [disabled]="deletingLettre" class="btn-primary" style="background: #ef4444;">{{ deletingLettre ? 'Deleting...' : 'Delete' }}</button>
            </div>
          </div>
          <input type="file" (change)="onLettreSelected($event)" accept=".pdf"
            style="width: 100%; padding: 0.75rem; background: var(--surface); border: 1px solid var(--border); border-radius: 8px; color: var(--text);"/>
          <p class="mono" style="font-size: 0.6875rem; color: var(--text-muted); margin-top: 0.5rem;">Only PDF files accepted</p>
          <button (click)="uploadLettre()" [disabled]="uploadingLettre || !lettreFile" class="btn-primary" style="margin-top: 0.75rem;">
            {{uploadingLettre ? 'Uploading...' : 'Upload Cover Letter'}}
          </button>
        </div>
      </div>

      <!-- Personal Info Tab -->
      <div *ngIf="activeTab === 'info'">
        <div *ngIf="candidat" class="card">
          <h2 class="card-heading">Personal Information</h2>
          <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1rem;">
            <div>
              <label class="form-label">Date of Birth</label>
              <input type="date" [(ngModel)]="candidat!.dateNaissance" class="field"/>
            </div>
            <div>
              <label class="form-label">Phone</label>
              <input type="tel" [(ngModel)]="candidat!.telephone" class="field"/>
            </div>
            <div>
              <label class="form-label">Education Level</label>
              <select [(ngModel)]="candidat!.niveauScolaire" class="form-select">
                <option value="">Select level</option>
                <option value="Bac">Bac</option>
                <option value="Bac+2">Bac+2 (DEUG, BTS)</option>
                <option value="Licence">Licence (Bac+3)</option>
                <option value="Master">Master (Bac+5)</option>
                <option value="Doctorat">Doctorat (Bac+8)</option>
              </select>
            </div>
            <div>
              <label class="form-label">Experience (years)</label>
              <input type="text" [(ngModel)]="candidat!.experience" placeholder="e.g. 2-5 years" class="field"/>
            </div>
            <div>
              <label class="form-label">Driver's License</label>
              <input type="text" [(ngModel)]="candidat!.permisDeConduire" placeholder="B, C, D..." class="field"/>
            </div>
            <div>
              <label class="form-label">City</label>
              <input type="text" [(ngModel)]="candidat!.ville" class="field"/>
            </div>
            <div style="grid-column: 1 / -1;">
              <label class="form-label">LinkedIn Profile</label>
              <input type="url" [(ngModel)]="candidat!.linkedin" placeholder="https://linkedin.com/in/yourprofile" class="field"/>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
}) 
export class ProfileComponent implements OnInit {
  allTags: Tag[] = [];
  categories: string[] = [];
  tagsByCategory: { [key: string]: Tag[] } = {};
  selectedSkills: { [tagId: number]: string } = {};
  loading = true;
  saving = false;
  saved = false;
  currentUserId: number | null = null;
  activeTab: 'skills' | 'docs' | 'info' = 'skills';
  configureMode = false;
  
  candidat: Candidat | null = null;
  cvFile: File | null = null;
  lettreFile: File | null = null;
  uploadingCv = false;
  uploadingLettre = false;
  deletingCv = false;
  deletingLettre = false;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      // Try to fallback to stored user (in case of SPA reload) so uploads and tags still work
      const userJson = localStorage.getItem('user');
      if (userJson) {
        try {
          const user = JSON.parse(userJson);
          if (user && user.id) {
            this.currentUserId = user.id;
          }
        } catch (e) {
          // ignore parse errors
        }
      }

      // still attempt to load tags (may be public) and candidate if we have an id fallback
      this.loadTags();
      if (this.currentUserId) this.loadCandidat();
      if (!token && !this.currentUserId) {
        // user truly not logged in
        alert('Please login first');
      }
      return;
    }
    this.loadCurrentUser();
  }

  loadCurrentUser() {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    this.http.get<any>('http://localhost:8000/api/auth/me', { headers }).subscribe({
      next: (user) => {
        this.currentUserId = user.id;
        this.loadCandidat();
        this.loadTags();
      },
      error: () => {
        // Token may be expired — fallback to stored user if available
        const userJson = localStorage.getItem('user');
        if (userJson) {
          try {
            const user = JSON.parse(userJson);
            if (user && user.id) this.currentUserId = user.id;
          } catch (e) { }
        }
        this.loadTags();
        if (this.currentUserId) this.loadCandidat();
      }
    });
  }

  loadCandidat() {
    if (!this.currentUserId) return;
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    this.http.get<Candidat>(`http://localhost:8000/api/candidats/${this.currentUserId}`, { headers }).subscribe({
      next: (candidat) => {
        this.candidat = candidat;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  loadTags() {
    // Tags may be publicly available; avoid sending an invalid/expired token header
    const token = localStorage.getItem('accessToken');
    const headers = token ? new HttpHeaders().set('Authorization', `Bearer ${token}`) : undefined;
    this.http.get<Tag[]>('http://localhost:8000/api/tags', headers ? { headers } : {}).subscribe({
      next: (tags) => {
        this.allTags = tags;
        this.categories = [...new Set(tags.map(t => t.categorie))];
        this.categories.forEach(cat => {
          this.tagsByCategory[cat] = tags.filter(t => t.categorie === cat);
        });
        this.loadMySkills();
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  loadMySkills() {
    if (!this.currentUserId) {
      this.loading = false;
      return;
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    this.http.get<any[]>(`http://localhost:8000/api/candidats/${this.currentUserId}/competences`, { headers }).subscribe({
      next: (skills) => {
        skills.forEach((skill: any) => {
          this.selectedSkills[skill.tag.id] = skill.niveau;
        });
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  isSelected(tagId: number): boolean { return tagId in this.selectedSkills; }

  toggleTag(tagId: number) {
    if (this.isSelected(tagId)) {
      delete this.selectedSkills[tagId];
    } else {
      this.selectedSkills[tagId] = 'DEBUTANT';
    }
  }

  getNiveau(tagId: number): string { return this.selectedSkills[tagId] || 'DEBUTANT'; }

  setNiveau(tagId: number, event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedSkills[tagId] = value;
  }

  hasSelectedSkills(): boolean { return Object.keys(this.selectedSkills).length > 0; }

  getSelectedTagIds(): number[] { return Object.keys(this.selectedSkills).map(id => parseInt(id)); }

  getTagName(tagId: number): string {
    const tag = this.allTags.find(t => t.id === tagId);
    return tag ? tag.libelle : '';
  }

  saveProfile() {
    this.saving = true;
    this.saved = false;
    const headers = new HttpHeaders()
      .set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`)
      .set('Content-Type', 'application/json');
    
    // Save skills
    if (this.hasSelectedSkills()) {
      const competences = Object.entries(this.selectedSkills).map(([tagId, niveau]) => ({
        tagId: parseInt(tagId),
        niveau: niveau
      }));
      this.http.put(`http://localhost:8000/api/candidats/${this.currentUserId}/competences`, competences, { headers }).subscribe({
        complete: () => {
          this.http.post(`http://localhost:8000/api/candidatures/recalculate/candidat/${this.currentUserId}`, {}, { headers }).subscribe();
        }
      });
    }
    
    // Save all personal info
    if (this.candidat) {
      const updateData = {
        nom: this.candidat.nom || '',
        telephone: this.candidat.telephone || '',
        ville: this.candidat.ville || '',
        dateNaissance: this.candidat.dateNaissance || null,
        niveauScolaire: this.candidat.niveauScolaire || '',
        experience: this.candidat.experience || '',
        permisDeConduire: this.candidat.permisDeConduire || '',
        linkedin: this.candidat.linkedin || ''
      };
      this.http.put(`http://localhost:8000/api/candidats/${this.currentUserId}`, updateData, { headers }).subscribe({
        next: () => {
          this.saving = false;
          this.saved = true;
          this.loadCandidat();
          setTimeout(() => this.saved = false, 3000);
        },
        error: (err) => {
          this.saving = false;
          console.error('Save error:', err);
          alert('Failed to save profile');
        }
      });
    } else {
      this.saving = false;
      this.saved = true;
      setTimeout(() => this.saved = false, 3000);
    }
  }

  onCvSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.cvFile = input.files[0];
    }
  }

  onLettreSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.lettreFile = input.files[0];
    }
  }

  uploadCv() {
    const candidateId = this.currentUserId || (() => { try { const u = JSON.parse(localStorage.getItem('user')||'null'); return u?.id; } catch(e){return null;} })();
    if (!this.cvFile) {
      alert('Please select a CV file first');
      return;
    }
    if (!candidateId) {
      alert('Unable to determine candidate account. Please refresh and try again.');
      return;
    }
    this.uploadingCv = true;
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    const formData = new FormData();
    formData.append('file', this.cvFile);
    formData.append('candidatId', candidateId.toString());

    this.http.post('http://localhost:8000/api/files/upload-cv', formData, { headers, responseType: 'text' as 'json' }).subscribe({
      next: () => {
        this.uploadingCv = false;
        this.loadCandidat();
        alert('CV uploaded successfully!');
      },
      error: (err) => {
        this.uploadingCv = false;
        console.error('CV upload failed', err);
        const message = err?.error || 'Failed to upload CV';
        alert(typeof message === 'string' ? message : 'Failed to upload CV');
      }
    });
  }

  uploadLettre() {
    const candidateId = this.currentUserId || (() => { try { const u = JSON.parse(localStorage.getItem('user')||'null'); return u?.id; } catch(e){return null;} })();
    if (!this.lettreFile) {
      alert('Please select a cover letter file first');
      return;
    }
    if (!candidateId) {
      alert('Unable to determine candidate account. Please refresh and try again.');
      return;
    }
    this.uploadingLettre = true;
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    const formData = new FormData();
    formData.append('file', this.lettreFile);
    formData.append('candidatId', candidateId.toString());

    this.http.post('http://localhost:8000/api/files/upload-lettre', formData, { headers, responseType: 'text' as 'json' }).subscribe({
      next: () => {
        this.uploadingLettre = false;
        this.loadCandidat();
        alert('Cover letter uploaded successfully!');
      },
      error: (err) => {
        this.uploadingLettre = false;
        console.error('Cover letter upload failed', err);
        const message = err?.error || 'Failed to upload cover letter';
        alert(typeof message === 'string' ? message : 'Failed to upload cover letter');
      }
    });
  }

  deleteCv() {
    if (!this.candidat?.cvUrl) return;
    this.deletingCv = true;
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    this.http.delete(`http://localhost:8000/api/files/cv/${this.candidat.cvUrl}`, { headers, responseType: 'text' as 'json' }).subscribe({
      next: () => {
        this.deletingCv = false;
        this.loadCandidat();
        alert('CV deleted successfully');
      },
      error: () => {
        this.deletingCv = false;
        alert('Failed to delete CV');
      }
    });
  }

  deleteLettre() {
    if (!this.candidat?.lettreMotivationUrl) return;
    this.deletingLettre = true;
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('accessToken')}`);
    this.http.delete(`http://localhost:8000/api/files/lettre/${this.candidat.lettreMotivationUrl}`, { headers, responseType: 'text' as 'json' }).subscribe({
      next: () => {
        this.deletingLettre = false;
        this.loadCandidat();
        alert('Cover letter deleted successfully');
      },
      error: () => {
        this.deletingLettre = false;
        alert('Failed to delete cover letter');
      }
    });
  }
}