import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { PlaylistDto } from '../../shared/models/PlaylistDto.model';
import { PlaylistService } from '../../shared/services/Playlist.service';
import { CreatePlaylistDialogComponent } from '../create-playlist-dialog/create-playlist-dialog.component';

@Component({
  selector: 'app-add-to-playlist-dialog-component',
  templateUrl: './add-to-playlist-dialog.component.html'
})
export class AddToPlaylistDialogComponent {
  selectedPlaylistId: number | undefined;

  constructor(
    private playlistService: PlaylistService,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<AddToPlaylistDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public playlists: PlaylistDto[]
  ) {}

  onClose(): void {
    this.dialogRef.close();
  }

  onAdd(): void {
    this.dialogRef.close(this.selectedPlaylistId);
  }

  openCreatePlaylistDialog(): void {
    const dialogRef = this.dialog.open(CreatePlaylistDialogComponent, {
      width: '400px'
    });

    dialogRef.afterClosed().subscribe((newPlaylist: { userId: number; }) => {
      if (newPlaylist) {
        // Refresh the playlist list to include the newly created playlist
        this.playlistService.getPlaylistsByUserId(newPlaylist.userId).subscribe(updatedPlaylists => {
          this.playlists = updatedPlaylists;
        });
      }
    });
  }
}
