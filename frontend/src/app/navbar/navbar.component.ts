import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../login/login.service';
import { TokenService } from '../login/token.service';
import { UploadSongDialogComponent } from '../upload-song-dialog/upload-song-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
})
export class NavbarComponent implements OnInit {

  username: any;
  userId: number | null | undefined;

  constructor(
    public loginService: LoginService,
    public tokenService: TokenService,
    private router: Router,
    private dialog: MatDialog,
  ) { }


  ngOnInit(): void {
    this.username = this.tokenService.getUsername();
    this.userId = this.tokenService.getUserId();
  }

  // Navigate to Home
  navigateToHome() {
    this.router.navigateByUrl('/');
  }

  // Navigate to Sign Up
  navigateToSignUp() {
    this.router.navigateByUrl('/signup');
  }

  // Navigate to Login
  navigateToLogin() {
    this.router.navigateByUrl('/login');
  }

  // Navigate to MyProfile
  navigateToMyProfile() {
    this.router.navigateByUrl('/me');
  }

  // Navigate to UserManagement
  navigateToUserManagement() {
    this.router.navigateByUrl('/user-management');
  }

  openUploadSongDialog(): void {
    const dialogRef = this.dialog.open(UploadSongDialogComponent, {
      panelClass: 'open-dialog',
      width: '60%',
      height: '60%',
      data: {
        userId: this.userId
      }
    });
  }

  logout() {
    this.loginService.logout();
    // Reload the page
    window.location.reload();
  }
  
}
