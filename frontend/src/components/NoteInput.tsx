import { SAMPLE_NOTE } from '../sampleNote';

interface NoteInputProps {
  value: string;
  onChange: (value: string) => void;
  onSubmit: () => void;
  isLoading: boolean;
}

export function NoteInput({ value, onChange, onSubmit, isLoading }: NoteInputProps) {
  return (
    <div className="note-input">
      <div className="note-input-header">
        <label htmlFor="clinical-note">Clinical note</label>
        <button
          type="button"
          className="link-button"
          onClick={() => onChange(SAMPLE_NOTE)}
          disabled={isLoading}
        >
          Load sample note
        </button>
      </div>

      <textarea
        id="clinical-note"
        rows={12}
        placeholder="Paste a de-identified clinical note here..."
        value={value}
        onChange={(e) => onChange(e.target.value)}
        disabled={isLoading}
      />

      <button
        type="button"
        className="primary-button"
        onClick={onSubmit}
        disabled={isLoading || value.trim().length === 0}
      >
        {isLoading ? 'Summarizing…' : 'Summarize note'}
      </button>
    </div>
  );
}
