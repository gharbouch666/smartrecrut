import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

export const authInterceptor = (req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> => {
  const token = localStorage.getItem('accessToken');
  console.log('authInterceptor: token present =', !!token);
  if (token) {
    const cloned = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    console.log('authInterceptor: added token to', req.url);
    return next.handle(cloned);
  }
  console.log('authInterceptor: NO TOKEN for', req.url);
  return next.handle(req);
};

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('accessToken');
    console.log('AuthInterceptor: token present =', !!token);
    if (token) {
      const cloned = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      console.log('AuthInterceptor: added token to', req.url);
      return next.handle(cloned);
    }
    console.log('AuthInterceptor: NO TOKEN for', req.url);
    return next.handle(req);
  }
}
