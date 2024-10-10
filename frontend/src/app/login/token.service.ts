import { JwtHelperService } from '@auth0/angular-jwt';
import { Injectable } from "@angular/core";

export const TOKEN_STORAGE_KEY = 'token';

@Injectable({
    providedIn: 'root'
  })
export class TokenService {
    constructor(private jwtHelper: JwtHelperService) { }

    /**
     * Sets a string as the token, to be sent to the server.
    */
    set(token: string) {
        if (!token) {
            throw new Error('Setting an empty token is not allowed.');
        }
        localStorage.setItem(TOKEN_STORAGE_KEY, token);    
    }

    /**
     * Removes the token from local Storage.
    */
    remove() {
        localStorage.removeItem(TOKEN_STORAGE_KEY);
    }

    /**
     * Gets username from token
     * @returns username
     */
    getUsername(): string | null {
        const token = get();
        if (!token) {
          return null;
        }
        return this.jwtHelper.decodeToken().username as string;
    }

    /**
     * Gets userId from token
     * @returns userId
     */
    getUserId(): number | null {
        const token = get();
        if(!token) {
            return null;
        }
        return this.jwtHelper.decodeToken().sub as number;
    }

    /**
     * Gets privileges from token
     * @returns privileges
     */
    getUserPrivileges(): string[] | null {
        const token = get();
        if (!token) {
          return null;
        }
        return this.jwtHelper.decodeToken().roles as string[];
    }

    isAdmin(): boolean {
        const roles = this.getUserPrivileges();
        if (!roles) {
            return false;
        }
        return roles.includes('ROLE_ADMIN');
    }
  
}

/**
 * Returns the token.
*/
export function get(): string | null {
    return localStorage.getItem(TOKEN_STORAGE_KEY);
}