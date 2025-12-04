export type FlowDefinition = {
  id?: string;
  title: string;
  description: string;
  processes: Process[];
  flowInstances?: FlowInstance[];
  updatedAt?: Date;
};

export type Status = "PENDING" | "ACTIVE" | "SUCCESS" | "FAILURE" | "PAUSED";

export type FlowInstance = {
  id: string;
  flowDefinitionId: string;
  title: string;
  flowStatus: Status;
  currentProcess: Process;
  updatedAt: Date;
};

export type TriggerType = "MANUAL" | "POST";

export type Trigger = {
  id: string;
  type: TriggerType;
};

export type ProcessType = "REQUEST" | "NOTIFICATION" | "APPROVAL";
export type RequestType = "ABSENCE_REQUEST" | "CLOCK_IN_REQUEST";

export type Process = {
  id?: string;
  processType: ProcessType;
  name: string;
  approvable?: boolean;
  minApprovals?: number;
  approvableBy?: string[];
  requestTypeName?: RequestType;
};
// Generalize, depends on type

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

export type FlowDefinitionPayload = {
  title: string;
  description: string;
  processes: Process[];
};

export type User = {
  name: string;
  email: string;
};
