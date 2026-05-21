import type { SummarizeResponse } from '../types';

interface SummaryCardProps {
  result: SummarizeResponse;
}

function Field({ label, value }: { label: string; value: string }) {
  return (
    <div className="summary-field">
      <span className="summary-field-label">{label}</span>
      <p>{value || 'Not documented'}</p>
    </div>
  );
}

export function SummaryCard({ result }: SummaryCardProps) {
  const { summary, validated, validationWarnings } = result;

  return (
    <div className="summary-card">
      {!validated && (
        <div className="warning-banner">
          <strong>Review needed:</strong> the model output did not pass
          structured validation.
          <ul>
            {validationWarnings.map((warning) => (
              <li key={warning}>{warning}</li>
            ))}
          </ul>
        </div>
      )}
      {validated && validationWarnings.length > 0 && (
        <div className="info-banner">
          <ul>
            {validationWarnings.map((warning) => (
              <li key={warning}>{warning}</li>
            ))}
          </ul>
        </div>
      )}

      <Field label="Chief Complaint" value={summary.chiefComplaint} />
      <Field label="History of Present Illness" value={summary.historyOfPresentIllness} />
      <Field label="Assessment" value={summary.assessment} />
      <Field label="Plan" value={summary.plan} />

      <div className="summary-field">
        <span className="summary-field-label">Medications</span>
        {summary.medications.length > 0 ? (
          <ul>
            {summary.medications.map((med) => (
              <li key={med}>{med}</li>
            ))}
          </ul>
        ) : (
          <p>Not documented</p>
        )}
      </div>

      <Field label="Follow-up" value={summary.followUp} />
    </div>
  );
}
