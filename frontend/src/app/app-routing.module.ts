import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { RegisterComponent } from './register/register.component';
import { AuthGuard } from '../shared/services/Auth.guard';
import { UserDetailsComponent } from './user-details/user-details.component';
import { UserManagementComponent } from './user-management/user-management.component';
import { AllSongsComponent } from './all-songs/all-songs.component';
import { MusicPlayerComponent } from './music-player/music-player.component';
import { AllPlaylistsComponent } from './all-playlists/all-playlists.component';
import { PlaylistPlayerComponent } from './playlist-player/playlist-player.component';


const routes: Routes = [
  { path: 'login', 
    component: LoginComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'signup',
    component: RegisterComponent,
    canActivate: [AuthGuard]
  },
  {
    path: '',
    component: HomeComponent,
  },
  {
    path: 'me',
    component: UserDetailsComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'user/:id',
    component: UserDetailsComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'user-management',
    component: UserManagementComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'all-songs',
    component: AllSongsComponent
  },
  {
    path: 'track/:id',
    component: MusicPlayerComponent
  },
  {
    path: 'all-playlists',
    component: AllPlaylistsComponent
  },
  {
    path: 'playlist/:id',
    component: PlaylistPlayerComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
