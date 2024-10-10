import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserDetails } from '../models/UserDetails.model';
import { PasswordChangeDto } from '../models/PasswordChangeDto.model';
import { PagedResponseDto } from '../models/PagedResponseDTO.model';


@Injectable({
    providedIn: 'root'
})

export class UserService {

    constructor(
        private http: HttpClient,
    ) {}
    
    private apiUrl = 'http://localhost:8080/api/users';
    private getUserByNameUrl = '/get-user-by-name/';
    private getUserByIdUrl = '/get/';
    private getMyUserUrl = '/me';
    private changePasswordUrl = '/change-password';
    private allUsersUrl = '/get-all';
    private deleteUserUrl = '/delete/'
    private updateDetailsUrl = '/update-details'


    getUserDetailsByName(username: string): Observable<UserDetails> {
        return this.http.get<UserDetails>(this.apiUrl + this.getUserByNameUrl + username);
    }

    getUserDetailsById(userId: number): Observable<UserDetails> {
        return this.http.get<UserDetails>(this.apiUrl + this.getUserByIdUrl + userId);
    }

    getMyProfile(): Observable<UserDetails> {
        return this.http.get<UserDetails>(this.apiUrl + this.getMyUserUrl);
    }

    changePassword(passWordChangeDto: PasswordChangeDto): Observable<any> {
        return this.http.put<any>(this.apiUrl + this.changePasswordUrl, passWordChangeDto);
    }

    getUsersWithFilterSort(pageIndex: number, active: boolean, filter: string, sort: string): Observable<PagedResponseDto<UserDetails>> {
        let params = new HttpParams()
              .set('page', pageIndex)
              .set('pageSize', 5)
              .set('active', active)
              .set('sort', sort);
        if (filter) {
          params = params.set('filter', filter);
        }
        
        return this.http.get<PagedResponseDto<UserDetails>>(this.apiUrl + this.allUsersUrl, { params });
      }

    deleteUser (id: number) {
        return this.http.delete(this.apiUrl + this.deleteUserUrl + id)
    }

    updateUser(user: UserDetails) {
        return this.http.put(this.apiUrl + this.updateDetailsUrl, user);
    }

}
