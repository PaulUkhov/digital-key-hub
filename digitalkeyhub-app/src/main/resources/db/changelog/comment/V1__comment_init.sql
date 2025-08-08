CREATE TABLE comments (
                          id UUID PRIMARY KEY,
                          entity_id UUID NOT NULL,
                          entity_type VARCHAR(50) NOT NULL,
                          user_id UUID NOT NULL,
                          content TEXT NOT NULL,
                          created_at TIMESTAMP NOT NULL
);