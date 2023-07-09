-- INSERT INTO Account (email, password) VALUES ('admin', '$2a$10$o9v4FooOAgnwk1GevPx0N.xJu8IZsrIP.O2cS8u1kzZ4O2ANdasd2');
-- INSERT INTO Account (email, password) VALUES ('user', '$10$yGh.OpZ21O4nowC6WXNI3eA05nRDctOT4NXFroGzEmWs4liaefkJ2');

insert into authority (authority_name) values ('ROLE_USER');
insert into authority (authority_name) values ('ROLE_ADMIN');
--
-- insert into account_authority (account_id, authority_name) values (1, 'ROLE_USER');
-- insert into account_authority (account_id, authority_name) values (1, 'ROLE_ADMIN');
-- insert into account_authority (account_id, authority_name) values (2, 'ROLE_USER');