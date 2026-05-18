import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-tags',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
    <div class="page-header mb-8">
      <div class="flex justify-between items-center">
        <div>
          <h1 class="page-title">Skills Library</h1>
          <p class="page-subtitle">Manage technical tags and competencies</p>
        </div>
        <button class="btn-primary" (click)="showAddForm = true">
          + New Skill
        </button>
      </div>
    </div>

    <div *ngIf="showAddForm" class="card mb-6">
      <h2 class="card-title">Add New Skill</h2>
      <form [formGroup]="tagForm" (ngSubmit)="addTag()" class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="block text-sm font-medium mb-2">Skill Name</label>
          <input formControlName="libelle" placeholder="e.g. React, Python, AWS" class="input">
        </div>
        <div>
          <label class="block text-sm font-medium mb-2">Category</label>
          <select formControlName="categorie" class="input">
            <option value="TECH">TECH - Technical</option>
            <option value="SOFT">SOFT - Soft Skills</option>
            <option value="LANG">LANG - Languages</option>
            <option value="TOOL">TOOL - Tools</option>
            <option value="FRAME">FRAME - Frameworks</option>
          </select>
        </div>
        <div class="md:col-span-2 flex gap-4">
          <button type="submit" [disabled]="tagForm.invalid" class="btn-primary">Add Skill</button>
          <button type="button" (click)="cancelAdd()" class="btn-secondary">Cancel</button>
        </div>
      </form>
    </div>

    <div *ngIf="loading" class="flex justify-center py-16">
      <div class="loading-spinner"></div>
    </div>

    <div *ngIf="!loading && tags.length === 0" class="card text-center py-16">
      <svg class="w-16 h-16 mx-auto mb-4 text-[var(--text-muted)]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
      </svg>
      <h3 class="text-xl font-semibold mb-2">No Skills Yet</h3>
      <p class="text-[var(--text-muted)] mb-6">Add your first technical skill to get started</p>
      <button class="btn-primary" (click)="showAddForm = true">Add First Skill</button>
    </div>

    <div *ngIf="!loading && tags.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
      <div *ngFor="let tag of tags" class="card hover:border-[var(--accent)] transition-all cursor-pointer group">
        <div class="flex justify-between items-start mb-4">
          <h3 class="text-lg font-semibold truncate">{{tag.libelle}}</h3>
          <span class="badge">{{tag.categorie}}</span>
        </div>
        <div class="flex gap-2 opacity-0 group-hover:opacity-100 transition-all">
          <button (click)="editTag(tag); $event.stopPropagation()" class="btn-accent text-xs py-2 px-3">
            Edit
          </button>
          <button (click)="deleteTag(tag.id); $event.stopPropagation()" class="btn-danger text-xs py-2 px-3">
            Delete
          </button>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class TagsComponent implements OnInit {
  tags: any[] = [];
  showAddForm = false;
  tagForm: FormGroup;
  editingTag: any = null;
  loading = true;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.tagForm = this.fb.group({
      libelle: ['', []],
      categorie: ['TECH', []]
    });
  }

  ngOnInit() {
    this.loadTags();
  }

  loadTags() {
    this.loading = true;
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    this.http.get<any[]>('http://localhost:8000/api/tags', { headers }).subscribe({
      next: (data) => {
        this.tags = data;
        this.loading = false;
      },
      error: () => {
        this.tags = [];
        this.loading = false;
      }
    });
  }

  editTag(tag: any) {
    this.editingTag = tag;
    this.tagForm.patchValue(tag);
    this.showAddForm = true;
  }

  cancelAdd() {
    this.showAddForm = false;
    this.tagForm.reset({ categorie: 'TECH' });
    this.editingTag = null;
  }

  addTag() {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    const data = this.tagForm.value;

    if (this.editingTag) {
      this.http.put(`http://localhost:8000/api/tags/${this.editingTag.id}`, data, { headers }).subscribe({
        next: () => {
          this.loadTags();
          this.cancelAdd();
        },
        error: (err) => console.error('Update tag failed', err)
      });
    } else {
      this.http.post('http://localhost:8000/api/tags', data, { headers }).subscribe({
        next: () => {
          this.loadTags();
          this.cancelAdd();
        },
        error: (err) => console.error('Add tag failed', err)
      });
    }
  }

  deleteTag(id: number) {
    if (!confirm('Delete this skill?')) return;
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
    this.http.delete(`http://localhost:8000/api/tags/${id}`, { headers }).subscribe({
      next: () => this.loadTags(),
      error: (err) => console.error('Delete failed', err)
    });
  }
}