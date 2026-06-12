import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private isDark = true;

  constructor() {
    const saved = localStorage.getItem('theme');
    this.isDark = saved !== 'light';
  }

  toggle() {
    this.isDark = !this.isDark;
    localStorage.setItem('theme', this.isDark ? 'dark' : 'light');
  }

  isDarkMode() {
    return this.isDark;
  }
}
