import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../../shared/services/User.service';
import { UserDetails } from '../../shared/models/UserDetails.model';
import { UserDto } from '../../shared/models/UserDto.model';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';


@Component({
  selector: 'app-user-details',
  templateUrl: './user-management.component.html'
})
export class UserManagementComponent implements OnInit, AfterViewInit {
  
  length: number | undefined;
  pageIndex = 0; 
  pageEvent: PageEvent | undefined;
  filter = '';
  sortField = 'username';
  sortDirection = 'asc';
  sort = 'ascending';

  pageSizeOptions: number[] | undefined;

  displayedUsersColumns: string[] = ['username', 'active', 'email', 'role', 'birthDate', 'country', 'actions'];
  
  userId: number | undefined;
  userDetails: UserDto | undefined = {} as UserDto;
  users: UserDetails[] | undefined;
  availableUsers: MatTableDataSource<UserDetails> = new MatTableDataSource<UserDetails>([]);
  filteredUsers: UserDto[] = [];
  active: boolean = true;

  @ViewChild(MatSort) usersSort: MatSort | undefined;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private userService: UserService,
    public snackBar: MatSnackBar,
    public router: Router,
  ) { }
  
  ngAfterViewInit(): void {
    this.availableUsers.paginator = this.paginator;
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  async loadUsers() {
    try {
      const pageIndex =this.pageIndex;
      const active = this.active;
      const sort = this.sortField + "," + this.sortDirection;
      const filter = this.filter;
      
      await this.userService.getUsersWithFilterSort(pageIndex, active, filter, sort).subscribe(data => {
        this.users = data.content;
        this.length = data.totalItems;
        this.availableUsers = new MatTableDataSource(this.users);
      });
    } catch (error) {
      console.error('Error loading users:', error);
    }
  }

  deleteUser(userId: number) {
    if (confirm('Are you sure you want to delete this user?')) {
      this.userService.deleteUser(userId).subscribe({
        next: () => {
          this.openSnackBar('User deleted successfully');
          this.loadUsers(); // Reload the list after deletion
        },
        error: (error) => {
          console.error('Error deleting user:', error);
          this.openSnackBar('Failed to delete user');
        }
      });
    }
  }
  
  changeActivation(user: UserDetails) {
    if (user.active) {
      user.active = false;
    } else {
      user.active = true;
    }
    this.userService.updateUser(user).subscribe({
      next: () => {
        this.openSnackBar('User updated successfully');
        this.loadUsers(); 
      },
      error: (error) => {
        console.error('Error updating user:', error);
        this.openSnackBar('Failed to inactivate user');
      }
    });
  }

  sortData(sort: any) {
    this.sortField = sort.active;
    if (sort.direction.length === 0 && this.sortDirection === 'asc') {
      sort.direction = 'desc';
    } else if (sort.direction.length === 0 && this.sortDirection === 'desc') {
      sort.direction = 'asc';
    } 
    this.sortDirection = sort.direction;
    this.loadUsers();
  }

  //Filter active/inactive
  updateFilteredUsers() {
    this.loadUsers();
  }

  applyFilter() {
    this.pageIndex = 0;
    this.paginator.pageIndex = 0;
    this.loadUsers();
  }

  handlePageEvent(event: PageEvent) {
    this.pageEvent = event;
    this.length = event.length;
    this.pageIndex = event.pageIndex;
    this.loadUsers();
  }

  openSnackBar(message: string) {
    this.snackBar.open(message, 'Close', {
      duration: 3000, 
      horizontalPosition: 'start', 
      verticalPosition: 'bottom' 
    });
  }

}