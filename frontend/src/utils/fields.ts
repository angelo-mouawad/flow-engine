const normaliseKey = (key: string) => key.replace(/[_\s-]/g, "").toLowerCase();

export const getField = (fields: Record<string, unknown> | undefined | null, key: string): unknown => {
  if (!fields) return undefined;
  if (key in fields) return fields[key];
  const wanted = normaliseKey(key);
  const match = Object.keys(fields).find((candidate) => normaliseKey(candidate) === wanted);
  return match === undefined ? undefined : fields[match];
};

export const getFieldText = (fields: Record<string, unknown> | undefined | null, key: string): string => {
  const value = getField(fields, key);
  return value === undefined || value === null ? "" : String(value);
};

export const prettifyKey = (key: string): string =>
  key
    .replace(/_/g, " ")
    .replace(/([a-z])([A-Z])/g, "$1 $2")
    .replace(/\s+/g, " ")
    .trim()
    .replace(/^./, (first) => first.toUpperCase());

const ISO_DATE = /^\d{4}-\d{2}-\d{2}([T ]\d{2}:\d{2}(:\d{2}(\.\d+)?)?(Z|[+-]\d{2}:?\d{2})?)?$/;

export const formatFieldValue = (value: unknown): string => {
  if (value === null || value === undefined || value === "") return "-";
  if (typeof value === "boolean") return value ? "Yes" : "No";
  if (typeof value === "number") return value.toString();
  if (Array.isArray(value)) return value.map((item) => formatFieldValue(item)).join(", ");
  if (typeof value === "object") return JSON.stringify(value);
  const text = String(value);
  if (ISO_DATE.test(text)) {
    const date = new Date(text);
    if (!Number.isNaN(date.getTime())) {
      return text.length > 10
        ? date.toLocaleString("en-GB", { dateStyle: "medium", timeStyle: "short" })
        : date.toLocaleDateString("en-GB", { dateStyle: "medium" });
    }
  }
  return text;
};
