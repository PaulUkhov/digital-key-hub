CREATE TABLE payments (
                          id UUID PRIMARY KEY,
                          order_id UUID NOT NULL,
                          user_id UUID NOT NULL,
                          amount DECIMAL(19,2) NOT NULL,
                          stripe_payment_id VARCHAR(255) NOT NULL,
                          status VARCHAR(50) NOT NULL,
                          error_message TEXT,
                          completed_at TIMESTAMP
);