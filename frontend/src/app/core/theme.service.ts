import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private isDark = true;

  constructor() {
    const saved = localStorage.getItem('theme');
    this.isDark = saved !== 'light';
    this.applyBodyClass();
  }

  toggle() {
    this.isDark = !this.isDark;
    localStorage.setItem('theme', this.isDark ? 'dark' : 'light');
    this.applyBodyClass();
  }

  private applyBodyClass() {
    if (typeof document !== 'undefined') {
      document.body.className = document.body.className
        .replace(/\blight\b/g, '')
        .replace(/\bdark\b/g, '')
        .trim();
      if (!this.isDark) {
        document.body.classList.add('light');
      } else {
        document.body.classList.add('dark');
      }
    }
  }

  isDarkMode() {
    return this.isDark;
  }
}
