import { Injectable, Renderer2, RendererFactory2 } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private renderer: Renderer2;
  private isDark = true;

  constructor(rendererFactory: RendererFactory2) {
    this.renderer = rendererFactory.createRenderer(null, null);
    const saved = localStorage.getItem('theme');
    if (saved === 'light') {
      this.disableDark();
    } else {
      this.enableDark();
    }
  }

  toggle() {
    if (this.isDark) {
      this.disableDark();
    } else {
      this.enableDark();
    }
  }

  private enableDark() {
    this.renderer.addClass(document.body, 'dark');
    document.documentElement.classList.add('dark');
    localStorage.setItem('theme', 'dark');
    this.isDark = true;
  }

  private disableDark() {
    this.renderer.removeClass(document.body, 'dark');
    document.documentElement.classList.remove('dark');
    localStorage.setItem('theme', 'light');
    this.isDark = false;
  }

  isDarkMode() {
    return this.isDark;
  }
}
