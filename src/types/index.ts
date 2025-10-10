export type FlowDefinition = {
    id?: string,
    title: string,
    description: string,
    processes: Process[],
    FlowInstances?: FlowInstance[],
    createdAt?: Date,
    updatedAt?: Date
};

export type FlowInstance = {

};

export type Process = {

};
