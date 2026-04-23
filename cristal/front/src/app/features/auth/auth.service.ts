import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import { jwtDecode } from 'jwt-decode';
import {BehaviorSubject} from "rxjs";

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = `${this.resolveBaseUrl()}/auth`;
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
      if (decoded.exp && decoded.exp * 1000 < Date.now()) {
        throw new Error('Token expired');
      }
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

  private resolveBaseUrl(): string {
    if (typeof window === 'undefined') {
      return 'http://localhost:8080';
    }

    return `${window.location.protocol}//${window.location.hostname}:8080`;
  }
}