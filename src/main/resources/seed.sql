/**
---------------------------------------------------------------
NOTE: THIS SCRIPT MUST NOT BE USED IN A PRODUCTION ENVIRONMENT.
---------------------------------------------------------------

The following SQL script can be used to seed the database with mock data for demonstration and testing purposes.

Login credentials for all users:
      admin/admin@example.com/password
      testuser1/user1@example.com/password
      testuser2/user2@example.com/password
      testuser3/user3@example.com/password
      testuser4/user4@example.com/password
      testuser5/user5@example.com/password
      testuser6/user6@example.com/password

This script will generate:
- 7 physical addresses
- 7 users (1 admin, 6 users)
- 11 user-to-user relationships
- 13 buckets
- 24 items
- 9 user-bucket relationships

To execute this script, simply open a console session for the appropriate database and run this script.
*/

DELETE FROM items;
DELETE FROM users_bucket_relationships;
DELETE FROM buckets;
DELETE FROM users_relationships;
DELETE FROM users;
DELETE FROM physical_addresses;


INSERT INTO physical_addresses VALUES
(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'CANADA', 'A1B2C3', 'New Brunswick', '252 Example Street', '', 'Fredericton'),
(2, '2018-09-15 19:10:08.599000', '2018-09-15 19:10:08.599000', 'CANADA', 'A1B2C3', 'New Brunswick', '253 Example Street', '', 'Fredericton'),
(3, '2018-09-16 19:10:08.599000', '2018-09-16 19:10:08.599000', 'CANADA', 'A1B2C3', 'New Brunswick', '254 Example Street', '', 'Fredericton'),
(4, '2018-09-17 19:10:08.599000', '2018-09-17 19:10:08.599000', 'CANADA', 'A1B2C3', 'New Brunswick', '255 Example Street', '', 'Fredericton'),
(5, '2018-09-18 19:10:08.599000', '2018-09-18 19:10:08.599000', 'CANADA', 'A1B2C3', 'New Brunswick', '256 Example Street', '', 'Fredericton'),
(6, '2018-09-19 19:10:08.599000', '2018-09-19 19:10:08.599000', 'CANADA', 'A1B2C3', 'New Brunswick', '257 Example Street', '', 'Fredericton'),
(7, '2018-09-20 19:10:08.599000', '2018-09-20 19:10:08.599000', 'CANADA', 'A1B2C3', 'New Brunswick', '258 Example Street', '', 'Fredericton');

INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, role, username, user_address_id, password, profile_picture_object_key) VALUES
(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am an admin', 'admin@example.com', 'Admin', 'One', '', 'ROLE_ADMIN', 'administrator', 1,
 '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', ''),
(2, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a user', 'user1@example.com', 'User', 'One', '',  'ROLE_USER', 'testuser1', 2,
 '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', ''),
(3, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a user', 'user2@example.ca', 'User', 'Two', '','ROLE_USER', 'testuser2', 3,
 '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', ''),
(4, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a user', 'user3@example.com', 'User', 'Three', '' , 'ROLE_USER', 'testuser3', 4,
 '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', ''),
(5, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a user', 'user4@example.com', 'User', 'Four', '', 'ROLE_USER', 'testuser4', 5,
 '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', ''),
(6, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a user', 'user5@example.com', 'User', 'Five', '', 'ROLE_USER', 'testuser5', 6,
 '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', ''),
(7, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a user', 'user6@example.com', 'User', 'Six', '',  'ROLE_USER', 'testuser6', 7,
 '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', '');

INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES
(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 1, 2),
(2, '2018-09-15 19:10:08.599000', '2018-09-15 19:10:08.599000', 2, 1),
(3, '2018-09-16 19:10:08.599000', '2018-09-16 19:10:08.599000', 2, 3),
(4, '2018-09-17 19:10:08.599000', '2018-09-17 19:10:08.599000', 5, 3),
(5, '2018-09-18 19:10:08.599000', '2018-09-18 19:10:08.599000', 1, 3),
(6, '2018-09-19 19:10:08.599000', '2018-09-19 19:10:08.599000', 6, 3),
(7, '2018-09-20 19:10:08.599000', '2018-09-20 19:10:08.599000', 7, 3),
(8, '2018-09-21 19:10:08.599000', '2018-09-21 19:10:08.599000', 3, 5),
(9, '2018-09-22 19:10:08.599000', '2018-09-22 19:10:08.599000', 1, 4),
(10, '2018-09-23 19:10:08.599000', '2018-09-23 19:10:08.599000', 4, 7),
(11, '2018-09-24 19:10:08.599000', '2018-09-24 19:10:08.599000', 7, 1);


INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES
(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Admin does fun stuff!', true, 'Admin''s bucket', 1),
(2, '2018-09-15 19:10:08.599000', '2018-09-15 19:10:08.599000', 'User 1''s important list of things to do.', true, 'User 1''s bucket', 2),
(3, '2018-09-16 19:10:08.599000', '2018-09-16 19:10:08.599000', 'User 2''s bucket list', true, 'User 2''s bucket', 3),
(4, '2018-09-17 19:10:08.599000', '2018-09-17 19:10:08.599000', 'User 3''s art bucket', false, 'User 3''s bucket', 4),
(5, '2018-09-18 19:10:08.599000', '2018-09-18 19:10:08.599000', 'User 4''s travelling bucket', true, 'User 4''s bucket', 5),
(6, '2018-09-19 19:10:08.599000', '2018-09-19 19:10:08.599000', 'User 5''s bucket', false, 'User 5''s bucket', 6),
(7, '2018-09-20 19:10:08.599000', '2018-09-20 19:10:08.599000', 'User 6''s bucket', true, 'User 6''s bucket', 7),
(8, '2018-09-21 19:10:08.599000', '2018-09-21 19:10:08.599000', 'User 2''s trip bucket', true, 'User 2''s trip bucket', 3),
(9, '2018-09-22 19:10:08.599000', '2018-09-22 19:10:08.599000', 'Admin''s skydiving bucket', true, 'Admin''s skydiving bucket', 1),
(10, '2018-09-23 19:10:08.599000', '2018-09-23 19:10:08.599000', 'User 2''s bucket', false, 'User 2''s other bucket', 3),
(11, '2018-09-24 19:10:08.599000', '2018-09-24 19:10:08.599000', 'User 5''s bucket', true, 'User 5''s new bucket', 6),
(12, '2018-09-25 19:10:08.599000', '2018-09-25 19:10:08.599000', 'User 3''s bucket', true, 'User 3''s crazy bucket', 4),
(13, '2018-09-26 19:10:08.599000', '2018-09-26 19:10:08.599000', 'User 4''s bucket', true, 'User 4''s other bucket', 5);


INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES
(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Go skydiving', 10, false),
(2, '2018-09-15 19:10:08.599000', '2018-09-15 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Go running', 10, false),
(3, '2018-09-16 19:10:08.599000', '2018-09-16 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Go driving', 10, false),
(4, '2018-09-17 19:10:08.599000', '2018-09-17 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Eat good food', 1, true),
(5, '2018-09-18 19:10:08.599000', '2018-09-18 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Meet a new friend', 1, false),
(6, '2018-09-19 19:10:08.599000', '2018-09-19 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Get healthy groceries for a month', 2, false),
(7, '2018-09-20 19:10:08.599000', '2018-09-20 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Travel somewhere new', 2, true),
(8, '2018-09-21 19:10:08.599000', '2018-09-21 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Learn french', 3, true),
(9, '2018-09-22 19:10:08.599000', '2018-09-22 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Learn spanish', 4, false),
(10, '2018-09-23 19:10:08.599000', '2018-09-23 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Bungee jumping', 5, false),
(11, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Give a speech', 5, true),
(12, '2018-09-15 19:10:08.599000', '2018-09-15 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Bike 20 miles', 6, true),
(13, '2018-09-16 19:10:08.599000', '2018-09-16 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Eat pancakes for supper', 6, false),
(14, '2018-09-17 19:10:08.599000', '2018-09-17 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Sleep for 72 hours straight', 7, true),
(15, '2018-09-18 19:10:08.599000', '2018-09-18 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Drive to Montreal', 8, true),
(16, '2018-09-19 19:10:08.599000', '2018-09-19 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Drive to Alberta', 8, true),
(17, '2018-09-20 19:10:08.599000', '2018-09-20 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Get someone to skydive with', 9, false),
(18, '2018-09-21 19:10:08.599000', '2018-09-21 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Skydive', 9, false),
(19, '2018-09-22 19:10:08.599000', '2018-09-22 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Eat a lot of ice cream', 10, true),
(20, '2018-09-23 19:10:08.599000', '2018-09-23 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Hike a mountain', 10, false),
(21, '2018-09-24 19:10:08.599000', '2018-09-24 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Climb a building', 11, false),
(22, '2018-09-25 19:10:08.599000', '2018-09-25 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Learn to code', 11, false),
(23, '2018-09-26 19:10:08.599000', '2018-09-26 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Swim a mile', 12, false),
(24, '2018-09-27 19:10:08.599000', '2018-09-27 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Fly a plane', 13, false);


INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES
(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 1, 3),
(2, '2018-09-15 19:10:08.599000', '2018-09-15 19:10:08.599000', 2, 11),
(3, '2018-09-16 19:10:08.599000', '2018-09-16 19:10:08.599000', 3, 5),
(4, '2018-09-17 19:10:08.599000', '2018-09-17 19:10:08.599000', 4, 6),
(5, '2018-09-18 19:10:08.599000', '2018-09-18 19:10:08.599000', 5, 8),
(6, '2018-09-19 19:10:08.599000', '2018-09-19 19:10:08.599000', 6, 1),
(7, '2018-09-20 19:10:08.599000', '2018-09-20 19:10:08.599000', 7, 2),
(8, '2018-09-21 19:10:08.599000', '2018-09-21 19:10:08.599000', 4, 3),
(9, '2018-09-22 19:10:08.599000', '2018-09-22 19:10:08.599000', 2, 12);
