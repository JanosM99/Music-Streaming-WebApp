import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { ReactiveFormsModule } from '@angular/forms';
import { HomeComponent } from './home/home.component'; 
import { JwtModule } from '@auth0/angular-jwt';
import { get as getToken } from './login/token.service';
import { RegisterComponent } from './register/register.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { NavbarComponent } from './navbar/navbar.component';
import { MatIconModule } from '@angular/material/icon';
import { UserDetailsComponent } from './user-details/user-details.component';
import { UserManagementComponent } from './user-management/user-management.component';
import { AllSongsComponent } from './all-songs/all-songs.component';
import {MatMenuModule} from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import {MatSelectModule} from '@angular/material/select';
import { MusicPlayerComponent } from './music-player/music-player.component';
import { UploadSongDialogComponent } from './upload-song-dialog/upload-song-dialog.component';
import {MatTabsModule} from '@angular/material/tabs';
import { AllPlaylistsComponent } from './all-playlists/all-playlists.component';
import { PlaylistPlayerComponent } from './playlist-player/playlist-player.component';
import { MatDialogModule } from '@angular/material/dialog';
import { CreatePlaylistDialogComponent } from './create-playlist-dialog/create-playlist-dialog.component';
import { AddToPlaylistDialogComponent } from './add-to-playlist-dialog/add-to-playlist-dialog.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    RegisterComponent,
    NavbarComponent,
    UserDetailsComponent,
    UserManagementComponent,
    AllSongsComponent,
    MusicPlayerComponent,
    UploadSongDialogComponent,
    AllPlaylistsComponent,
    PlaylistPlayerComponent,
    AddToPlaylistDialogComponent,
    CreatePlaylistDialogComponent,
  ],
  imports: [
    MatDialogModule,
    BrowserModule,
    MatTableModule,
    ReactiveFormsModule,
    AppRoutingModule,
    HttpClientModule,
    MatPaginatorModule,
    MatSlideToggleModule,
    MatSelectModule,
    MatTabsModule,
    FormsModule,
    BrowserAnimationsModule,
    MatFormFieldModule,
    MatMenuModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatDatepickerModule,        
    MatNativeDateModule,
    MatCardModule,
    JwtModule.forRoot({
      config: {
        tokenGetter: getToken,
        allowedDomains: ['localhost:8080', 'localhost:4200'],
        skipWhenExpired: true
      }
    }),
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
