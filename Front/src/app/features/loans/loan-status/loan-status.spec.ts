import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanStatus } from './loan-status';

describe('LoanStatus', () => {
  let component: LoanStatus;
  let fixture: ComponentFixture<LoanStatus>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoanStatus]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoanStatus);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
