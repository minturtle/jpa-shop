-- orders 테이블에 대한 INSERT문
INSERT INTO orders (order_uid, user_id, address, detailed_address, order_status, created_at, modified_at)
VALUES ('order-001', 1, '123 Main St', 'Apt 101', 'ORDERED', '2021-08-01T00:00:00', '2021-08-01T00:00:00');

-- order_products 테이블에 대한 INSERT문
INSERT INTO order_products (order_id, product_id, count, item_price)
VALUES (1, 1, 2, 500);


-- orders 테이블에 대한 INSERT문
INSERT INTO orders (order_uid, user_id, address, detailed_address, order_status, created_at, modified_at)
VALUES ('order-002', 1, '123 Main St', 'Apt 101', 'CANCELED', '2021-08-02T00:00:00', '2021-08-02T00:00:00');

-- order_products 테이블에 대한 INSERT문
INSERT INTO order_products (order_id, product_id, count, item_price)
VALUES (1, 2, 2, 500);