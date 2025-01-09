import { Component, OnInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FileService } from '../../shared/services/File.service';
import { HttpEvent, HttpEventType, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, NavigationStart, ParamMap, Router } from '@angular/router';
import { SongDto } from '../../shared/models/SongDto.model';
import { SongService } from '../../shared/services/Song.service';
import { LoginService } from '../login/login.service';
import { AddToPlaylistDialogComponent } from '../add-to-playlist-dialog/add-to-playlist-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { PlaylistService } from '../../shared/services/Playlist.service';
import { PlaylistDto } from '../../shared/models/PlaylistDto.model';
import { TokenService } from '../login/token.service';
import { Subscription } from 'rxjs';


@Component({
  selector: 'app-music-player',
  templateUrl: './music-player.component.html',
})
export class MusicPlayerComponent implements OnInit, OnDestroy {
  // Song
  fileUrl: string = '';
  songId: number | undefined;
  songIdParam: string | null | undefined;
  songDetails: SongDto | undefined;
  playlists: PlaylistDto[] = [];
  
  // Audio
  audio: HTMLAudioElement | undefined;  
  isPlaying: boolean = false;   
  currentTime: number = 0;     
  duration: number = 0;   

  // Buttons
  isRepeating: boolean = false;
  isLiked: boolean = false;

  @ViewChild('progressBar', { static: false }) progressBar!: ElementRef;
  private routerSubscription!: Subscription;

  constructor(
    private fileService: FileService,
    private playlistService: PlaylistService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private songService: SongService,
    private router: Router,
    public loginService: LoginService,
    public dialog: MatDialog,
    public tokenService: TokenService,
  ) {}

  ngOnInit(): void {
    // Stop music on route change
    this.routerSubscription = this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.stopMusic();
      }
    });

    // Get the ID from the URL
    this.route.paramMap.subscribe(async (params: ParamMap) => {
      this.songIdParam = params.get('id');
      if (this.songIdParam) {
        // Load song details by ID
        this.songId = +this.songIdParam;
        this.loadSongDetailsById();
        this.loadPlaylists();
      }
    });
  }

  // Stop music playback
  stopMusic(): void {
    if (this.audio) {
      this.audio.pause();
      this.audio.currentTime = 0;
      this.isPlaying = false;
    }
  }

  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  // Load song details
  async loadSongDetailsById(): Promise<void> {
    if (this.songId) {
      return new Promise((resolve, reject) => {
        this.songService.getSongDetailsById(this.songId!).subscribe(
          (data: SongDto) => {
            this.songDetails = data;
            this.songId = data.id;
            this.loadSong();
            resolve();
          },
          error => {
            this.openSnackBar('Error loading the song.');
            reject(error);
          }
        );
      });
    }
  }

  // Load the song
  loadSong(): void {
    if (this.songId) {
      this.fileService.downloadSong(this.songId).subscribe(
        (event: HttpEvent<Blob>) => {
          if (event.type === HttpEventType.Response) {
            const httpResponse = event as HttpResponse<Blob>;

            if (httpResponse.status === 200 && httpResponse.body) {
              const urlCreator = window.URL || window.webkitURL;
              this.fileUrl = urlCreator.createObjectURL(httpResponse.body);
              this.setupAudio(); // Set up audio after loading
            } else {
              this.fileUrl = '';
            }
          }
        },
        error => {
          this.fileUrl = '';
        }
      );
    }
  }

  // Setup audio
  setupAudio(): void {
    this.audio = new Audio(this.fileUrl);
    this.audio.load();

    // Load metadata and duration
    this.audio.addEventListener('loadedmetadata', () => {
      this.duration = this.audio?.duration || 0; // Total duration
    });

    // Track current time and update progress bar
    this.audio.addEventListener('timeupdate', () => {
      this.currentTime = this.audio?.currentTime || 0; // Current time
      this.updateProgressBar();
    });

    // Handle end of audio playback
    this.audio.addEventListener('ended', () => {
      if (this.isRepeating && this.audio) {
        this.audio.currentTime = 0; // Reset to the start of the song
        this.audio.play(); // Play again
      }
    });
  }

  // Start/stop playback
  togglePlayback(): void {
    if (this.audio) {
      if (this.isPlaying) {
        this.audio.pause();
      } else {
        this.audio.play();
      }
      this.isPlaying = !this.isPlaying;
    }
  }

  // Toggle repeat mode
  toggleRepeat(): void {
    this.isRepeating = !this.isRepeating;
    const message = this.isRepeating ? 'Repeat mode enabled' : 'Repeat mode disabled';
    this.openSnackBar(message);
  }

  // Toggle repeat mode
  toggleLiked(): void {
    this.isLiked = !this.isLiked;
    const message = this.isLiked ? 'Liked' : 'Unliked';
    this.openSnackBar(message);
  }
  
  // Update progress bar
  updateProgressBar(): void {
    if (this.progressBar && this.duration > 0) {
      const percentage = (this.currentTime / this.duration) * 100;
      this.progressBar.nativeElement.style.width = percentage + '%';
    }
  }

  // Method to format seconds to mm:ss
  formatTime(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${minutes}:${secs < 10 ? '0' : ''}${secs}`;
  }

  // Load playlists for the user
  loadPlaylists(): void {
    this.playlistService.getPlaylistsByUserId(this.tokenService.getUserId()!).subscribe(playlists => {
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

  navigateBack() {
    const backTo = localStorage.getItem('returnUrl');
    if(backTo) {
      this.router.navigateByUrl(backTo);
    } else {
      this.router.navigateByUrl('/home')
    }
  }

  openSnackBar(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'start',
      verticalPosition: 'bottom',
    });
  }
  
}
