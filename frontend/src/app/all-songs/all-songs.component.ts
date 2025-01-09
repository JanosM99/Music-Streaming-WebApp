import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SongService } from '../../shared/services/Song.service';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { SongDto } from '../../shared/models/SongDto.model';
import { TokenService } from '../../app/login/token.service'
import { LoginService } from '../login/login.service';
import { MatDialog } from '@angular/material/dialog';
import { PlaylistService } from '../../shared/services/Playlist.service';
import { PlaylistDto } from '../../shared/models/PlaylistDto.model';
import { AddToPlaylistDialogComponent } from '../add-to-playlist-dialog/add-to-playlist-dialog.component';


@Component({
  selector: 'app-user-details',
  templateUrl: './all-songs.component.html'
})
export class AllSongsComponent implements OnInit, AfterViewInit {
  
  length: number | undefined;
  pageIndex = 0; 
  pageEvent: PageEvent | undefined;
  filter = '';
  sortField = 'name';
  sortDirection = 'asc';
  sort = 'ascending';

  userId: number | undefined;
  songs: SongDto[] | undefined;
  availableSongs: MatTableDataSource<SongDto> = new MatTableDataSource<SongDto>([]);
  playlists: PlaylistDto[] = [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private songService: SongService,
    private playlistService: PlaylistService,
    public tokenService: TokenService,
    public snackBar: MatSnackBar,
    public router: Router,
    public loginService: LoginService,
    public dialog: MatDialog
  ) { }
  
  ngAfterViewInit(): void {
    this.availableSongs.paginator = this.paginator;
  }

  ngOnInit(): void {
    this.userId = Number(this.tokenService.getUserId())
    this.loadSongs();
    this.loadPlaylists();
    localStorage.setItem('returnUrl', "/all-songs");
  }

  async loadSongs() {
    try {
      const pageIndex =this.pageIndex;
      const sort = this.sortField + "," + this.sortDirection;
      const filter = this.filter;
      
      await this.songService.getSongsWithFilterSort(pageIndex, filter, sort).subscribe(data => {
        this.songs = data.content;
        this.length = data.totalItems;
        this.availableSongs = new MatTableDataSource(this.songs);
      });
    } catch (error) {
      console.error('Error loading users:', error);
    }
  }

  deleteSong(songId: number) {
    if (confirm('Are you sure you want to delete this song?')) {
      this.songService.deleteSong(songId).subscribe({
        next: () => {
          this.openSnackBar('Song deleted successfully');
          this.loadSongs(); // Reload the list after deletion
        },
        error: (error) => {
          console.error('Error deleting song:', error);
          this.openSnackBar('Failed to delete song');
        }
      });
    }
  }

  likeSong(songId: number) {
      this.songService.likeSong(songId).subscribe({
        next: () => {
          this.openSnackBar('You liked a song!');
          this.loadSongs(); // Reload the list after deletion
        },
        error: (error) => {
          console.error('Error liking song:', error);
          this.openSnackBar('Failed to like a song');
        }
      });
  }
  
  sortData(sort: any) {
    this.sortField = sort.active;
    if (sort.direction.length === 0 && this.sortDirection === 'asc') {
      sort.direction = 'desc';
    } else if (sort.direction.length === 0 && this.sortDirection === 'desc') {
      sort.direction = 'asc';
    } 
    this.sortDirection = sort.direction;
    this.loadSongs();
  }

  //Filter active/inactive
  updateFilteredSongs() {
    this.loadSongs();
  }

  applyFilter() {
    this.pageIndex = 0;
    this.paginator.pageIndex = 0;
    this.loadSongs();
  }

  handlePageEvent(event: PageEvent) {
    this.pageEvent = event;
    this.length = event.length;
    this.pageIndex = event.pageIndex;
    this.loadSongs();
  }

  navigateToSong(songId: number): void {
    this.router.navigate(['/track', songId]); 
  }

  // Load playlists for the user
  loadPlaylists(): void {
    this.playlistService.getPlaylistsByUserId(this.userId!).subscribe(playlists => {
      this.playlists = playlists;
    }, error => {
      console.error('Error loading playlists:', error);
    });
  }

  // Add song to selected playlist
  addSongToPlaylist(playlistId: number, songId: number): void {
    this.playlistService.addSongToPlaylist(playlistId, songId).subscribe({
      next: () => {
        this.openSnackBar('Song added to playlist successfully!');
      },
      error: (error) => {
        console.error('Error adding song to playlist:', error);
        this.openSnackBar('Failed to add song to playlist.');
      }
    });
  }

  openAddToPlaylistDialog(songId: number): void {
    const dialogRef = this.dialog.open(AddToPlaylistDialogComponent, {
      width: '300px',
      data: this.playlists, // Pass the playlists to the dialog
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.addSongToPlaylist(result, songId); // Call method to add song to playlist
      }
    });
  }

  openSnackBar(message: string) {
    this.snackBar.open(message, 'Close', {
      duration: 3000, 
      horizontalPosition: 'start', 
      verticalPosition: 'bottom' 
    });
  }

}