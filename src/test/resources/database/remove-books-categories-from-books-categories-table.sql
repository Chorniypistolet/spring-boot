DELETE FROM books_categories WHERE book_id IN (1, 2);
DELETE FROM books_categories WHERE category_id IN (SELECT id FROM categories);