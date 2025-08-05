import { NgModule } from '@angular/core';
import { NZ_MESSAGE_CONFIG } from 'ng-zorro-antd/message';

@NgModule({
  providers: [
    { provide: NZ_MESSAGE_CONFIG, useValue: { duration: 5000 } } // 5000ms = 5 giây
  ]
})
export class MessageConfigModule { }
