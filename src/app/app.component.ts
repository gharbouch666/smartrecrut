import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from './layout/sidebar/sidebar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, SidebarComponent],
  template: `
    @if (showSidebar) {
      <div class="flex min-h-screen" style="background: var(--ink)">
        <app-sidebar></app-sidebar>
        <main class="flex-1 ml-72 p-8 overflow-auto" style="background: var(--ink)">
          <router-outlet></router-outlet>
        </main>
      </div>
    } @else {
      <router-outlet></router-outlet>
    }
  `,
  styles: []
})
export class AppComponent implements OnInit {
  showSidebar = false;

  constructor(public router: Router) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.updateSidebarVisibility(event.urlAfterRedirects);
      }
    });
  }

  ngOnInit() {
    if (!document.body.classList.contains('dark')) {
      document.body.classList.add('dark');
    }
    this.updateSidebarVisibility(this.router.url);
  }

  updateSidebarVisibility(url: string) {
    if (url.includes('#')) {
      url = url.split('#')[0];
    }
    const noSidebarRoutes = ['/', '/login', '/register', ''];
    const isAuth = noSidebarRoutes.some(route => url === route || url.startsWith('/auth'));
    this.showSidebar = !isAuth;
  }
}

