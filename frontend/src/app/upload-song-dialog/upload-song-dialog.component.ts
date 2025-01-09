import { Component } from '@angular/core';
import { SongService } from '../../shared/services/Song.service';
import { SongDto } from '../../shared/models/SongDto.model';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-upload-song-dialog',
  templateUrl: './upload-song-dialog.component.html',
})
export class UploadSongDialogComponent {
  // Fields
  songName: string = '';
  artistName: string = '';
  genre: string = '';
  selectedFile: File | null = null;

  constructor(
    private songService: SongService,
    public dialogRef: MatDialogRef<UploadSongDialogComponent>,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  uploadSong(): void {
    if (this.selectedFile) {
      const songDto: SongDto = {
        id: 0,
        name: this.songName,
        artist: this.artistName,
        genre: this.genre,
        likeCount: 0,
        userId: 0
      };

      this.songService.uploadSong(this.selectedFile, songDto).subscribe({
        next: (response) => {
          this.openSnackBar("Song uploaded successfully! Enjoy your music!");
          // Check if the current route is 'all-songs'
          if (this.router.url === '/all-songs' || this.router.url === '/me') {
            // Reload the page
            window.location.reload();
          }
          this.cancel();
        },
        error: (error) => {
          console.error('Error uploading song:', error);
          this.openSnackBar("An unexpected error occurred while uploading your song!");
        },
      });
    } else {
      console.error('No file selected');
    }
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  cancel() {
    this.dialogRef.close();
  }

  openSnackBar(message: string) {
    // Display a snackbar with the given message
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'start',
      verticalPosition: 'bottom'
    });
  }

}
