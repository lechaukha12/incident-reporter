import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzModalService } from 'ng-zorro-antd/modal';
import { environment } from '../environments/environment';

interface Incident {
  id: number;
  title: string;
  description: string;
  status: string;
  severityLevel: string;
  affectedService: string;
  assignee: string | null;
  reportedBy: string;
  resolved: boolean;
  resolvedAt: string | null;
  createdAt: string;
  updatedAt: string;
  timeline: TimelineItem[];
}

interface TimelineItem {
  id: number;
  message: string;
  updateType: string;
  createdBy: string;
  createdAt: string;
}

interface IncidentStats {
  totalIncidents: number;
  activeIncidents: number;
  resolvedIncidents: number;
  byStatus: Record<string, number>;
  bySeverity: Record<string, number>;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  incidents: Incident[] = [];
  loading = false;
  stats: IncidentStats | null = null;
  selectedIncident: Incident | null = null;
  isModalVisible = false;
  isUpdateModalVisible = false;
  updateMessage = '';
  updateType = 'INVESTIGATION';
  newIncident = {
    title: '',
    description: '',
    status: 'INVESTIGATING',
    severityLevel: 'HIGH',
    affectedService: '',
    reportedBy: ''
  };
  
  updateTypes = [
    { label: 'Điều tra', value: 'INVESTIGATION' },
    { label: 'Thay đổi trạng thái', value: 'STATUS_CHANGE' },
    { label: 'Nguyên nhân gốc rễ', value: 'ROOT_CAUSE' },
    { label: 'Hành động đã thực hiện', value: 'ACTION_TAKEN' },
    { label: 'Giải quyết', value: 'RESOLUTION' },
    { label: 'Khác', value: 'OTHER' }
  ];
  
  severityLevels = [
    { label: 'Nghiêm trọng', value: 'CRITICAL' },
    { label: 'Cao', value: 'HIGH' },
    { label: 'Trung bình', value: 'MEDIUM' },
    { label: 'Thấp', value: 'LOW' }
  ];
  
  statuses = [
    { label: 'Đang điều tra', value: 'INVESTIGATING' },
    { label: 'Đã xác định', value: 'IDENTIFIED' },
    { label: 'Đang theo dõi', value: 'MONITORING' },
    { label: 'Đã giải quyết', value: 'RESOLVED' }
  ];

  constructor(
    private http: HttpClient,
    private message: NzMessageService,
    private modal: NzModalService
  ) {}

  ngOnInit(): void {
    this.fetchIncidents();
    this.fetchStats();
  }

  fetchIncidents(): void {
    this.loading = true;
    this.http.get<any>(`${environment.apiUrl}/incidents`, { params: this.currentParams }).subscribe({
      next: (response) => {
        this.incidents = response.content;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error fetching incidents', error);
        this.message.error('Không thể tải danh sách sự cố');
        this.loading = false;
      }
    });
  }

  fetchStats(): void {
    this.http.get<IncidentStats>(`${environment.apiUrl}/incidents/stats`).subscribe({
      next: (stats: IncidentStats) => {
        this.stats = stats;
      },
      error: (error: any) => {
        console.error('Error fetching stats', error);
      }
    });
  }

  currentParams: any = {};

  filterByStatus(status: string): void {
    this.currentParams = { status };
    this.fetchIncidents();
  }

  filterBySeverity(severity: string): void {
    this.currentParams = { severityLevel: severity };
    this.fetchIncidents();
  }

  resetFilters(): void {
    this.currentParams = {};
    this.fetchIncidents();
  }

  showIncidentDetails(incident: Incident): void {
    this.selectedIncident = incident;
    this.isModalVisible = true;
  }

  handleModalCancel(): void {
    this.isModalVisible = false;
  }

  showUpdateModal(incident: Incident): void {
    this.selectedIncident = incident;
    this.isUpdateModalVisible = true;
  }

  handleUpdateModalCancel(): void {
    this.isUpdateModalVisible = false;
    this.updateMessage = '';
  }

  addUpdate(): void {
    if (!this.selectedIncident || !this.updateMessage) {
      this.message.warning('Vui lòng nhập nội dung cập nhật');
      return;
    }

    const update = {
      message: this.updateMessage,
      updateType: this.updateType,
      createdBy: 'Web User'
    };

    this.http.post(`${environment.apiUrl}/incidents/${this.selectedIncident.id}/updates`, update).subscribe({
      next: () => {
        this.message.success('Đã thêm cập nhật thành công');
        this.isUpdateModalVisible = false;
        this.updateMessage = '';
        this.fetchIncidents();
        
        // Refresh the selected incident details
        if (this.selectedIncident) {
          this.http.get<Incident>(`${environment.apiUrl}/incidents/${this.selectedIncident.id}`).subscribe({
            next: (incident: Incident) => {
              this.selectedIncident = incident;
            }
          });
        }
      },
      error: (error: any) => {
        console.error('Error adding update', error);
        this.message.error('Không thể thêm cập nhật');
      }
    });
  }

