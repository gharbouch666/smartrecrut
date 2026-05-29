import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-tags',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './tags.component.html',
  styleUrls: ['./tags.component.scss']
})
export class TagsComponent implements OnInit {
  tags: any[] = [];
  showAddForm = false;
  tagForm: FormGroup;
  editingTag: any = null;
  loading = true;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.tagForm = this.fb.group({
      libelle: [''],
      categorie: ['TECH']
    });
  }

  ngOnInit() { this.loadTags(); }

  loadTags() {
    this.loading = true;
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', 'Bearer ' + token);
    this.http.get<any[]>('http://localhost:8000/api/tags', { headers }).subscribe({
      next: (data) => { this.tags = data; this.loading = false; },
      error: () => { this.tags = []; this.loading = false; }
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

  saveTag() {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', 'Bearer ' + token);
    const data = this.tagForm.value;

    if (this.editingTag) {
      this.http.put('http://localhost:8000/api/tags/' + this.editingTag.id, data, { headers }).subscribe({
        next: () => { this.loadTags(); this.cancelAdd(); },
        error: (err) => console.error('Update tag failed', err)
      });
    } else {
      this.http.post('http://localhost:8000/api/tags', data, { headers }).subscribe({
        next: () => { this.loadTags(); this.cancelAdd(); },
        error: (err) => console.error('Add tag failed', err)
      });
    }
  }

  deleteTag(id: number) {
    if (!confirm('Delete this skill?')) return;
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders().set('Authorization', 'Bearer ' + token);
    this.http.delete('http://localhost:8000/api/tags/' + id, { headers }).subscribe({
      next: () => this.loadTags(),
      error: (err) => console.error('Delete failed', err)
    });
  }
}
