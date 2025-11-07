export type FlowDefinition = {
  id: string;
  title: string;
  description: string;
  processes: Process[];
  flowInstances: FlowInstance[];
  updatedAt: Date;
};

export type Status = 'PENDING' | 'ACTIVE' | 'SUCCESS' | 'FAILURE' | 'PAUSED'

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
  title: string;
  type: string;
};