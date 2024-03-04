export function toFormData(body: never): FormData {
  const formData = new FormData();
  Object.entries(body).forEach(([key, value]) => formData.append(key, value as string));
  return formData;
}

export const FORM_DATA_HEADER = { 'Content-Type': 'multipart/form-data' };
export const WWW_FORM_HEADER = { 'Content-Type': 'application/x-www-form-urlencoded' };
