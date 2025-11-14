export type FlowDefinition = {
  id: string;
  title: string;
  description: string;
  processes: Process[];
  flowInstances: FlowInstance[];
  updatedAt: Date;
};

export type Status = 'PENDING' | 'ACTIVE' | 'SUCCESS' | 'FAILURE' | 'PAUSED'

export type RequestType = 'ABSENCE_REQUEST'

export type FlowInstance = {
  id: string;
  flowDefinition: FlowDefinition;
  title: string;
  status: Status;
  currentProcess: Process;
  updatedAt: Date;
};

export type TriggerType = 'MANUAL' | 'POST'

export type Trigger = {
  id: string;
  type: TriggerType;
}

export type Process = {
  id: string;
  type: string;
  role: string;
  description: string;
};

export type AbsenceData = {
  startDate: Date;
  endDate: Date;
  submittedBy: string;
  reason: string;
}

export type RequestSubmission = {
  id: string;
  requestType: RequestType;
  status: Status;
  data: AbsenceData;
  submittedAt: Date;
  processedAt: Date;
  flowInstanceId: string;
}

export type FlowDefinitionPayload = {
  title: string;
  description: string;
  processes: {
    id: string;
    title: string;
    createdAt: string | undefined;
  }[];
  trigger: string;
};
