import { useState, useEffect, useRef } from 'react';
import { useQuery, useMutation, useLazyQuery } from '@apollo/client';
import { useAuth } from '../context/AuthContext';
import { MY_SESSIONS, GET_SESSION } from '../graphql/queries';
import { ASK_QUESTION } from '../graphql/mutations';
import SessionList from '../components/SessionList';
import MessageBubble from '../components/MessageBubble';

export default function ChatPage() {
  const { logout } = useAuth();
  const [activeSessionId, setActiveSessionId] = useState(null);
  const [question, setQuestion] = useState('');
  const [localMessages, setLocalMessages] = useState([]);
  const messagesEndRef = useRef(null);

  const { data: sessionsData, refetch: refetchSessions } = useQuery(MY_SESSIONS);

  const [fetchSession, { data: sessionData }] = useLazyQuery(GET_SESSION, {
    fetchPolicy: 'network-only',
  });

  const [askQuestion, { loading: asking }] = useMutation(ASK_QUESTION);

  useEffect(() => {
    if (activeSessionId) {
      fetchSession({ variables: { id: activeSessionId } });
    } else {
      setLocalMessages([]);
    }
  }, [activeSessionId, fetchSession]);

  useEffect(() => {
    if (sessionData?.session) {
      setLocalMessages(sessionData.session.messages);
    }
  }, [sessionData]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [localMessages]);

  async function handleAsk(e) {
    e.preventDefault();
    if (!question.trim()) return;

    const questionText = question;
    setQuestion('');

    setLocalMessages((prev) => [
      ...prev,
      { id: `temp-user-${Date.now()}`, role: 'USER', content: questionText, sources: [] },
    ]);

    try {
      const res = await askQuestion({
        variables: { question: questionText, sessionId: activeSessionId },
      });

      const result = res.data.askQuestion;

      setLocalMessages((prev) => [
        ...prev,
        {
          id: `temp-assistant-${Date.now()}`,
          role: 'ASSISTANT',
          content: result.answer,
          sources: result.sources.map((s) => s.articleNumber),
        },
      ]);

      if (!activeSessionId) {
        setActiveSessionId(result.sessionId);
      }
      refetchSessions();
    } catch (err) {
      setLocalMessages((prev) => [
        ...prev,
        {
          id: `temp-error-${Date.now()}`,
          role: 'ASSISTANT',
          content: 'Something went wrong. Please try again.',
          sources: [],
        },
      ]);
    }
  }

  function handleNewChat() {
    setActiveSessionId(null);
    setLocalMessages([]);
  }

  const sessions = sessionsData?.mySessions || [];

  return (
    <div className="chat-page">
      <aside className="sidebar">
        <SessionList
          sessions={sessions}
          activeSessionId={activeSessionId}
          onSelect={setActiveSessionId}
          onNewChat={handleNewChat}
        />
        <button className="logout-btn" onClick={logout}>
          Log Out
        </button>
      </aside>

      <main className="chat-main">
        <div className="messages">
          {localMessages.length === 0 && (
            <div className="empty-state">Ask a question about the Constitution of Tanzania.</div>
          )}
          {localMessages.map((msg) => (
            <MessageBubble
              key={msg.id}
              role={msg.role}
              content={msg.content}
              sources={msg.sources}
            />
          ))}
          {asking && <div className="typing-indicator">KatibaAI is thinking...</div>}
          <div ref={messagesEndRef} />
        </div>

        <form className="ask-form" onSubmit={handleAsk}>
          <input
            type="text"
            placeholder="Ask a question..."
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
            disabled={asking}
          />
          <button type="submit" disabled={asking || !question.trim()}>
            Send
          </button>
        </form>
      </main>
    </div>
  );
}
