import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from './layout/sidebar/sidebar.component';
import { ThemeService } from './core/theme.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, SidebarComponent],
  template: `
    @if (showSidebar) {
      <div class="app-shell" [class.dark]="theme.isDarkMode()" [class.light]="!theme.isDarkMode()">
        <app-sidebar></app-sidebar>
        <main class="app-main">
          <div class="app-content">
            <router-outlet></router-outlet>
          </div>
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

  constructor(public router: Router, public theme: ThemeService) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.updateSidebarVisibility(event.urlAfterRedirects);
      }
    });
  }

  ngOnInit() {
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

