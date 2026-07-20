import { gql } from '@apollo/client';

export const MY_SESSIONS = gql`
  query MySessions {
    mySessions {
      id
      title
      createdAt
      updatedAt
    }
  }
`;

export const GET_SESSION = gql`
  query GetSession($id: ID!) {
    session(id: $id) {
      id
      title
      messages {
        id
        role
        content
        sources
        createdAt
      }
    }
  }
`;
