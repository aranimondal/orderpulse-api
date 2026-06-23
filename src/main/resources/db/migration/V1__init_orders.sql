CREATE TABLE IF NOT EXISTS orders (
    order_id      VARCHAR(20)    PRIMARY KEY,
    customer_name VARCHAR(120)   NOT NULL,
    item_name     VARCHAR(200)   NOT NULL,
    quantity      INTEGER        NOT NULL,
    amount        NUMERIC(10,2)  NOT NULL,
    status        VARCHAR(30)    NOT NULL,
    ordered_at    TIMESTAMP      NOT NULL
);

INSERT INTO orders (order_id, customer_name, item_name, quantity, amount, status, ordered_at) VALUES
('ORD-1001', 'Ananya Sharma',   'Wireless Earbuds Pro',      1, 2499.00, 'DELIVERED',  '2026-06-10 10:15:00'),
('ORD-1002', 'Rahul Verma',     'Mechanical Keyboard X1',    1, 4999.00, 'SHIPPED',    '2026-06-12 14:30:00'),
('ORD-1003', 'Priya Nair',      'Smart Fitness Band',        2, 3198.00, 'PROCESSING', '2026-06-15 09:05:00'),
('ORD-1004', 'Sourav Ghosh',    '27-inch 4K Monitor',        1, 21999.00,'DELIVERED',  '2026-06-16 18:45:00'),
('ORD-1005', 'Meera Iyer',      'USB-C Hub 7-in-1',          3, 1797.00, 'SHIPPED',    '2026-06-18 11:20:00'),
('ORD-1006', 'Arjun Patel',     'Noise Cancelling Headphone',1, 6999.00, 'CANCELLED',  '2026-06-19 16:10:00'),
('ORD-1007', 'Kavya Reddy',     'Portable SSD 1TB',          1, 7499.00, 'PROCESSING', '2026-06-20 08:50:00'),
('ORD-1008', 'Vikram Singh',    'Ergonomic Mouse',           2, 1598.00, 'DELIVERED',  '2026-06-21 13:25:00');
