export interface ClinicalSummary {
  chiefComplaint: string;
  historyOfPresentIllness: string;
  assessment: string;
  plan: string;
  medications: string[];
  followUp: string;
}

export interface SummarizeResponse {
  summary: ClinicalSummary;
  validated: boolean;
  validationWarnings: string[];
  rawModelOutput: string | null;
}

export interface UsageStats {
  totalRequests: number;
  successfulRequests: number;
  failedRequests: number;
  averageLatencyMs: number;
  lastRequestEpochMillis: number | null;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
}
