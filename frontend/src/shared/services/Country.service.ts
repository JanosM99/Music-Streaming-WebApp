import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CountryDto } from '../models/CountryDto.model';


@Injectable({
    providedIn: 'root'
})

export class CountryService {

    constructor(
        private http: HttpClient,
    ) {}
    
    private apiUrl = 'http://localhost:8080/api/country';
    private getAllUrl = '/all';


    getAllCountries(): Observable<CountryDto[]> {
        return this.http.get<CountryDto[]>(this.apiUrl + this.getAllUrl);
    }

}