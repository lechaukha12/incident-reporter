import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-simple-test',
  template: `
    <div style="padding: 20px;">
      <h1>Simple API Test Component</h1>
      <div>
        <button (click)="testStats()">Test Stats API</button>
        <button (click)="testIncidents()">Test Incidents API</button>
        <button (click)="clearResults()">Clear</button>
      </div>
      <pre style="margin-top: 20px; padding: 15px; background-color: #f5f5f5; border-radius: 4px;">{{ results }}</pre>
    </div>
  `,
  styles: [`
    button {
      background-color: #4CAF50;
      color: white;
      border: none;
      padding: 10px 15px;
      cursor: pointer;
      margin-right: 10px;
      border-radius: 4px;
    }
  `]
})
export class SimpleTestComponent implements OnInit {
  results: string = 'Results will appear here...';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.results = 'Component initialized. API URL: ' + environment.apiUrl;
  }

  testStats(): void {
    this.results = 'Loading stats...';
    this.http.get(`${environment.apiUrl}/incidents/stats`).subscribe({
      next: (data) => {
        this.results = JSON.stringify(data, null, 2);
      },
      error: (error) => {
        this.results = 'Error: ' + (error.message || JSON.stringify(error));
        console.error('API Error:', error);
      }
    });
  }

  testIncidents(): void {
    this.results = 'Loading incidents...';
    this.http.get(`${environment.apiUrl}/incidents`).subscribe({
      next: (data) => {
        this.results = JSON.stringify(data, null, 2);
      },
      error: (error) => {
        this.results = 'Error: ' + (error.message || JSON.stringify(error));
        console.error('API Error:', error);
      }
    });
  }

  clearResults(): void {
    this.results = 'Results cleared...';
  }
}
