-- The next comment line has no text after the '--' prefix.
--
-- The next comment line starts with a space.
-- x, y, z...

INSERT INTO customer (id, name)
VALUES (1, 'Rod; Johnson'),
       (2, 'Adrian Collier');
-- This is also a comment.
INSERT INTO orders(id, order_date, customer_id)
VALUES (1, '2008-01-02', 2);
INSERT INTO orders(id, order_date, customer_id)
VALUES (1, '2008-01-02', 2);
INSERT INTO persons( person_id--      
                   , name)
VALUES ( 1 -- person_id
       , 'Name' --name
       );--