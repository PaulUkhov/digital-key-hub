CREATE TABLE products (
                          id UUID PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(19,2) NOT NULL,
                          stock_quantity INTEGER NOT NULL,
                          sku VARCHAR(255) NOT NULL,
                          photo_url VARCHAR(255),
                          digital_content VARCHAR(255),
                          is_active BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL
);