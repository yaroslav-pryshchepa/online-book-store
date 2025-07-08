INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES
    (1, 'test@example.com', 'password', 'Test', 'User', '123 Test St', false);

INSERT INTO users_roles (users_id, roles_id) VALUES (1, 1);
