CREATE TABLE orders (
                        id UUID PRIMARY KEY,
                        user_id UUID NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        total_amount DECIMAL(19,2),
                        created_at TIMESTAMP NOT NULL
);

CREATE TABLE order_items (
                             id UUID PRIMARY KEY,
                             order_id UUID NOT NULL REFERENCES orders(id),
                             product_id UUID NOT NULL,
                             quantity INTEGER NOT NULL,
                             unit_price DECIMAL(19,2) NOT NULL,
                             subtotal DECIMAL(19,2) NOT NULL
);