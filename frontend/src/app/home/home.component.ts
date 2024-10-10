import { Component, OnInit } from '@angular/core';
import { TokenService } from '../login/token.service';
import { LoginService } from '../login/login.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {

  username: any;
  userId: any;
  userPrivileges!: any[] | null;

  constructor(
    private tokenService: TokenService,
    public loginService: LoginService
  ) { }

  ngOnInit(): void {
    this.username = this.tokenService.getUsername();
    this.userId = this.tokenService.getUserId();
    this.userPrivileges = this.tokenService.getUserPrivileges();
  }

}
