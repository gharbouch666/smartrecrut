import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TagsComponent } from './tags/tags.component';

const routes: Routes = [
  { path: '', redirectTo: 'tags', pathMatch: 'full' },
  { path: 'tags', component: TagsComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }

