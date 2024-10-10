import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SongDto } from '../models/SongDto.model';
import { Observable } from 'rxjs';


@Injectable({
    providedIn: 'root'
})

export class SongService {

    constructor(
        private http: HttpClient,
    ) {}
    
    private apiUrl = 'http://localhost:8080/api/songs';
    private fileUrl = 'http://localhost:8080/api/files/songs';


    getSongDetailsById(songId: number): Observable<SongDto> {
        return this.http.get<SongDto>(this.apiUrl + '/' + songId);
    }

    uploadSong(file: File, songDto: SongDto): Observable<string> {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('name', songDto.name);
        formData.append('artist', songDto.artist);
        formData.append('genre', songDto.genre);

        return this.http.post<string>(this.fileUrl, formData, { responseType: 'text' as 'json' });
    }

}
