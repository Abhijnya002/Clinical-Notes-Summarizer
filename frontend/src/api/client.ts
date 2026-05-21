import type { SummarizeResponse, UsageStats, ApiError } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

async function parseErrorMessage(response: Response): Promise<string> {
  try {
    const body = (await response.json()) as ApiError;
    return body.error ?? `Request failed with status ${response.status}`;
  } catch {
    return `Request failed with status ${response.status}`;
  }
}

export async function summarizeNote(clinicalNote: string): Promise<SummarizeResponse> {
  const response = await fetch(`${API_BASE_URL}/api/summarize`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ clinicalNote }),
  });

  if (!response.ok) {
    throw new Error(await parseErrorMessage(response));
  }

  return response.json() as Promise<SummarizeResponse>;
}

export async function fetchUsage(): Promise<UsageStats> {
  const response = await fetch(`${API_BASE_URL}/api/usage`);
  if (!response.ok) {
    throw new Error(await parseErrorMessage(response));
  }
  return response.json() as Promise<UsageStats>;
}
