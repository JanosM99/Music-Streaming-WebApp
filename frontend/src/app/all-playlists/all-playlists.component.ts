import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { TokenService } from '../../app/login/token.service'
import { LoginService } from '../login/login.service';
import { PlaylistService } from '../../shared/services/Playlist.service';
import { PlaylistDto } from '../../shared/models/PlaylistDto.model';

@Component({
  selector: 'app-all-playlists',
  templateUrl: './all-playlists.component.html'
})
export class AllPlaylistsComponent implements OnInit, AfterViewInit {

  length: number | undefined;
  pageIndex = 0; 
  pageEvent: PageEvent | undefined;
  filter = '';
  sortField = 'name';
  sortDirection = 'asc';
  sort = 'ascending';

  pageSizeOptions: number[] | undefined;

  userId: number | undefined;
  playlists: PlaylistDto[] = [];
  availablePlaylists: MatTableDataSource<PlaylistDto> = new MatTableDataSource<PlaylistDto>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    public tokenService: TokenService,
    public snackBar: MatSnackBar,
    public router: Router,
    public loginService: LoginService,
    public playlistService: PlaylistService,
  ) { }
  
  ngAfterViewInit(): void {
    this.availablePlaylists.paginator = this.paginator;
  }

  ngOnInit(): void {
    this.userId = Number(this.tokenService.getUserId())
    this.loadPlaylists();
    localStorage.setItem('returnUrl', "/all-playlists");
  }

  async loadPlaylists() {
    try {
        const pageIndex = this.pageIndex;
        const sort = this.sortField + "," + this.sortDirection;
        const filter = this.filter;
        
        await this.playlistService.getPlaylistsWithFilterSort(pageIndex, filter, sort).subscribe(data => {
            this.playlists = data.content;
            this.length = data.totalItems;
            this.availablePlaylists = new MatTableDataSource(this.playlists);
        });
    } catch (error) {
        console.error('Error loading playlists:', error);
    }
  }

  deletePlaylist(playlistId: number) {
    if (confirm('Are you sure you want to delete this playlist?')) {
      this.playlistService.deletePlaylist(playlistId).subscribe({
        next: () => {
          this.openSnackBar('Playlist deleted successfully');
          this.loadPlaylists();
        },
        error: (error) => {
          console.error('Error deleting playlist:', error);
          this.openSnackBar('Failed to delete playlist');
        }
      });
    }
  }
  
  sortData(sort: any) {
    this.sortField = sort.active;
    if (sort.direction.length === 0 && this.sortDirection === 'asc') {
      sort.direction = 'desc';
    } else if (sort.direction.length === 0 && this.sortDirection === 'desc') {
      sort.direction = 'asc';
    } 
    this.sortDirection = sort.direction;
    this.loadPlaylists();
  }

  //Filter active/inactive
  updateFilteredSongs() {
    this.loadPlaylists();
  }

  applyFilter() {
    this.pageIndex = 0;
    this.paginator.pageIndex = 0;
    this.loadPlaylists();
  }

  handlePageEvent(event: PageEvent) {
    this.pageEvent = event;
    this.length = event.length;
    this.pageIndex = event.pageIndex;
    this.loadPlaylists();
  }

  navigateToPlaylist(playlistId: number): void {
    this.router.navigate(['/playlist', playlistId]); 
  }

  openSnackBar(message: string) {
    this.snackBar.open(message, 'Close', {
      duration: 3000, 
      horizontalPosition: 'start', 
      verticalPosition: 'bottom' 
    });
  }

}
