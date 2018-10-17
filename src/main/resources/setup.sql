DELETE FROM buckets;
DELETE FROM users_relationships;
DELETE FROM users;
DELETE FROM physical_addresses;

INSERT INTO physical_addresses VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'CANADA', 'E3B1G9', 'New Brunswick', '252 Brunswick Street', 'Apartment 402', 'Fredericton');
INSERT INTO physical_addresses VALUES(2, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'CANADA', 'E3A4V4', 'New Brunswick', '18 William Street', '', 'Fredericton');

INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a person', 'brandon1024.br@gmail.com', 'Brandon', 'Richardson', 'Michael', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'USER', 'brandon1024.br', 1);
INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(2, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a person', 'brileb73@gmail.com', 'Brian', 'LeBlanc', 'Frazer', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'ADMIN', 'brileb73', 2);

INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 1, 2);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(2, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 2, 1);

INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'brandons bucket', true, 'brandons bucket', 1);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(2, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'brians bucket', false, 'brians bucket', 2);