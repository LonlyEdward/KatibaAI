import { gql } from '@apollo/client';

export const ASK_QUESTION = gql`
  mutation AskQuestion($question: String!, $sessionId: ID) {
    askQuestion(question: $question, sessionId: $sessionId) {
      sessionId
      answer
      sources {
        articleNumber
        chapterTitle
        distance
      }
    }
  }
`;
