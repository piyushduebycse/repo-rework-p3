import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerformanceApplyComponent } from './performance-apply.component';

describe('PerformanceApplyComponent', () => {
  let component: PerformanceApplyComponent;
  let fixture: ComponentFixture<PerformanceApplyComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PerformanceApplyComponent]
    });
    fixture = TestBed.createComponent(PerformanceApplyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
