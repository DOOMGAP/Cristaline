import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import { jwtDecode } from 'jwt-decode';
import {BehaviorSubject} from "rxjs";

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/auth';
  private currentUserSubject = new BehaviorSubject<string | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor() {
    const token = localStorage.getItem('token');
    if (token)
    {
      try{
        this.decodeAndSaveUser(token);
      }
      catch(e){
        this.logout();
      }
    }
  }

  decodeAndSaveUser(token: string){
      const decoded: any = jwtDecode(token);
      const username = decoded.sub;
      this.currentUserSubject.next(username);
  }

  logout(){
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
  }

  login(credentials: any) {
    return this.http.post(`${this.apiUrl}/login`, credentials);
  }

  register(userData: any) {
    return this.http.post(`${this.apiUrl}/register`, userData);
  }
}