  resolveIncident(incident: Incident): void {
    this.modal.confirm({
      nzTitle: 'Xác nhận giải quyết sự cố',
      nzContent: `Bạn có chắc chắn muốn đánh dấu sự cố "${incident.title}" là đã giải quyết không?`,
      nzOkText: 'Xác nhận',
      nzCancelText: 'Hủy',
      nzOnOk: () => {
        this.http.put(`${environment.apiUrl}/incidents/${incident.id}/resolve`, {}).subscribe({
          next: () => {
            this.message.success('Sự cố đã được đánh dấu là đã giải quyết');
            this.fetchIncidents();
            this.fetchStats();
          },
          error: (error: any) => {
            console.error('Error resolving incident', error);
            this.message.error('Không thể giải quyết sự cố');
          }
        });
      }
    });
  }

  createIncident(): void {
    this.modal.confirm({
      nzTitle: 'Tạo sự cố mới',
      nzContent: this.renderCreateIncidentForm(),
      nzOkText: 'Tạo sự cố',
      nzCancelText: 'Hủy',
      nzWidth: 600,
      nzOnOk: () => {
        if (!this.newIncident.title || !this.newIncident.reportedBy) {
          this.message.warning('Vui lòng điền đầy đủ thông tin bắt buộc');
          return false;
        }

        this.http.post(`${environment.apiUrl}/incidents`, this.newIncident).subscribe({
          next: () => {
            this.message.success('Đã tạo sự cố mới');
            this.fetchIncidents();
            this.fetchStats();
            // Reset form
            this.newIncident = {
              title: '',
              description: '',
              status: 'INVESTIGATING',
              severityLevel: 'SEV2',
              affectedService: '',
              reportedBy: ''
            };
            return true;
          },
          error: (error: any) => {
            console.error('Error creating incident', error);
            this.message.error('Không thể tạo sự cố mới');
            return true;
          }
        });
        return true;
      }
    });
  }

  renderCreateIncidentForm(): string {
    return `
      <form>
        <div style="margin-bottom: 16px;">
          <label for="title" style="display: block; margin-bottom: 8px;">Tiêu đề <span style="color: red;">*</span></label>
          <input id="title" type="text" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 2px;" 
                 [(ngModel)]="newIncident.title" name="title">
        </div>
        
        <div style="margin-bottom: 16px;">
          <label for="description" style="display: block; margin-bottom: 8px;">Mô tả</label>
          <textarea id="description" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 2px;" rows="4"
                    [(ngModel)]="newIncident.description" name="description"></textarea>
        </div>
        
        <div style="margin-bottom: 16px;">
          <label for="severityLevel" style="display: block; margin-bottom: 8px;">Mức độ nghiêm trọng</label>
          <select id="severityLevel" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 2px;"
                  [(ngModel)]="newIncident.severityLevel" name="severityLevel">
            <option *ngFor="let level of severityLevels" [value]="level.value">{{ level.label }}</option>
          </select>
        </div>
        
        <div style="margin-bottom: 16px;">
          <label for="affectedService" style="display: block; margin-bottom: 8px;">Dịch vụ bị ảnh hưởng</label>
          <input id="affectedService" type="text" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 2px;" 
                 [(ngModel)]="newIncident.affectedService" name="affectedService">
        </div>
        
        <div style="margin-bottom: 16px;">
          <label for="reportedBy" style="display: block; margin-bottom: 8px;">Người báo cáo <span style="color: red;">*</span></label>
          <input id="reportedBy" type="text" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 2px;" 
                 [(ngModel)]="newIncident.reportedBy" name="reportedBy">
        </div>
      </form>
    `;
  }

  getSeverityColor(severity: string): string {
    switch (severity) {
      case 'CRITICAL': return 'red';
      case 'HIGH': return 'orange';
      case 'MEDIUM': return 'gold';
      case 'LOW': return 'green';
      default: return 'blue';
    }
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'INVESTIGATING': return 'processing';
      case 'IDENTIFIED': return 'warning';
      case 'MONITORING': return 'cyan';
      case 'RESOLVED': return 'success';
      default: return 'default';
    }
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return `${date.toLocaleDateString('vi-VN')} ${date.toLocaleTimeString('vi-VN')}`;
  }
}
