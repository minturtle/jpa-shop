-- save album
INSERT INTO products (product_uid, name, price, stock_quantity, thumbnail_image_url, description, created_at, modified_at)
VALUES ('album-001', 'Album Name', 2000, 5, 'http://example.com/album_thumbnail.jpg', 'Album description', '2024-02-01 11:00:00', '2024-02-02 11:00:00');

INSERT INTO album (product_id, artist, etc)
VALUES (LAST_INSERT_ID(), 'Artist Name', 'Etc information');



-- save book
INSERT INTO products (product_uid, name, price, stock_quantity, thumbnail_image_url, description, created_at, modified_at)
VALUES ('book-001', 'Book Name', 1500, 20, 'http://example.com/book_thumbnail.jpg', 'Book description', '2024-01-01 10:00:00', '2024-01-02 10:00:00');

INSERT INTO book (product_id, author, isbn)
VALUES (LAST_INSERT_ID(), 'Author Name', 'ISBN1234567890');




-- save movie
INSERT INTO products (product_uid, name, price, stock_quantity, thumbnail_image_url, description, created_at, modified_at)
VALUES ('movie-001', 'Movie Name', 3000, 8, 'http://example.com/movie_thumbnail.jpg', 'Movie description', '2024-03-01 12:00:00', '2024-03-02 12:00:00');

INSERT INTO movie (product_id, director, actor)
VALUES (LAST_INSERT_ID(), 'Director Name', 'Actor Name');

