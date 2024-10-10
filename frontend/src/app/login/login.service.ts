import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, firstValueFrom, map } from 'rxjs';
import { TokenService, get } from './token.service';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserDto } from '../../shared/models/UserDto.model';
import { CountryDto } from '../../shared/models/CountryDto.model';

@Injectable({
    providedIn: 'root'
  })

/**
 * A class for managing user logins.
*/

export class LoginService {
  constructor(
    private http: HttpClient,
    private tokenService: TokenService,
    private router: Router,
    private jwtHelper: JwtHelperService
  ) { }

  apiUrl = 'http://localhost:8080';
  loginURL = '/api/users';


  /**
   * Sends the authentication details to the server and saves the returned token in local storage
  */
  authenticateUser(username: string, password: string): Observable<void> {
    const body = { username, password };
  
    return this.http.post<{ token: string }>(this.apiUrl + '/auth/login', body).pipe(
      map(response => {
        const token = response.token;
        this.saveToken(token);
      })
    );
  }

  /**
   * Saves a token to local storage
  */
  private saveToken(token:string) {
    if(!token) {
      throw new Error('Setting an empty token is not possible');
    }
    this.tokenService.set(token);
  }

  /**
   * Returns the token.
  */
  public getToken(): string | null {
    return get();
  }
  
  /**
   * Removes the token from local storage
  */
  public removeToken() {
    this.tokenService.remove();
  }

  /**
   * Checks if the user is logged in 
   * If the users authentication ran out this timed check will kill itself and log the user out
  */
  public checkToken(interval: number): void {
    if (!( (this.router.url === "/login") || this.router.url === "/login?returnUrl=%2F") && !this.isAuthenticated() ) {
      clearInterval(interval);
      this.logout();
    }
  }

  /**
   * Removes the token from local storage and returns the user to the login screen
  */
  logout() {
    this.removeToken();
  }

  /**
   * Checks if the user is authenticated
  */
  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) {
        return false;
    }
    const tokenExpired = this.isTokenExpired(token);
    if (tokenExpired) {
        return false;
    }
    return true;
  }

  /**
   * Checks if the token is expired
  */
  private isTokenExpired(token: string): boolean {
    try {
        return this.jwtHelper.isTokenExpired(token, 10);
    } catch (e) {
        console.error(e)
        return false;
    }
  }

  /**
   * Creates a new user with the provided username and password
  */
  createUser(username: string, password: string, email: string, role: string, birthDate: Date, country: string, followerCount: number, active: boolean): Promise<any> {
    const userDto: UserDto = { username, password, email, role, birthDate, country, followerCount, active };
    const url = this.apiUrl + this.loginURL + '/create';

    try {
      return firstValueFrom(this.http.post(url, userDto));
    } catch (error) {
      throw new Error('Error creating user: ' + error);
    }
  }
  
}  