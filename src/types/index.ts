export type TriggerType = "MANUAL" | "POST";
export type Status = "PENDING" | "ACTIVE" | "SUCCESS" | "FAILURE" | "PAUSED";
export type ProcessType = "REQUEST" | "NOTIFICATION" | "APPROVAL";
export type RequestType = "ABSENCE_REQUEST" | "CLOCKIN_REQUEST";
export type NotificationType = "EMAIL_NOTIFICATION" | "POPUP_NOTIFICATION";

export type Process = Request | Approval | Notification;

export type FlowDefinition = {
  id?: string;
  title: string;
  description: string;
  processes: Process[];
  flowInstances?: FlowInstance[];
  updatedAt?: Date;
};

export type FlowDefinitionPayload = {
  title: string;
  description: string;
  processes: Process[];
};

export type FlowInstance = {
  id: string;
  flowDefinitionId: string;
  title: string;
  flowStatus: Status;
  currentProcess: Process;
  updatedAt: Date;
};

type ProcessBase = {
  id?: string;
  name: string;
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
};

export type Notification = ProcessBase & {
  processType: "NOTIFICATION";
  notificationType: NotificationType;
};

export type AbsenceData = {
  allFields: {
    startDate: Date;
    endDate: Date;
    submittedBy: string;
    reason: string;
  };
};

export type RequestSubmission = {
  id: string;
  requestType: RequestType;
  status: Status;
  data: AbsenceData;
  submittedAt: Date;
  processedAt: Date;
  flowInstanceId: string;
};

export type User = {
  name: string;
  email: string;
};
