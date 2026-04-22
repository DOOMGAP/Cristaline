import { Component, inject } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { AuthComponent } from '../auth/auth.component';
import { AuthService } from '../auth/auth.service';
import {AsyncPipe, CommonModule} from "@angular/common";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, AsyncPipe],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  private dialog = inject(MatDialog);
  public authService = inject(AuthService);
  openAuth() {
    this.dialog.open(AuthComponent, {
      width: '450px',
      enterAnimationDuration: '300ms',
      exitAnimationDuration: '200ms',
    });
  }

  protected readonly AuthService = AuthService;
}