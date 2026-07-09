import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

type ShiftChangeStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

interface Employee {
  employeeID: number;
  name: string;
  email: string;
  managerId: number;
}

interface Shift {
  shiftId: number;
  startTime: string;
  endTime: string;
  status: string;
  employeeId: number;
}

interface ShiftChangeRequest {
  requestId: number;
  status: ShiftChangeStatus;
  updatedBy?: number;
  updateTime?: string;
  changeShiftId1: number;
  changeShiftId2: number;
  employeeId1: number;
  employeeId2: number;
  comment?: string;
  createdAt: string;
}

@Component({
  selector: 'app-root',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private readonly http = inject(HttpClient);
  private readonly fb = inject(FormBuilder);

  protected readonly activeView = signal<'employee' | 'manager'>('employee');
  protected readonly managerTab = signal<'open' | 'history'>('open');
  protected readonly employees = signal<Employee[]>([]);
  protected readonly shifts = signal<Shift[]>([]);
  protected readonly requests = signal<ShiftChangeRequest[]>([]);
  protected readonly resolvedRequests = signal<ShiftChangeRequest[]>([]);
  protected readonly loadingRequests = signal(false);
  protected readonly loadingResolvedRequests = signal(false);
  protected readonly loadingShifts = signal(false);
  protected readonly requestMessage = signal('');
  protected readonly shiftMessage = signal('');
  protected readonly managerMessage = signal('');
  protected readonly setupMessage = signal('');
  protected readonly selectedRequestId = signal<number | null>(null);

  protected readonly managers = computed(() =>
    this.employees().filter((employee) => employee.managerId === -1)
  );

  protected readonly employeeForm = this.fb.nonNullable.group({
    employeeId1: [3, [Validators.required, Validators.min(1)]],
    changeShiftId1: [1, [Validators.required, Validators.min(1)]],
    employeeId2: [4, [Validators.required, Validators.min(1)]],
    changeShiftId2: [2, [Validators.required, Validators.min(1)]]
  });

  protected readonly resolveForm = this.fb.nonNullable.group({
    updatedBy: [1, [Validators.required, Validators.min(1)]],
    comment: ['']
  });

  constructor() {
    this.loadEmployees();
    this.loadMyShifts();
    this.loadOpenRequests();
  }

  protected showView(view: 'employee' | 'manager'): void {
    this.activeView.set(view);
    if (view === 'manager') {
      this.loadManagerRequests();
    }
  }

  protected showManagerTab(tab: 'open' | 'history'): void {
    this.managerTab.set(tab);
    this.loadManagerRequests();
  }

  protected createDemoData(): void {
    this.setupMessage.set('Creating demo data...');

    this.http.post('/employees/setupDummyEmployees', null, { responseType: 'text' }).subscribe({
      next: () => {
        this.http.post('/shift/addDummyShifts', null, { responseType: 'text' }).subscribe({
          next: () => {
            this.setupMessage.set('Demo employees and shifts are ready.');
            this.loadEmployees();
            this.loadMyShifts();
          },
          error: (error) => this.setupMessage.set(this.readError(error))
        });
      },
      error: (error) => this.setupMessage.set(this.readError(error))
    });
  }

  protected submitRequest(): void {
    if (this.employeeForm.invalid) {
      this.employeeForm.markAllAsTouched();
      return;
    }

    this.requestMessage.set('Submitting shift change request...');

    this.http.post('/shiftsSwap/swap', this.employeeForm.getRawValue(), { responseType: 'text' }).subscribe({
      next: (message) => {
        this.requestMessage.set(message);
        this.loadOpenRequests();
        this.loadMyShifts();
      },
      error: (error) => this.requestMessage.set(this.readError(error))
    });
  }

  protected loadMyShifts(): void {
    const employeeId = this.employeeForm.controls.employeeId1.value;

    if (!employeeId || employeeId < 1) {
      this.shiftMessage.set('Enter a valid employee ID to load shifts.');
      return;
    }

    this.loadingShifts.set(true);
    this.shiftMessage.set('');

    this.http.get<Shift[]>(`/shift/employee/${employeeId}`).subscribe({
      next: (shifts) => {
        this.shifts.set(shifts);
        this.loadingShifts.set(false);
        if (!shifts.length) {
          this.shiftMessage.set(`No shifts found for employee #${employeeId}.`);
        }
      },
      error: (error) => {
        this.shifts.set([]);
        this.loadingShifts.set(false);
        this.shiftMessage.set(this.readError(error));
      }
    });
  }

  protected loadOpenRequests(): void {
    this.loadingRequests.set(true);
    this.managerMessage.set('');

    this.http.get<ShiftChangeRequest[]>('/shiftsSwap/open').subscribe({
      next: (requests) => {
        this.requests.set(requests);
        this.loadingRequests.set(false);
      },
      error: (error) => {
        this.requests.set([]);
        this.loadingRequests.set(false);
        this.managerMessage.set(this.readError(error));
      }
    });
  }

  protected loadResolvedRequests(): void {
    this.loadingResolvedRequests.set(true);
    this.managerMessage.set('');

    this.http.get<ShiftChangeRequest[]>('/shiftsSwap/resolved').subscribe({
      next: (requests) => {
        this.resolvedRequests.set(requests);
        this.loadingResolvedRequests.set(false);
      },
      error: (error) => {
        this.resolvedRequests.set([]);
        this.loadingResolvedRequests.set(false);
        this.managerMessage.set(this.readError(error));
      }
    });
  }

  protected resolveRequest(request: ShiftChangeRequest, status: 'APPROVED' | 'REJECTED'): void {
    if (this.resolveForm.invalid) {
      this.resolveForm.markAllAsTouched();
      return;
    }

    this.selectedRequestId.set(request.requestId);
    this.managerMessage.set(`${status === 'APPROVED' ? 'Approving' : 'Rejecting'} request #${request.requestId}...`);

    this.http.post('/shiftsSwap/resolve', {
      requestID: request.requestId,
      status,
      ...this.resolveForm.getRawValue()
    }, { responseType: 'text' }).subscribe({
      next: (message) => {
        this.managerMessage.set(message);
        this.selectedRequestId.set(null);
        this.resolveForm.patchValue({ comment: '' });
        this.loadOpenRequests();
        this.loadResolvedRequests();
      },
      error: (error) => {
        this.managerMessage.set(this.readError(error));
        this.selectedRequestId.set(null);
      }
    });
  }

  protected employeeName(employeeId: number): string {
    const employee = this.employees().find((item) => item.employeeID === employeeId);
    return employee ? `${employee.name} (#${employee.employeeID})` : `Employee #${employeeId}`;
  }

  protected currentEmployee(): Employee | undefined {
    return this.findEmployee(this.employeeForm.controls.employeeId1.value);
  }

  protected currentManager(): Employee | undefined {
    return this.findEmployee(this.resolveForm.controls.updatedBy.value);
  }

  protected managerOpenRequests(): ShiftChangeRequest[] {
    const managerId = this.resolveForm.controls.updatedBy.value;
    return this.requests().filter((request) =>
      this.findEmployee(request.employeeId1)?.managerId === managerId
    );
  }

  protected reviewerName(employeeId?: number): string {
    if (!employeeId) {
      return 'Not recorded';
    }

    return this.employeeName(employeeId);
  }

  protected loadManagerRequests(): void {
    if (this.managerTab() === 'history') {
      this.loadResolvedRequests();
      return;
    }

    this.loadOpenRequests();
  }

  private loadEmployees(): void {
    this.http.get<Employee[]>('/employees/').subscribe({
      next: (employees) => this.employees.set(employees),
      error: () => this.employees.set([])
    });
  }

  private findEmployee(employeeId: number): Employee | undefined {
    return this.employees().find((employee) => employee.employeeID === employeeId);
  }

  private readError(error: { error?: unknown; message?: string }): string {
    if (typeof error.error === 'string' && error.error.trim()) {
      return error.error;
    }

    return error.message || 'Something went wrong. Please try again.';
  }
}
