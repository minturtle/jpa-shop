
INSERT INTO categories (category_id, category_uid, name, product_type)
VALUES (1, 'category-003', 'hiphop', 'ALBUM');


INSERT INTO categories (category_id, category_uid, name, product_type)
VALUES (2, 'category-002', 'self-development', 'BOOK');



INSERT INTO categories (category_id, category_uid, name, product_type)
VALUES (3, 'category-001', 'romance', 'MOVIE');


-- save album
INSERT INTO products (product_id , product_uid, name, price, stock_quantity, thumbnail_image_url, description, created_at, modified_at, dtype)
VALUES (1, 'album-001', 'Album Name', 2000, 5, 'http://example.com/album_thumbnail.jpg', 'Album description', '2024-02-01 11:00:00', '2024-02-02 11:00:00', 'ALBUM');

INSERT INTO album (product_id, artist, etc)
VALUES (1, 'Artist Name', 'Etc information');



-- save book
INSERT INTO products (product_id, product_uid, name, price, stock_quantity, thumbnail_image_url, description, created_at, modified_at, dtype)
VALUES (2, 'book-001', 'Book Name', 1500, 20, 'http://example.com/book_thumbnail.jpg', 'Book description', '2024-01-01 10:00:00', '2024-01-02 10:00:00', 'BOOK');

INSERT INTO book (product_id, author, isbn)
VALUES (2, 'Author Name', 'ISBN1234567890');




-- save movie
INSERT INTO products (product_id, product_uid, name, price, stock_quantity, thumbnail_image_url, description, created_at, modified_at, dtype)
VALUES (3, 'movie-001', 'Movie Name', 3000, 8, 'http://example.com/movie_thumbnail.jpg', 'Movie description', '2024-03-01 12:00:00', '2024-03-02 12:00:00', 'MOVIE');

INSERT INTO movie (product_id, director, actor)
VALUES (3, 'Director Name', 'Actor Name');

INSERT INTO products (product_id, product_uid, name, price, stock_quantity, thumbnail_image_url, description, created_at, modified_at, dtype)
VALUES (4, 'movie-002', 'Movie Name2', 4000, 8, 'http://example.com/movie2_thumbnail.jpg', 'Movie description2', '2024-03-01 12:00:00', '2024-03-02 12:00:00', 'MOVIE');

INSERT INTO movie (product_id, director, actor)
VALUES (4, 'Director Name2', 'Actor Name2');

INSERT INTO product_category (product_category_id, product_id, category_id)
VALUES (1, 1, 1);

INSERT INTO product_category (product_category_id, product_id, category_id)
VALUES (2, 2, 2);

INSERT INTO product_category (product_category_id, product_id, category_id)
VALUES (3, 3, 3);