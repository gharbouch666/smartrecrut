import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CandidatesComponent } from './candidates/candidates.component';
import { JobsComponent } from './jobs/jobs.component';
import { ApplicationsComponent } from './applications/applications.component';
import { SettingsComponent } from './settings/settings.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ForgotPasswordComponent } from './auth/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './auth/reset-password/reset-password.component';
import { TagsComponent } from './admin/tags/tags.component';
import { AdminComponent } from './admin/admin.component';
import { UsersComponent } from './admin/users.component';
import { ProfileComponent } from './profile/profile.component';
import { RecruteurProfileComponent } from './recruteur-profile/recruteur-profile.component';
import { AdminProfileComponent } from './admin-profile/admin-profile.component';
import { ChatComponent } from './chat/chat.component';
import { LandingComponent } from './landing/landing.component';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register', component: RegisterComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'auth/forgot-password', component: ForgotPasswordComponent },
  { path: 'forgot-password', redirectTo: '/auth/forgot-password', pathMatch: 'full' },
  { path: 'auth/reset-password', component: ResetPasswordComponent },
  { path: 'reset-password', redirectTo: '/auth/reset-password', pathMatch: 'full' },
  { 
    path: 'dashboard', 
    component: DashboardComponent,
    data: { roles: ['ADMIN', 'RECRUTEUR', 'CANDIDAT'] }
  },
  { 
    path: 'candidates', 
    component: CandidatesComponent,
    data: { roles: ['ADMIN', 'RECRUTEUR'] }
  },
  { 
    path: 'jobs', 
    component: JobsComponent,
    data: { roles: ['RECRUTEUR', 'CANDIDAT'] }
  },
  { 
    path: 'my-applications', 
    component: ApplicationsComponent,
    data: { roles: ['CANDIDAT'] }
  },
  { 
    path: 'applications', 
    component: ApplicationsComponent,
    data: { roles: ['RECRUTEUR'] }
  },
  { 
    path: 'profile', 
    component: ProfileComponent,
    data: { roles: ['CANDIDAT'] }
  },
  { 
    path: 'settings', 
    component: SettingsComponent,
    data: { roles: ['CANDIDAT'] }
  },
  { 
    path: 'recruteur-profile', 
    component: RecruteurProfileComponent,
    data: { roles: ['RECRUTEUR'] }
  },
  { 
    path: 'admin-profile', 
    component: AdminProfileComponent,
    data: { roles: ['ADMIN'] }
  },
  { 
    path: 'admin', 
    component: AdminComponent,
    data: { roles: ['ADMIN'] }
  },
  { 
    path: 'admin/tags', 
    component: TagsComponent,
    data: { roles: ['ADMIN'] }
  },
  { 
    path: 'users', 
    component: UsersComponent,
    data: { roles: ['ADMIN'] }
  },
  { 
    path: 'chat', 
    component: ChatComponent,
    data: { roles: ['ADMIN', 'RECRUTEUR', 'CANDIDAT'] }
  },
  { path: '**', redirectTo: '/dashboard' }
];

