import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors, withInterceptorsFromDi } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { provideAlertService, provideAuthService, provideLoaderState, provideMainState, provideRouterState } from '@usnistgov/ngx-dam-framework';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { BlockUIModule } from 'ng-block-ui';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { AUTHENTICATION_CONFIGURATION } from './authentication.configuration';


export const appConfig: ApplicationConfig = {
  providers: [
    // Angular Providers
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    importProvidersFrom(BrowserAnimationsModule),
    // NgRx Providers
    importProvidersFrom(StoreModule.forRoot()),
    importProvidersFrom(EffectsModule.forRoot()),
    importProvidersFrom(StoreDevtoolsModule.instrument({
      maxAge: 25,
    })),
    // Block UI Providers
    importProvidersFrom(BlockUIModule.forRoot()),
    // DAM Loader
    provideLoaderState(),
    // DAM Alerts
    ...provideAlertService(),
    // DAM Authentication
    provideAuthService({
      urls: AUTHENTICATION_CONFIGURATION
    }),
    // DAM Router
    ...provideRouterState(),
    // DAM Main State
    provideMainState(),
  ]
};
