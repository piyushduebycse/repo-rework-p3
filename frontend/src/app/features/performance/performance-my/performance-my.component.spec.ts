import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerformanceMyComponent } from './performance-my.component';

describe('PerformanceMyComponent', () => {
  let component: PerformanceMyComponent;
  let fixture: ComponentFixture<PerformanceMyComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PerformanceMyComponent]
    });
    fixture = TestBed.createComponent(PerformanceMyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
