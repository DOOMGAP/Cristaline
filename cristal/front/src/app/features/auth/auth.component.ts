import {Component, inject, NgModule} from '@angular/core';
import { AuthService } from "./auth.service";
import {MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, ReactiveFormsModule, Validators} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ isLogin ? 'Connexion' : 'Inscription' }}</h2>
    <mat-dialog-content>
      <form [formGroup]="authForm">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Username</mat-label>
          <input matInput formControlName="username">
        </mat-form-field>

        <mat-form-field *ngIf="!isLogin" appearance="outline" class="full-width">
          <mat-label>Email</mat-label>
          <input matInput type="email" formControlName="email">
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Mot de passe</mat-label>
          <input matInput type="password" formControlName="password">
        </mat-form-field>
      </form>
      <a (click)="isLogin = !isLogin" class="toggle-link">
        {{ isLogin ? "Pas de compte ? S'inscrire" : "Déjà inscrit ? Se connecter" }}
      </a>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Annuler</button>
      <button mat-raised-button color="primary" (click)="submit()">Valider</button>
    </mat-dialog-actions>
  `
})
export class AuthComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  private dialogRef = inject(MatDialogRef<AuthComponent>);

  isLogin = true;
  authForm = this.fb.group({
    username: ['', Validators.required],
    email: [''],
    password: ['', Validators.required]
  });

  submit() {
    const action = this.isLogin
        ? this.authService.login(this.authForm.value)
        : this.authService.register(this.authForm.value);

    action.subscribe({
      next: (res: any) => {
        localStorage.setItem('token', res.token);
        this.authService.decodeAndSaveUser(res.token);
        this.dialogRef.close();
      },
      error: (err) => console.error(err)
    });

  }
}