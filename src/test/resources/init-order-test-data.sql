-- orders 테이블에 대한 INSERT문
INSERT INTO orders (order_id, order_uid, user_id, address, detailed_address, account_id, amount, order_status, created_at, modified_at)
VALUES (1, 'order-001', 1, '123 Main St', 'Apt 101',  1, 1000, 'ORDERED', '2021-08-01T00:00:00', '2021-08-01T00:00:00');

-- order_products 테이블에 대한 INSERT문
INSERT INTO order_products (order_id, product_id, count, item_price)
VALUES (1, 1, 2, 500);


-- orders 테이블에 대한 INSERT문
INSERT INTO orders (order_id,order_uid, user_id, address, detailed_address, account_id, amount, order_status, created_at, modified_at)
VALUES (2, 'order-002', 1, '123 Main St', 'Apt 101',  1, 2000, 'CANCELED', '2021-08-02T00:00:00', '2021-08-02T00:00:00');

-- order_products 테이블에 대한 INSERT문
INSERT INTO order_products (order_id, product_id, count, item_price)
VALUES (2, 2, 2, 1000);