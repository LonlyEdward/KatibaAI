export default function MessageBubble({ role, content, sources }) {
  const isUser = role === 'USER';

  const sourceList = Array.isArray(sources) ? sources : [];

  return (
    <div className={`message ${isUser ? 'message-user' : 'message-assistant'}`}>
      <div className="message-role">{isUser ? 'You' : 'KatibaAI'}</div>
      <div className="message-content">{content}</div>
      {sourceList.length > 0 && (
        <div className="message-sources">Sources: Article {sourceList.join(', Article ')}</div>
      )}
    </div>
  );
}
