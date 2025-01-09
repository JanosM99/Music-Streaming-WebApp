import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SongDto } from '../models/SongDto.model';
import { Observable } from 'rxjs';
import { PagedResponseDto } from '../models/PagedResponseDTO.model';


@Injectable({
    providedIn: 'root'
})

export class SongService {

    constructor(
        private http: HttpClient,
    ) {}
    
    private apiUrl = 'http://localhost:8080/api/songs';
    private fileUrl = 'http://localhost:8080/api/files/songs';
    
    private allSongsUrl = '/get-all';
    private deleteSongUrl = '/delete/';
    private likeSongUrl = '/like/';


    getSongDetailsById(songId: number): Observable<SongDto> {
        return this.http.get<SongDto>(this.apiUrl + '/' + songId);
    }

    getSongsByUserId(userId: number): Observable<SongDto[]> {
        return this.http.get<SongDto[]>(this.apiUrl + '/user/' + userId);
    }

    uploadSong(file: File, songDto: SongDto): Observable<string> {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('name', songDto.name);
        formData.append('artist', songDto.artist);
        formData.append('genre', songDto.genre);

        return this.http.post<string>(this.fileUrl, formData, { responseType: 'text' as 'json' });
    }

    getSongsWithFilterSort(pageIndex: number, filter: string, sort: string): Observable<PagedResponseDto<SongDto>> {
        let params = new HttpParams()
              .set('page', pageIndex)
              .set('pageSize', 5)
              .set('sort', sort);
        if (filter) {
          params = params.set('filter', filter);
        }
        
        return this.http.get<PagedResponseDto<SongDto>>(this.apiUrl + this.allSongsUrl, { params });
    }

    deleteSong (id: number) {
        return this.http.delete(this.apiUrl + this.deleteSongUrl + id)
    }

    likeSong (id: number) {
        return this.http.post(this.apiUrl + this.likeSongUrl + id, 1)
    }

}
