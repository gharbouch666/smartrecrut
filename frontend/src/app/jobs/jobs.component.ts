import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { AiService } from '../core/ai.service';

interface Tag {
  id: number;
  libelle: string;
  categorie: string;
}

interface JobTag {
  tagId: number;
  obligatoire: boolean;
  poids: number;
}

@Component({
  selector: 'app-jobs',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './jobs.component.html',
  styleUrls: ['./jobs.component.scss']
})
export class JobsComponent implements OnInit {
  jobForm: FormGroup;
  jobs: any[] = [];
  allTags: Tag[] = [];
  categories: string[] = [];
  tagsByCategory: { [key: string]: Tag[] } = {};
  selectedTags: JobTag[] = [];
  showForm = false;
  loading = false;
  showNewSkillForm = false;
  newSkillName = '';
  newSkillCategory = 'TECH';
  applyingToJob: number | null = null;
  appliedJobIds: number[] = [];
  userRole: string = '';
  currentUserId: number = 0;
  selectedJobId: number | null = null;
  aiLoading = false;
  aiDescription = '';

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router, private ai: AiService) {
    this.jobForm = this.fb.group({
      titre: ['', Validators.required],
      description: ['', Validators.required],
      localisation: ['', Validators.required],
      departement: [''],
      typeContrat: ['CDI', Validators.required],
      scoreMinimum: [50]
    });
  }

  ngOnInit() {
    const token = localStorage.getItem('accessToken');
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      this.userRole = user.type || '';
      this.currentUserId = user.id || 0;
    }
    this.loadJobs();
    if (this.userRole === 'RECRUTEUR' || this.userRole === 'ADMIN' || this.userRole === 'ADMINISTRATEUR') {
      this.loadTags();
    }
    if (this.userRole === 'CANDIDAT' && this.currentUserId) {
      this.loadAppliedJobIds();
    }
  }

  loadAppliedJobIds() {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.get<any[]>(`http://localhost:8000/api/candidatures/candidat/${this.currentUserId}`, { headers }).subscribe({
      next: (applications) => {
        this.appliedJobIds = applications.map((a: any) => a.offre?.id || a.offreId).filter(Boolean);
      },
      error: () => {}
    });
  }

  get isRecruiter(): boolean {
    return this.userRole === 'RECRUTEUR' || this.userRole === 'ADMIN' || this.userRole === 'ADMINISTRATEUR';
  }

  get isCandidate(): boolean {
    return this.userRole === 'CANDIDAT';
  }

  loadTags() {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.get<Tag[]>('http://localhost:8000/api/tags', { headers }).subscribe({
      next: (tags) => {
        this.allTags = tags;
        this.categories = [...new Set(tags.map(t => t.categorie))];
        this.categories.forEach(cat => {
          this.tagsByCategory[cat] = tags.filter(t => t.categorie === cat);
        });
      },
      error: (err) => console.error('Failed to load tags', err)
    });
  }

  loadJobs() {
    this.loading = true;
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const url = this.isCandidate 
      ? 'http://localhost:8000/api/offres/with-tags'
      : 'http://localhost:8000/api/offres';
    this.http.get(url, { headers }).subscribe({
      next: (data: any) => {
        this.jobs = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load jobs', err);
        this.jobs = [];
        this.loading = false;
      }
    });
  }

  toggleTagSelection(tagId: number, isRequired: boolean) {
    const existing = this.selectedTags.find(t => t.tagId === tagId);
    if (existing) {
      existing.obligatoire = isRequired;
    } else {
      this.selectedTags.push({ tagId, obligatoire: isRequired, poids: 1.0 });
    }
  }

  isTagSelected(tagId: number): boolean {
    return this.selectedTags.some(t => t.tagId === tagId);
  }

  isTagRequired(tagId: number): boolean {
    const tag = this.selectedTags.find(t => t.tagId === tagId);
    return tag ? tag.obligatoire : false;
  }

  setTagWeight(tagId: number, weight: number) {
    const tag = this.selectedTags.find(t => t.tagId === tagId);
    if (tag) {
      tag.poids = weight;
    }
  }

  getTagWeight(tagId: number): number {
    const tag = this.selectedTags.find(t => t.tagId === tagId);
    return tag ? tag.poids : 1.0;
  }

  removeTag(tagId: number) {
    this.selectedTags = this.selectedTags.filter(t => t.tagId !== tagId);
  }

  toggleForm() {
    this.showForm = !this.showForm;
    if (!this.showForm) {
      this.jobForm.reset({ typeContrat: 'CDI', scoreMinimum: 50 });
      this.selectedTags = [];
    }
  }

  onSubmit() {
    if (this.jobForm.invalid) return;
    const token = localStorage.getItem('accessToken');
    const userId = localStorage.getItem('userId');
    const headers = new HttpHeaders()
      .set('Authorization', `Bearer ${token}`)
      .set('Content-Type', 'application/json');

    const jobData: any = {
      titre: this.jobForm.value.titre,
      description: this.jobForm.value.description,
      localisation: this.jobForm.value.localisation,
      departement: this.jobForm.value.departement,
      typeContrat: this.jobForm.value.typeContrat,
      scoreMinimum: this.jobForm.value.scoreMinimum || 50,
      statut: 'OUVERTE'
    };

    if (userId && !this.selectedJobId) {
      jobData.recruteur = { id: parseInt(userId) };
    }

    if (this.selectedTags.length > 0 && !this.selectedJobId) {
      const payload = {
        offre: jobData,
        tags: this.selectedTags.map(t => ({ tagId: t.tagId, obligatoire: t.obligatoire, poids: t.poids }))
      };
      this.http.post('http://localhost:8000/api/offres/with-tags', payload, { headers }).subscribe({
        next: () => {
          alert('Job posted with skills!');
          this.loadJobs();
          this.toggleForm();
        },
        error: () => {
          // Fallback: save job without tags
          this.http.post('http://localhost:8000/api/offres', jobData, { headers }).subscribe({
            next: () => {
              alert('Job posted (tags will need to be added separately)');
              this.loadJobs();
              this.toggleForm();
            }
          });
        }
      });
    } else {
      const submitObservable = this.selectedJobId 
        ? this.http.put(`http://localhost:8000/api/offres/${this.selectedJobId}`, jobData, { headers })
        : this.http.post('http://localhost:8000/api/offres', jobData, { headers });

      submitObservable.subscribe({
        next: () => {
          alert(this.selectedJobId ? 'Job updated successfully!' : 'Job posted successfully!');
          this.loadJobs();
          this.toggleForm();
          this.selectedJobId = null;
        },
        error: (err) => {
          if (err.status === 401 || err.status === 403) {
            alert('Authorization failed. Please log in again and ensure your recruiter account is active.');
          } else {
            console.error('Failed to save job', err);
          }
        }
      });
    }
  }

  addNewSkill() {
    if (!this.newSkillName.trim()) return;
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders()
      .set('Authorization', `Bearer ${token}`)
      .set('Content-Type', 'application/json');

    this.http.post('http://localhost:8000/api/tags', { 
      libelle: this.newSkillName.trim(), 
      categorie: this.newSkillCategory 
    }, { headers }).subscribe({
      next: () => {
        this.loadTags();
        this.newSkillName = '';
        this.showNewSkillForm = false;
      },
      error: (err) => console.error('Failed to add skill', err)
    });
  }

  applyToJob(jobId: number) {
    if (!this.currentUserId) {
      alert('Please log in to apply');
      return;
    }
    this.applyingToJob = jobId;
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders()
      .set('Authorization', `Bearer ${token}`)
      .set('Content-Type', 'application/json');

    this.http.post('http://localhost:8000/api/candidatures/apply', {
      offreId: jobId,
      candidatId: this.currentUserId
    }, { headers }).subscribe({
      next: () => {
        alert('Application submitted successfully!');
        this.applyingToJob = null;
        this.loadAppliedJobIds();  // Refresh applied jobs
        this.router.navigate(['/my-applications']);
      },
      error: (err) => {
        this.applyingToJob = null;
        if (err.status === 409) {
          alert('You have already applied to this job');
        } else {
          alert('Failed to apply: ' + (err.error?.message || err.statusText));
        }
      }
    });
  }

  hasApplied(jobId: number): boolean {
    return this.appliedJobIds.includes(jobId);
  }

  editJob(job: any) {
    this.selectedJobId = job.id;
    this.selectedTags = [];
    
    this.jobForm.patchValue({
      titre: job.titre,
      description: job.description,
      localisation: job.localisation,
      departement: job.departement || '',
      typeContrat: job.typeContrat || 'CDI',
      scoreMinimum: job.scoreMinimum || 50
    });
    
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.get<any[]>(`http://localhost:8000/api/offres/${job.id}/tags`, { headers }).subscribe({
      next: (tags) => {
        this.selectedTags = tags.map((t: any) => ({
          tagId: t.tag?.id || t.tagId,
          obligatoire: t.obligatoire,
          poids: t.poids || 1.0
        }));
      },
      error: () => {}
    });
    
    this.showForm = true;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  deleteJob(jobId: number) {
    if (!confirm('Are you sure you want to delete this job?')) return;
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders()
      .set('Authorization', `Bearer ${token}`)
      .set('Content-Type', 'application/json');

    this.http.delete(`http://localhost:8000/api/offres/${jobId}`, { headers }).subscribe({
      next: () => {
        alert('Job deleted successfully!');
        this.loadJobs();
      },
      error: (err) => {
        if (err.status === 401 || err.status === 403) {
          alert('Authorization failed. Please log in again and ensure your recruiter account is active.');
        } else {
          alert('Failed to delete job: ' + (err.error?.message || err.statusText));
        }
      }
    });
  }

  generateWithAI() {
    const title = this.jobForm.value.titre;
    if (!title) {
      alert('Please enter a job title first');
      return;
    }
    
    this.aiLoading = true;
    this.aiDescription = '';
    
    this.ai.generateOffer(title).subscribe({
      next: (res) => {
        this.aiLoading = false;
        if (res.success && res.offer) {
          this.aiDescription = res.offer;
          if (!this.jobForm.value.description) {
            this.jobForm.patchValue({ description: res.offer });
          }
        } else {
          alert(res.error || 'AI generation failed');
        }
      },
      error: () => {
        this.aiLoading = false;
        alert('AI service unavailable. Make sure AI service is running on port 8001');
      }
    });
  }
}
