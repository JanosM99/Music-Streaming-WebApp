import { Component, OnInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FileService } from '../../shared/services/File.service';
import { HttpEvent, HttpEventType, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, NavigationStart, Router } from '@angular/router';
import { SongDto } from '../../shared/models/SongDto.model';
import { SongService } from '../../shared/services/Song.service';
import { LoginService } from '../login/login.service';
import { PlaylistService } from '../../shared/services/Playlist.service';
import { TokenService } from '../login/token.service';
import { Subscription } from 'rxjs';


@Component({
  selector: 'app-playlist-player',
  templateUrl: './playlist-player.component.html',
})
export class PlaylistPlayerComponent implements OnInit, OnDestroy {
  // Song
  fileUrl: string = '';
  songId: number | undefined;
  songIdParam: string | null | undefined;
  songDetails: SongDto | undefined;

  // Audio
  audio: HTMLAudioElement | undefined;  
  isPlaying: boolean = false;   
  currentTime: number = 0;     
  duration: number = 0;   

  // Buttons
  isRepeating: boolean = false;
  isShuffled: boolean = false;

  // Playlist
  playlistId: number | undefined;
  playlistName: string | undefined;
  playlistUserId: number | undefined;
  playlistSongs: SongDto[] = [];
  currentSongIndex: number = 0;

  // Loading state
  isLoading: boolean = false;

  @ViewChild('progressBar', { static: false }) progressBar!: ElementRef;
  private routerSubscription!: Subscription;

  constructor(
    private fileService: FileService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private songService: SongService,
    private router: Router,
    public loginService: LoginService,
    private playlistService: PlaylistService,
    public tokenService: TokenService
  ) {}

  ngOnInit(): void {
    // Stop music on route change
    this.routerSubscription = this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.stopMusic();
      }
    });

    const playlistId = +this.route.snapshot.paramMap.get('id')!;
  
    this.playlistService.getPlaylistById(playlistId).subscribe(playlist => {
      this.playlistId = playlist.id;
      this.playlistName = playlist.name;
      this.playlistUserId = playlist.userId;
      this.playlistSongs = playlist.songs;
      this.playSongFromPlaylist(this.currentSongIndex);
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

  playSongFromPlaylist(index: number): void {
    this.currentSongIndex = index;
    this.songId = this.playlistSongs[this.currentSongIndex].id;
    this.loadSongDetailsById();
  }

  // Load song details
  async loadSongDetailsById(): Promise<void> {
    if (this.songId) {
      return new Promise((resolve, reject) => {
        this.songService.getSongDetailsById(this.songId!).subscribe(
          (data: SongDto) => {
            this.songDetails = data;
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
              this.setupAudio();
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
    this.isLoading = true;

    if (this.audio) {
      this.audio.pause();
    }

    this.audio = new Audio(this.fileUrl);

    // Load metadata and duration
    this.audio.addEventListener('loadedmetadata', () => {
      this.duration = this.audio!.duration; // Total duration
      this.isLoading = false; // Mark loading as done
      if (this.isPlaying) {
        this.playAudio();
      }
    });

    // Track current time and update progress bar
    this.audio.addEventListener('timeupdate', () => {
      this.currentTime = this.audio!.currentTime; // Current time
      this.updateProgressBar(); // Update progress bar
    });

    // Play the next song when the current one ends
    this.audio.addEventListener('ended', () => {
      if (this.isRepeating) {
        this.audio!.currentTime = 0;
        this.audio!.play().catch(error => {
          console.error('Error attempting to play the audio: ', error);
        });
      } else {
         // Check if this is the last song in the playlist
         if (this.currentSongIndex < this.playlistSongs.length - 1) {
          this.playNextSong(); // Load and play the next song
      } else {
          this.isPlaying = false; // Stop playback if its the last song
          this.audio!.pause();
        }
      }
    });
  }

  // Play the audio
  private playAudio(): void {
    this.audio!.currentTime = 0;
    this.audio!.play().catch(error => {
      console.error('Error attempting to play the audio: ', error);
    });
  }

  // Play next song
  playNextSong(): void {
    if (this.isShuffled) {
      // Pick a random song
      let randomIndex;
      do {
        randomIndex = Math.floor(Math.random() * this.playlistSongs.length);
      } while (randomIndex === this.currentSongIndex); // Ensure we don't play the same song
      this.playSongFromPlaylist(randomIndex);
    } else {
      // If not shuffled, play the next song in the list
      if (this.currentSongIndex < this.playlistSongs.length - 1) {
        this.playSongFromPlaylist(this.currentSongIndex + 1);
      } else {
        this.isPlaying = false; // Stop playback if it’s the last song
      }
    }
  }

  // Play previous song
  playPreviousSong(): void {
    if (this.currentSongIndex > 0) {
      this.playSongFromPlaylist(this.currentSongIndex - 1);
    }
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

  // Toggle shuffle mode
  toggleShuffle(): void {
    this.isShuffled = !this.isShuffled;
    const message = this.isShuffled ? 'Shuffle mode enabled' : 'Shuffle mode disabled';
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

  removeSong(playlistId: number, songId: number) {
    this.playlistService.removeSongFromPlaylist(playlistId,songId).subscribe({
      next: () => {
        this.openSnackBar('Song removed successfully');
        location.reload();
      },
      error: (error) => {
        console.error('Error removing song:', error);
        this.openSnackBar('Failed to remove song');
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
