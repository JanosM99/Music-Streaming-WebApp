import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PlaylistDto } from '../models/PlaylistDto.model';
import { PagedResponseDto } from '../models/PagedResponseDTO.model';


@Injectable({
    providedIn: 'root'
})

export class PlaylistService {

    constructor(
        private http: HttpClient,
    ) {}
    
    private apiUrl = 'http://localhost:8080/api/playlists';
    private allPlaylistsUrl = '/get-all';
    private createPlaylistUrl = '/create';
    private deletePlaylistUrl = '/delete/';
    private addAndRemoveSongToPlaylistUrl = '/song/';


    getPlaylistsByUserId(userId: number): Observable<PlaylistDto[]> {
        return this.http.get<PlaylistDto[]>(this.apiUrl + '/user/' + userId);
    }

    getPlaylistById(id: number): Observable<PlaylistDto> {
        return this.http.get<PlaylistDto>(this.apiUrl + '/' + id);
    }

    getPlaylistsWithFilterSort(pageIndex: number, filter: string, sort: string): Observable<PagedResponseDto<PlaylistDto>> {
        let params = new HttpParams()
              .set('page', pageIndex)
              .set('pageSize', 5)
              .set('sort', sort);
        if (filter) {
          params = params.set('filter', filter);
        }
        
        return this.http.get<PagedResponseDto<PlaylistDto>>(this.apiUrl + this.allPlaylistsUrl, { params });
    }

    createPlaylist(playlist: PlaylistDto): Observable<PlaylistDto> {
        return this.http.post<PlaylistDto>(this.apiUrl + this.createPlaylistUrl, playlist);
    }

    deletePlaylist (id: number) {
        return this.http.delete(this.apiUrl + this.deletePlaylistUrl + id)
    }

    addSongToPlaylist(playlistId: number, songId: number): Observable<PlaylistDto> {
        return this.http.post<PlaylistDto>(this.apiUrl + '/' + playlistId +  this.addAndRemoveSongToPlaylistUrl + songId, {});
    }

    removeSongFromPlaylist(playlistId: number, songId: number): Observable<void> {
        return this.http.delete<void>(this.apiUrl + '/' + playlistId +  this.addAndRemoveSongToPlaylistUrl + songId);
    }
    
}
