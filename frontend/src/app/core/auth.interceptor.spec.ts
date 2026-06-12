import { TestBed } from '@angular/core/testing';
import { HttpRequest } from '@angular/common/http';
import { AuthInterceptor, authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  it('should be exported', () => {
    expect(authInterceptor).toBeTruthy();
  });

  it('should have an AuthInterceptor class', () => {
    expect(AuthInterceptor).toBeTruthy();
  });
});

