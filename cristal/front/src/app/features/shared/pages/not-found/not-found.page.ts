import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found-page',
  standalone: true,
  imports: [RouterLink],
  template: `
    <section class="panel">
      <p>La page demandee n'existe pas.</p>
      <a routerLink="/games">Retour au catalogue</a>
    </section>
  `,
  styles: [`
    .panel {
      border: 1px solid var(--border);
      background: var(--surface);
      box-shadow: var(--shadow);
      border-radius: 24px;
      padding: 24px;
      display: grid;
      gap: 12px;
      justify-items: start;
    }

    a {
      padding: 10px 14px;
      border-radius: 12px;
      background: var(--accent);
      color: white;
    }
  `],
})
export class NotFoundPage {}
