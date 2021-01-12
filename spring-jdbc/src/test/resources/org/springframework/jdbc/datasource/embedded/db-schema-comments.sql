-- Failed DROP can be ignored if necessary 
DROP TABLE T_TEST if EXISTS;

-- Create the test table
CREATE TABLE T_TEST
(
    NAME varchar(50) NOT NULL
);