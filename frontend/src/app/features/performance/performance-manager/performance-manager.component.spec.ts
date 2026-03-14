import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerformanceManagerComponent } from './performance-manager.component';

describe('PerformanceManagerComponent', () => {
  let component: PerformanceManagerComponent;
  let fixture: ComponentFixture<PerformanceManagerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PerformanceManagerComponent]
    });
    fixture = TestBed.createComponent(PerformanceManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
