import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LoginService } from './login.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {

  constructor(
    private router: Router,
    private snackBar: MatSnackBar,
    private loginService: LoginService,
    private formBuilder: FormBuilder,
  ) { }

  loginForm: FormGroup = new FormGroup({
    username: new FormControl('', {nonNullable: true}),
    password: new FormControl('', {nonNullable: true}),
  });

  authenticating: boolean = false;

  protected intervalRef: any;
  protected intervalTimer: any;


  async ngOnInit(): Promise<void> {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.pattern('([a-z]){2}\\d?')]],
      password: ['', Validators.required]
    })

    // 1 MIN
    this.intervalTimer = 60 * 1000;
  }

  onSubmit() {
    const username = this.loginForm.get('username')?.value;
    const password = this.loginForm.get('password')?.value;

    this.loginService.authenticateUser(username, password)
    .subscribe({
      next: (result) => {
        this.intervalRef = setInterval(()=> this.loginService.checkToken(this.intervalRef), this.intervalTimer);
        this.navigate();
      },
      error: (errorCode) => {
        this.openSnackBar("Authorization failed."),
        () => this.authenticating = false
      },
      complete: () => {
        this.openSnackBar("You are successfully logged in!");
      }
    })            
  }

  // Navigate to REGISTER
  navigateToSignUp() {
    this.router.navigateByUrl('/signup');
  }
  
  // Navigate to HOME
  navigate() {
    this.router.navigateByUrl('');
  }

  openSnackBar(message: string) {
    // Display a snackbar with the given message
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'start',
      verticalPosition: 'bottom'
    });
  }

}
