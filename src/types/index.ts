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
  title: string;
  flowStatus: Status;
  currentProcess: Process;
  step: number;
  processes: Process[];
  updatedAt: Date;
  flowDefinition?: FlowDefinition | null;
  triggeredBy: string;
  callingURL: string;
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
  requestStep?: number;
};

export type ClockInData = {
  allFields: {
    startTime: Date;
    endTime: Date;
    date: Date;
    submittedBy: string;
  };
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
  requestTypeName: RequestType;
  status: Status;
  data: {
    allFields: {
      [key: string]: any;
    }
  };
  submittedAt: Date;
  processedAt: Date;
  flowInstanceId: string;
};

export type User = {
  name: string;
  email: string;
};
