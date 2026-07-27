import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError, switchMap } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Eğer hata 401 ise ve bu hatayı veren istek /refresh endpointi değilse
      // (Refresh'in kendisi 401 verirse sonsuz döngüye girmemek için)
      if (error.status === 401 && !req.url.includes('/refresh')) {
        return authService.refreshToken().pipe(
          switchMap(() => {
            // Eğer errorInterceptor, authInterceptor'dan ÖNCE çalışıyorsa
            // next(req) dediğimizde istek authInterceptor'a gider ve yeni token eklenir!
            return next(req);
          }),
          catchError((refreshError) => {
            authService.logout();
            return throwError(() => refreshError);
          })
        );
      }

      return throwError(() => error);
    })
  );
};
