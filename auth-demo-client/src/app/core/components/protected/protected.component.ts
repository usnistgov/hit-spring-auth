import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { DataStateValue } from '@usnistgov/ngx-dam-framework';
import { Observable } from 'rxjs';


// Initializer for Entity List
export const DataState = new DataStateValue<{ value: string }>({
  key: 'data', // unique name for this state variable
  routeLoader: (params, injector, state) => {
    const http = injector.get(HttpClient);
    return http.get<{ value: string }>("/api/data");
  }
});

@Component({
  selector: 'app-protected',
  standalone: true,
  imports: [
    CommonModule
  ],
  templateUrl: './protected.component.html',
  styleUrl: './protected.component.scss'
})
export class ProtectedComponent {
  data: Observable<{ value: string }>;
  constructor(private store: Store<any>) {
    this.data = DataState.getValue(store);
  }

}
