import { useEffect, useState } from 'react';
import { NoteInput } from './components/NoteInput';
import { SummaryCard } from './components/SummaryCard';
import { UsageBadge } from './components/UsageBadge';
import { fetchUsage, summarizeNote } from './api/client';
import type { SummarizeResponse, UsageStats } from './types';
import './App.css';

function App() {
  const [note, setNote] = useState('');
  const [result, setResult] = useState<SummarizeResponse | null>(null);
  const [usage, setUsage] = useState<UsageStats | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const refreshUsage = () => {
    fetchUsage()
      .then(setUsage)
      .catch(() => setUsage(null));
  };

  useEffect(() => {
    refreshUsage();
  }, []);

  const handleSubmit = async () => {
    setIsLoading(true);
    setError(null);
    setResult(null);
    try {
      const response = await summarizeNote(note);
      setResult(response);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Something went wrong');
    } finally {
      setIsLoading(false);
      refreshUsage();
    }
  };

  return (
    <div className="app">
      <header className="app-header">
        <h1>Clinical Notes Summarizer</h1>
        <UsageBadge usage={usage} />
      </header>

      <div className="hipaa-banner">
        Demo project -- do not paste real patient data. Notes are sent to a
        locally running open-source LLM (Ollama) and are never persisted by
        this app. See the README for production HIPAA considerations.
      </div>

      <main className="app-main">
        <NoteInput
          value={note}
          onChange={setNote}
          onSubmit={handleSubmit}
          isLoading={isLoading}
        />

        <div className="results-panel">
          {error && <div className="error-banner">{error}</div>}
          {result && <SummaryCard result={result} />}
          {!error && !result && (
            <p className="placeholder-text">
              Paste or load a clinical note and click "Summarize note" to see
              a structured summary here.
            </p>
          )}
        </div>
      </main>
    </div>
  );
}

export default App;
