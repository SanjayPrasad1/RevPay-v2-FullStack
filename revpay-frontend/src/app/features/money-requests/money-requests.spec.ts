import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoneyRequestsComponent } from './money-requests.component';

describe('MoneyRequests', () => {
  let component: MoneyRequestsComponent;
  let fixture: ComponentFixture<MoneyRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MoneyRequestsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MoneyRequestsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
