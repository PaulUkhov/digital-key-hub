
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
                            user_id UUID NOT NULL REFERENCES users(id),
                            role_id BIGINT NOT NULL REFERENCES roles(id),
                            PRIMARY KEY (user_id, role_id)
);

CREATE TABLE profiles (
                          id UUID PRIMARY KEY,
                          user_id UUID UNIQUE NOT NULL REFERENCES users(id),
                          name VARCHAR(100),
                          bio TEXT,
                          avatar_url VARCHAR(255)
);