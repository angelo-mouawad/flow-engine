export type FlowDefinition = {
  id?: string;
  title: string;
  description: string;
  processes: Process[];
  flowInstances?: FlowInstance[];
  updatedAt?: Date;
};

export type Status = "PENDING" | "ACTIVE" | "SUCCESS" | "FAILURE" | "PAUSED";

export type RequestType = "ABSENCE_REQUEST";

export type FlowInstance = {
  id: string;
  flowDefinitionId: string;
  title: string;
  flowStatus: Status;
  currentProcess: Process;
  updatedAt: Date;
  flowDefinition?: FlowDefinition | null;
};

export type TriggerType = "MANUAL" | "POST";

export type Trigger = {
  id: string;
  type: TriggerType;
};

export type Process = {
  id: string;
  processType: string;
  name: string;
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

export type FlowDefinitionPayload = {
  title: string;
  description: string;
  processes: string[];
};

export type User = {
  type: string;
  email: string;
}