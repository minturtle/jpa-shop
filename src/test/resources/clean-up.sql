-- Corrected SQL script with semicolons to separate statements
DELETE FROM order_products WHERE order_id >= 0;
DELETE FROM orders WHERE order_id >= 0;
DELETE FROM carts WHERE user_id >= 0;
DELETE FROM product_category WHERE product_id >= 0;
DELETE FROM album WHERE product_id >= 0;
DELETE FROM book WHERE product_id >= 0;
DELETE FROM movie WHERE product_id >= 0;
DELETE FROM products WHERE product_id >= 0;
DELETE FROM accounts WHERE account_id >= 0;
DELETE FROM users WHERE user_id >= 0;
DELETE FROM categories WHERE category_id >= 0;
