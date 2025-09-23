export type User = {
  id?: number;
  name?: string;
  age?: number;
  email?: string;
  password?: string;
}

export type StatusMessage = {
  message: string;
  type: 'error' | 'success';
}
