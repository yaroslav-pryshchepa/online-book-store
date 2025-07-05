INSERT INTO categories (id, name, description, is_deleted)
VALUES
    (1, 'Fiction', 'Fiction books', false),
    (2, 'Science', 'Books about science', false),
    (3, 'History', 'Historical literature', false);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES
    (1, 'First Book', 'Author A', '9780306406157', 10.00, 'Description 1', 'cover1.jpg', false),
    (2, 'Second Book', 'Author B', '9781566199094', 15.50, 'Description 2', 'cover2.jpg', false),
    (3, 'Third Book', 'Author C', '9783161484100', 20.00, 'Description 3', 'cover3.jpg', false);

INSERT INTO books_categories (books_id, categories_id)
VALUES
    (1, 1),
    (2, 1),
    (2, 2),
    (3, 3);
