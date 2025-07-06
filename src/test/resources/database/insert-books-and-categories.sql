INSERT INTO categories (id, name, description, is_deleted)
VALUES
    (1, 'Fiction', 'Fiction books', false),
    (2, 'Science', 'Books about science', false),
    (3, 'History', 'Historical literature', false);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES
    (1, 'Sapiens1', 'Yuval Harari', '9780062316096', 10.99, 'Test description', 'http://example.com/test-cover.jpg', false),
    (2, 'Dune', 'Frank Herbert', '9780441013593', 15.99, 'Test description', 'http://example.com/test-cover.jpg', false),
    (3, 'The Hobbit', 'Yuval Tolkien', '9780547928227', 20.99, 'Test description', 'http://example.com/test-cover.jpg', false);

INSERT INTO books_categories (books_id, categories_id)
VALUES
    (1, 1),
    (2, 1),
    (3, 2);
