import type { UsageStats } from '../types';

interface UsageBadgeProps {
  usage: UsageStats | null;
}

export function UsageBadge({ usage }: UsageBadgeProps) {
  if (!usage) {
    return null;
  }

  return (
    <div className="usage-badge" title="Aggregate counts only -- no note content is ever tracked">
      <span>{usage.totalRequests} requests</span>
      <span>{usage.failedRequests} failed</span>
      <span>{Math.round(usage.averageLatencyMs)}ms avg</span>
    </div>
  );
}
