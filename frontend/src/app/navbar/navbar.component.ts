import { Component, Inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../login/login.service';
import { TokenService } from '../login/token.service';
import { UploadSongDialogComponent } from '../upload-song-dialog/upload-song-dialog.component';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { CreatePlaylistDialogComponent } from '../create-playlist-dialog/create-playlist-dialog.component';
import { PlaylistService } from '../../shared/services/Playlist.service';
import { PlaylistDto } from '../../shared/models/PlaylistDto.model';
import { AddToPlaylistDialogComponent } from '../add-to-playlist-dialog/add-to-playlist-dialog.component';

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
    private playlistService: PlaylistService,
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

  openCreatePlaylistDialog(): void {
    const dialogRef = this.dialog.open(CreatePlaylistDialogComponent, {
      panelClass: 'my-css-class',
      width: '400px'
    });
  }

  logout() {
    this.loginService.logout();
    // Reload the page
    window.location.reload();
  }
  
}
