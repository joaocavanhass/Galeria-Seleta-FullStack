import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AdminAuthService } from '../services/admin-auth.service';

/** Protege todas as rotas /admin/* — redireciona para /login se não autenticado */
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AdminAuthService);
  const router = inject(Router);

  if (auth.isAuthenticated()) {
    return true;
  }
  return router.createUrlTree(['/login']);
};