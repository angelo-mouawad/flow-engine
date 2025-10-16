export type FlowDefinition = {
  id?: string;
  title: string;
  description: string;
  processes: Process[];
  flowInstances?: FlowInstance[];
  createdAt?: Date;
  updatedAt?: Date;
};

export type FlowInstance = {};

export type Process = {};

export type Step = {
  id: number;
  name: string;
  type: string;
  role: string;
  description: string;
};
