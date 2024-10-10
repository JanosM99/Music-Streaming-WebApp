import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, delay, map, of } from 'rxjs';
import { LoginService } from '../login/login.service';
import { CountryDto } from '../../shared/models/CountryDto.model';
import { CountryService } from '../../shared/services/Country.service';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent implements OnInit {

  countries: CountryDto[] = [];

  constructor(
    private router: Router,
    private snackBar: MatSnackBar,
    private loginService: LoginService,
    private formBuilder: FormBuilder,
    private countryService: CountryService,
  ) { }

  registerForm: FormGroup = new FormGroup({
    username: new FormControl('', {nonNullable: true}),
    password: new FormControl('', {nonNullable: true}),
    confirmPassword: new FormControl('', {nonNullable: true}),

    email: new FormControl('', {nonNullable: true}),
    birthdate: new FormControl('', {nonNullable: true}),
    country: new FormControl('', {nonNullable: true}),
  });

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required],

      email: ['', [Validators.required, Validators.pattern("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$")], [asyncEmailValidator]],
      birthDate: [ '', Validators.required],
      country: ['', [Validators.required, Validators.pattern(/^[^\d]*$/)]]
    })

    function asyncEmailValidator(control: AbstractControl): Observable<{ [key: string]: any } | null> {
      return of(control.value).pipe(
        delay(2000),
        map(value => {
          const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
          if (!regex.test(value)) {
            return { invalidEmail: true };
          } else {
            return null;
          }
        })
      );
    }

    this.fetchCountries();
  }

  registerUser() {
    if (this.registerForm.valid) {
      const username = this.registerForm.get('username')?.value;
      const password = this.registerForm.get('password')?.value;
      const confirmPassword = this.registerForm.get('confirmPassword')?.value;
      
      const email = this.registerForm.get('email')?.value;
      const birthDate = this.registerForm.get('birthDate')?.value.toISOString().slice(0, 10);
      const country = this.registerForm.get('country')?.value;
      
      if(password === confirmPassword) {
        this.loginService.createUser(
          username, 
          password,
          email,
          "ROLE_USER",
          birthDate,
          country,
          0,
          true
        )
        .then(() => {
          this.openSnackBar('Registration successful!');
          this.navigate();
        })
        .catch((error: { message: string; }) => {
          this.openSnackBar('Registration failed: ' + error.message);
        });
      } else {
        this.openSnackBar('Passwords do NOT match!');
      }
    }
  }

  onKeyDown(event: KeyboardEvent) {
    if (event.key === 'Backspace' || event.key === 'Delete' || event.key === 'Tab' 
    || event.key === 'ArrowLeft' || event.key === 'ArrowRight'
    || (event.ctrlKey && /^[c]$/.test(event.key)) || (event.ctrlKey && /^[v]$/.test(event.key))
    || /^[0-9.]$/.test(event.key) || (event.ctrlKey && /^[a]$/.test(event.key))) {
      return true;
    } else {
      return false;
    }
  }

  // Country
  fetchCountries(): void {
    this.countryService.getAllCountries().subscribe(
      (data: CountryDto[]) => {
        this.countries = data;
      },
      (error) => {
        console.error('Error fetching countries:', error);
      }
    );
  } 

  // Navigate to LOGIN
  navigate() {
    this.router.navigateByUrl('/login');
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
