INSERT INTO shopping_carts (id, user_id)
SELECT 3, 3
    WHERE NOT EXISTS (
    SELECT 3 FROM shopping_carts WHERE id = 3
);
INSERT INTO shopping_carts (id, user_id)
SELECT 4, 4
    WHERE NOT EXISTS (
    SELECT 4 FROM shopping_carts WHERE id = 4
);
