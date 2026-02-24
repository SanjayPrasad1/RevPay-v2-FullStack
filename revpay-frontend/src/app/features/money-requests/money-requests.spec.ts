import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoneyRequests } from './money-requests';

describe('MoneyRequests', () => {
  let component: MoneyRequests;
  let fixture: ComponentFixture<MoneyRequests>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MoneyRequests]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MoneyRequests);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
