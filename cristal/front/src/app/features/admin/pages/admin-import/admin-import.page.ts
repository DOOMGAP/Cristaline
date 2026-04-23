import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AdminImportApi } from '../../data/admin-import.api';

@Component({
  selector: 'app-admin-import-page',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="panel">
      <p class="eyebrow">Administration</p>
      <h1>Import FreeToGame</h1>
      <p class="lead">
        Cette action publie une demande d'import. En profil prod, Kafka orchestre
        l'execution asynchrone cote API.
      </p>

      <button
        type="button"
        class="trigger"
        (click)="triggerImport()"
        [disabled]="isSubmitting"
        data-testid="admin-import-trigger"
      >
        {{ isSubmitting ? 'Envoi...' : 'Lancer l import' }}
      </button>

      <p *ngIf="message" class="message" data-testid="admin-import-message">{{ message }}</p>
    </section>
  `,
  styles: [`
    .panel {
      display: grid;
      gap: 16px;
      padding: 24px;
      border: 1px solid var(--border);
      background: var(--surface);
      box-shadow: var(--shadow);
      border-radius: 24px;
    }

    .eyebrow {
      margin: 0;
      color: var(--accent);
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.08em;
    }

    h1, .lead, .message {
      margin: 0;
    }

    .lead {
      color: var(--muted);
    }

    .trigger {
      justify-self: start;
      border: none;
      border-radius: 14px;
      padding: 12px 18px;
      background: var(--accent);
      color: white;
      cursor: pointer;
    }

    .trigger:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .message {
      font-weight: 600;
      color: var(--accent-dark);
    }
  `],
})
export class AdminImportPage {
  private readonly adminImportApi = inject(AdminImportApi);

  isSubmitting = false;
  message = '';

  triggerImport(): void {
    this.message = '';
    this.isSubmitting = true;

    this.adminImportApi.triggerFreeToGameImport().subscribe({
      next: () => {
        this.message = "Demande d'import envoyee.";
        this.isSubmitting = false;
      },
      error: () => {
        this.message = "Impossible de lancer l'import.";
        this.isSubmitting = false;
      },
    });
  }
}
