const toDate = (value?: string | Date | null): Date | null => {
  if (!value) return null;
  const date = value instanceof Date ? value : new Date(value);
  return Number.isNaN(date.getTime()) ? null : date;
};

export const formatDate = (value?: string | Date | null, fallback = "-"): string => {
  const date = toDate(value);
  return date
    ? date.toLocaleDateString("en-GB", { day: "2-digit", month: "short", year: "numeric" })
    : fallback;
};

export const formatDateTime = (value?: string | Date | null, fallback = "-"): string => {
  const date = toDate(value);
  return date
    ? date.toLocaleString("en-GB", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      })
    : fallback;
};

export const timeValue = (value?: string | Date | null): number => toDate(value)?.getTime() ?? 0;

export const formatTriggerName = (email?: string | null): string => {
  if (!email) return "";
  const local = email.split("@")[0] ?? email;
  const parts = local.split(/[._-]/).filter(Boolean);
  const name = parts.map((part) => part.charAt(0).toUpperCase() + part.slice(1)).join(" ");
  return name || email;
};
