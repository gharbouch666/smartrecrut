import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminComponent } from './admin.component';
import { AdminRoutingModule } from './admin-routing.module';
import { TagsComponent } from './tags/tags.component';
import { UsersComponent } from './users.component';

@NgModule({
  declarations: [
    AdminComponent,
    TagsComponent,
    UsersComponent
  ],
  imports: [
    CommonModule,
    AdminRoutingModule
  ]
})
export class AdminModule { }