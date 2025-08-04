import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';

// NG-ZORRO
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzModalModule } from 'ng-zorro-antd/modal';
import { NzMessageModule } from 'ng-zorro-antd/message';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzSelectModule } from 'ng-zorro-antd/select';
import { NzTimelineModule } from 'ng-zorro-antd/timeline';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzPopconfirmModule } from 'ng-zorro-antd/popconfirm';
import { NzBadgeModule } from 'ng-zorro-antd/badge';
import { NzSpinModule } from 'ng-zorro-antd/spin';

import { AppComponent } from './app.component';
import { SimpleTestComponent } from './simple-test.component';

@NgModule({
  declarations: [
    AppComponent,
    SimpleTestComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    BrowserAnimationsModule,
    FormsModule,
    // NG-ZORRO
    NzButtonModule,
    NzTableModule,
    NzDividerModule,
    NzModalModule,
    NzMessageModule,
    NzTagModule,
    NzInputModule,
    NzSelectModule,
    NzTimelineModule,
    NzIconModule,
    NzCardModule,
    NzPopconfirmModule,
    NzBadgeModule,
    NzSpinModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
