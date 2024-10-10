export interface UserDto{
    username: string;
    password: string;
    
    email: string;
    role: string;
    birthDate: Date;
    country: string;
    followerCount: number;
    active: boolean;
}