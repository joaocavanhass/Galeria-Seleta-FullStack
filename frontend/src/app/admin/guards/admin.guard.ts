import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

export const adminGuard: CanActivateFn = () => {
  const auth   = inject(AuthService);
  const router = inject(Router);

  const usuario = auth.currentUser();

  if (usuario?.papel === 'admin') {
    return true;
  }

  // Usuário logado mas não é admin → vai para a loja
  if (usuario) {
    return router.createUrlTree(['/']);
  }

  // Não logado → vai para login
  return router.createUrlTree(['/login']);
};
