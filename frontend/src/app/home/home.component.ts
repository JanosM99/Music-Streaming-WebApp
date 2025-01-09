import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { UploadSongDialogComponent } from '../upload-song-dialog/upload-song-dialog.component';
import { LoginService } from '../login/login.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
})
export class HomeComponent {
  userId: number | undefined;

  constructor(
    private router: Router,
    public dialog: MatDialog,
    private loginService: LoginService,
    private snackBar: MatSnackBar
  ) {}

  // Navigate to AllSongs
  navigateToAllSongs() {
    this.router.navigateByUrl('/all-songs');
  }

  // Navigate to AllPlaylists
  navigateToAllPlaylists() {
    this.router.navigateByUrl('/all-playlists');
  }

  // Open Upload Song Dialog
  openUploadSongDialog(): void {
    if (this.loginService.isAuthenticated()) {
    this.dialog.open(UploadSongDialogComponent, {
      panelClass: 'open-dialog',
      width: '60%',
      height: '60%',
      data: { userId: this.userId }
    });
    }
    else {
      this.openSnackBar('You must be logged in to upload a song.');
    }
  }

  openSnackBar(message: string) {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'start',
      verticalPosition: 'bottom'
    });
  }

}
