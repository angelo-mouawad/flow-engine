export type Status = "PENDING" | "ACTIVE" | "SUCCESS" | "FAILURE";
export type RequestStatus = "PENDING" | "APPROVED" | "DECLINED" | "CANCELLED";
export type ProcessType = "REQUEST" | "NOTIFICATION" | "APPROVAL";
export type RequestType = "ABSENCE_REQUEST" | "CLOCKIN_REQUEST" | "TASK_CHANGE_REQUEST" | "MEETING_REQUEST";
export type NotificationType = "EMAIL_NOTIFICATION" | "POPUP_NOTIFICATION";

export type Process = Request | Approval | Notification;

export type FlowDefinition = {
  id?: string;
  title: string;
  description: string;
  processes: Process[];
  createdAt?: string;
  updatedAt?: string;
  triggerableBy: string[];
  anyTrigger: boolean;
};

export type FlowDefinitionPayload = {
  title: string;
  description: string;
  triggerableBy: string[];
  processes: Process[];
};

export type FlowInstance = {
  id: string;
  flowDefinitionId: string;
  flowDefinitionTitle?: string;
  title: string;
  flowStatus: Status;
  currentProcess: Process | null;
  step: number;
  processes: Process[];
  data?: Partial<Record<RequestType, Record<string, unknown>>>;
  createdAt?: string;
  updatedAt: string;
  triggeredBy: string;
  callbackUrl?: string | null;
};

type ProcessBase = {
  id?: string;
  name: string;
  step?: number;
};

export type Request = ProcessBase & {
  processType: "REQUEST";
  requestTypeName: RequestType;
  approvable: boolean;
  minApprovals: number;
  approvableBy: string[];
};

export type Approval = ProcessBase & {
  processType: "APPROVAL";
  requestSteps: number[];
};

export type Notification = ProcessBase & {
  processType: "NOTIFICATION";
  notificationType: NotificationType;
  toNotify?: string[];
  requestStep?: number | null;
};

export type RequestSubmission = {
  id: string;
  requestName?: string;
  requestStep?: number;
  requestTypeName: RequestType;
  status: RequestStatus;
  minApprovals?: number;
  approvals?: Record<string, RequestStatus>;
  data: {
    allFields: Record<string, unknown>;
  };
  submittedAt: string;
  processedAt: string | null;
  flowInstanceId: string;
};

export type User = {
  name: string;
  email: string;
};
