import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FileService } from '../../shared/services/File.service';
import { HttpEvent, HttpEventType, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { SongDto } from '../../shared/models/SongDto.model';
import { SongService } from '../../shared/services/Song.service';


@Component({
  selector: 'app-music-player',
  templateUrl: './music-player.component.html',
})
export class MusicPlayerComponent implements OnInit {
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
  isLiked: boolean = false;

  @ViewChild('progressBar', { static: false }) progressBar!: ElementRef;

  constructor(
    private fileService: FileService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private songService: SongService,
  ) {}

  ngOnInit(): void {
    // Get the ID from the URL
    this.route.paramMap.subscribe(async (params: ParamMap) => {
      this.songIdParam = params.get('id');
      if (this.songIdParam) {
        // Load song details by ID
        this.songId = +this.songIdParam;
        this.loadSongDetailsById();
      }
    });
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

  openSnackBar(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'start',
      verticalPosition: 'bottom',
    });
  }
  
}
