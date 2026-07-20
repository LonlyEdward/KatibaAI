export default function SessionList({ sessions, activeSessionId, onSelect, onNewChat }) {
  return (
    <div className="session-list">
      <button className="new-chat-btn" onClick={onNewChat}>
        + New Chat
      </button>
      {sessions.map((session) => (
        <div
          key={session.id}
          className={`session-item ${session.id === activeSessionId ? 'active' : ''}`}
          onClick={() => onSelect(session.id)}
        >
          {session.title}
        </div>
      ))}
    </div>
  );
}
