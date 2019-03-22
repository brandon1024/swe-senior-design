DELETE FROM items;
DELETE FROM buckets;
DELETE FROM users_relationships;
DELETE FROM users_bucket_relationships;
DELETE FROM users;
DELETE FROM physical_addresses;

INSERT INTO physical_addresses VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'CANADA', 'E3B1G9', 'New Brunswick', '252 Brunswick Street', 'Apartment 402', 'Fredericton');
INSERT INTO physical_addresses VALUES(2, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'CANADA', 'E3A4V4', 'New Brunswick', '18 William Street', '', 'Fredericton');
INSERT INTO physical_addresses VALUES(3, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'CANADA', 'E3A4V4', 'New Brunswick', '19 William Street', '', 'Fredericton');
INSERT INTO physical_addresses VALUES(4, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'CANADA', 'E3A4V4', 'New Brunswick', '20 William Street', '', 'Fredericton');
INSERT INTO physical_addresses VALUES(5, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'CANADA', 'E3A4V4', 'New Brunswick', '21 William Street', '', 'Fredericton');
INSERT INTO physical_addresses VALUES(6, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'CANADA', 'E3A4V4', 'New Brunswick', '22 William Street', '', 'Fredericton');
INSERT INTO physical_addresses VALUES(7, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'CANADA', 'E3A4V4', 'New Brunswick', '23 William Street', '', 'Fredericton');

INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a person', 'brandon1024.br@gmail.com', 'Brandon', 'Richardson', 'Michael', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'USER', 'brandon1024.br', 1);
INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(2, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a person', 'brileb73@gmail.com', 'Brian', 'LeBlanc', 'Frazer', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'ADMIN', 'brileb73', 2);
INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(3, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a person', 'tsargent@unb.ca', 'Tyler', 'Sargent', 'Michael', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'USER', 'tsargent', 3);
INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(4, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a person', 'megan@gmail.com', 'Megan', 'Doherty', '', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'USER', 'megandoherty', 4);
INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(5, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a person', 'kohdy@gmail.com', 'Kohdy', 'Nicholson', '', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'USER', 'kohdynicholson', 5);
INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(6, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a person', 'john@gmail.com', 'John', 'Doe', 'Bennet', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'USER', 'johndoe', 6);
INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(7, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a person', 'sue@gmail.com', 'Sue', 'Loo', '', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'USER', 'sueloo', 7);

INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 1, 2);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(2, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 2, 1);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(3, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 2, 3);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(4, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 5, 3);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(5, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 1, 3);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(6, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 6, 3);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(7, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 7, 3);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(8, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 3, 5);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(9, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 1, 4);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(10, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 4, 7);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(11, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 7, 1);


INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Brandon does fun stuff!', true, 'Brandon''s bucket', 1);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(2, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Brian''s important list of things to do.', true, 'Brian''s bucket', 2);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(3, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Tyler''s bucket list', true, 'Tyler''s bucket', 3);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(4, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Megan''s art bucket', true, 'Megan''s bucket', 4);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(5, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Kohdy''s travelling bucket', true, 'Kohdy''s bucket', 5);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(6, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'John''s bucket', true, 'John''s bucket', 6);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(7, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Sue''s bucket', true, 'Sue''s bucket', 7);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(8, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Tyler''s trip bucket', true, 'Tyler''s trip bucket', 3);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(9, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Brandon''s skydiving bucket', true, 'Brandon''s skydiving bucket', 1);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(10, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Tyler''s bucket', true, 'Tyler''s other bucket', 3);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(11, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'John''s bucket', true, 'John''s new bucket', 6);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(12, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Megan''s bucket', true, 'Megan''s crazy bucket', 4);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(13, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Kohdy''s bucket', true, 'Kohdy''s other bucket', 5);


INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Go skydiving', 10, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(2, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Go running', 10, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(3, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Go driving', 10, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(4, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Eat good food', 1, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(5, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Meet a new friend', 1, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(6, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Get healthy groceries for a month', 2, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(7, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Travel somewhere new', 2, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(8, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Learn french', 3, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(9, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Learn spanish', 4, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(10, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Bungee jumping', 5, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(11, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Give a speech', 5, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(12, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Bike 20 miles', 6, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(13, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Eat pancakes for supper', 6, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(14, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Sleep for 72 hours straight', 7, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(15, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Drive to Montreal', 8, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(16, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Drive to Alberta', 8, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(17, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Get someone to skydive with', 9, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(18, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Skydive', 9, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(19, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Eat a lot of ice cream', 10, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(20, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Hike a mountain', 10, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(21, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Climb a building', 11, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(22, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Learn to code', 11, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(23, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Swim a mile', 12, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(24, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Fly a plane', 13, false);


INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 1, 3);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(2, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 2, 11);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(3, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 3, 5);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(4, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 4, 6);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(5, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 5, 8);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(6, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 6, 1);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(7, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 7, 2);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(8, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 4, 3);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(9, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 2, 12);
