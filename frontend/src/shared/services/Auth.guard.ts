import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { LoginService } from '../../app/login/login.service';


@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router,
    private loginService: LoginService,
    private snackBar: MatSnackBar) { }


  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const isLoggedIn = this.loginService.isAuthenticated();

    // If the user is logged in and tries to access login or signup routes
    if (isLoggedIn && (state.url === '/login' || state.url === '/signup')) {
      this.router.navigateByUrl('/'); 
      this.openSnackBar("You are already logged in!");
      return false; 
    }

    // If the user is not logged in and tries to access routes other than login/signup
    if (!isLoggedIn && (state.url !== '/login' && state.url !== '/signup')) {
      this.router.navigateByUrl('/login');
      this.openSnackBar("You must log in to access this page.");
      return false; 
    }

    return true; 
  }

  openSnackBar(message: string) {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'start',
      verticalPosition: 'bottom'
    });
  }
  
}
