export type FlowDefinition = {
  id: string;
  title: string;
  description: string;
  processes: Process[];
  flowInstances: FlowInstance[];
  updatedAt: Date;
};

export type Status = 'PENDING' | 'ACTIVE' | 'SUCCESS' | 'FAILURE'

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

export type Step = {
  id: number;
  name: string;
  type: string;
  role: string;
  description: string;
};

export type Process = {
  id: string;
  name: string;
  type: string;
  role: string;
  description: string;
};
