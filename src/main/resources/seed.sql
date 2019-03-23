/*
  Login credentials (username/email/password) for all users:
      admin/admin@example.com/password
      user1/user1@example.com/password
      user2/user2@example.com/password
      user3/user3@example.com/password
      user4/user4@example.com/password
      user5/user5@example.com/password
      user6/user6@example.com/password

  The following SQL script is a seed script used to generate mock data in the Kick the Bucket database for demonstration purposes.

  It will generate 7 physical addresses, 7 users (1 admin, 6 users), 11 user-to-user relationships, 13 buckets, 24 items,
   and 9 user-bucket relationships into the database.

  To execute this script and populate the database, the corresponding shell script can be executed, or this entire file
   can be copied and pasted into the Kick the Bucket SQL query processor on the hosted back-end server.
 */

DELETE FROM items;
DELETE FROM users_bucket_relationships;
DELETE FROM buckets;
DELETE FROM users_relationships;
DELETE FROM users;
DELETE FROM physical_addresses;


INSERT INTO physical_addresses VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'CANADA', 'A1B2C3', 'New Brunswick', '252 Example Street', '', 'Fredericton');
INSERT INTO physical_addresses VALUES(2, '2018-09-15 19:10:08.599000', '2018-09-15 19:10:08.599000', 'CANADA', 'A1B2C3', 'New Brunswick', '253 Example Street', '', 'Fredericton');
INSERT INTO physical_addresses VALUES(3, '2018-09-16 19:10:08.599000', '2018-09-16 19:10:08.599000', 'CANADA', 'A1B2C3', 'New Brunswick', '254 Example Street', '', 'Fredericton');
INSERT INTO physical_addresses VALUES(4, '2018-09-17 19:10:08.599000', '2018-09-17 19:10:08.599000', 'CANADA', 'A1B2C3', 'New Brunswick', '255 Example Street', '', 'Fredericton');
INSERT INTO physical_addresses VALUES(5, '2018-09-18 19:10:08.599000', '2018-09-18 19:10:08.599000', 'CANADA', 'A1B2C3', 'New Brunswick', '256 Example Street', '', 'Fredericton');
INSERT INTO physical_addresses VALUES(6, '2018-09-19 19:10:08.599000', '2018-09-19 19:10:08.599000', 'CANADA', 'A1B2C3', 'New Brunswick', '257 Example Street', '', 'Fredericton');
INSERT INTO physical_addresses VALUES(7, '2018-09-20 19:10:08.599000', '2018-09-20 19:10:08.599000', 'CANADA', 'A1B2C3', 'New Brunswick', '258 Example Street', '', 'Fredericton');

INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am an admin', 'admin@example.com', 'Admin', 'One', '', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'ADMIN', 'admin', 1);
INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(2, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a user', 'user1@example.com', 'User', 'One', '', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'USER', 'user1', 2);
INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(3, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a user', 'user2@example.ca', 'User', 'Two', '', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'USER', 'user2', 3);
INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(4, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a user', 'user3@example.com', 'User', 'Three', '', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'USER', 'user3', 4);
INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(5, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a user', 'user4@example.com', 'User', 'Four', '', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'USER', 'user4', 5);
INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(6, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a user', 'user5@example.com', 'User', 'Five', '', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'USER', 'user5', 6);
INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(7, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a user', 'user6@example.com', 'User', 'Six', '', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'USER', 'user6', 7);

INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 1, 2);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(2, '2018-09-15 19:10:08.599000', '2018-09-15 19:10:08.599000', 2, 1);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(3, '2018-09-16 19:10:08.599000', '2018-09-16 19:10:08.599000', 2, 3);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(4, '2018-09-17 19:10:08.599000', '2018-09-17 19:10:08.599000', 5, 3);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(5, '2018-09-18 19:10:08.599000', '2018-09-18 19:10:08.599000', 1, 3);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(6, '2018-09-19 19:10:08.599000', '2018-09-19 19:10:08.599000', 6, 3);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(7, '2018-09-20 19:10:08.599000', '2018-09-20 19:10:08.599000', 7, 3);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(8, '2018-09-21 19:10:08.599000', '2018-09-21 19:10:08.599000', 3, 5);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(9, '2018-09-22 19:10:08.599000', '2018-09-22 19:10:08.599000', 1, 4);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(10, '2018-09-23 19:10:08.599000', '2018-09-23 19:10:08.599000', 4, 7);
INSERT INTO users_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(11, '2018-09-24 19:10:08.599000', '2018-09-24 19:10:08.599000', 7, 1);


INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Admin does fun stuff!', true, 'Admin''s bucket', 1);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(2, '2018-09-15 19:10:08.599000', '2018-09-15 19:10:08.599000', 'User 1''s important list of things to do.', true, 'User 1''s bucket', 2);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(3, '2018-09-16 19:10:08.599000', '2018-09-16 19:10:08.599000', 'User 2''s bucket list', true, 'User 2''s bucket', 3);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(4, '2018-09-17 19:10:08.599000', '2018-09-17 19:10:08.599000', 'User 3''s art bucket', false, 'User 3''s bucket', 4);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(5, '2018-09-18 19:10:08.599000', '2018-09-18 19:10:08.599000', 'User 4''s travelling bucket', true, 'User 4''s bucket', 5);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(6, '2018-09-19 19:10:08.599000', '2018-09-19 19:10:08.599000', 'User 5''s bucket', false, 'User 5''s bucket', 6);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(7, '2018-09-20 19:10:08.599000', '2018-09-20 19:10:08.599000', 'User 6''s bucket', true, 'User 6''s bucket', 7);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(8, '2018-09-21 19:10:08.599000', '2018-09-21 19:10:08.599000', 'User 2''s trip bucket', true, 'User 2''s trip bucket', 3);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(9, '2018-09-22 19:10:08.599000', '2018-09-22 19:10:08.599000', 'Admin''s skydiving bucket', true, 'Admin''s skydiving bucket', 1);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(10, '2018-09-23 19:10:08.599000', '2018-09-23 19:10:08.599000', 'User 2''s bucket', false, 'User 2''s other bucket', 3);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(11, '2018-09-24 19:10:08.599000', '2018-09-24 19:10:08.599000', 'User 5''s bucket', true, 'User 5''s new bucket', 6);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(12, '2018-09-25 19:10:08.599000', '2018-09-25 19:10:08.599000', 'User 3''s bucket', true, 'User 3''s crazy bucket', 4);
INSERT INTO buckets(id, created_at, updated_at, description, is_public, name, owner_id) VALUES(13, '2018-09-26 19:10:08.599000', '2018-09-26 19:10:08.599000', 'User 4''s bucket', true, 'User 4''s other bucket', 5);


INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Go skydiving', 10, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(2, '2018-09-15 19:10:08.599000', '2018-09-15 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Go running', 10, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(3, '2018-09-16 19:10:08.599000', '2018-09-16 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Go driving', 10, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(4, '2018-09-17 19:10:08.599000', '2018-09-17 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Eat good food', 1, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(5, '2018-09-18 19:10:08.599000', '2018-09-18 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Meet a new friend', 1, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(6, '2018-09-19 19:10:08.599000', '2018-09-19 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Get healthy groceries for a month', 2, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(7, '2018-09-20 19:10:08.599000', '2018-09-20 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Travel somewhere new', 2, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(8, '2018-09-21 19:10:08.599000', '2018-09-21 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Learn french', 3, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(9, '2018-09-22 19:10:08.599000', '2018-09-22 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Learn spanish', 4, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(10, '2018-09-23 19:10:08.599000', '2018-09-23 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Bungee jumping', 5, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(11, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Give a speech', 5, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(12, '2018-09-15 19:10:08.599000', '2018-09-15 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Bike 20 miles', 6, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(13, '2018-09-16 19:10:08.599000', '2018-09-16 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Eat pancakes for supper', 6, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(14, '2018-09-17 19:10:08.599000', '2018-09-17 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Sleep for 72 hours straight', 7, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(15, '2018-09-18 19:10:08.599000', '2018-09-18 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Drive to Montreal', 8, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(16, '2018-09-19 19:10:08.599000', '2018-09-19 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Drive to Alberta', 8, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(17, '2018-09-20 19:10:08.599000', '2018-09-20 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Get someone to skydive with', 9, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(18, '2018-09-21 19:10:08.599000', '2018-09-21 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Skydive', 9, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(19, '2018-09-22 19:10:08.599000', '2018-09-22 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Eat a lot of ice cream', 10, true);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(20, '2018-09-23 19:10:08.599000', '2018-09-23 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Hike a mountain', 10, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(21, '2018-09-24 19:10:08.599000', '2018-09-24 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Climb a building', 11, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(22, '2018-09-25 19:10:08.599000', '2018-09-25 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Learn to code', 11, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(23, '2018-09-26 19:10:08.599000', '2018-09-26 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Swim a mile', 12, false);
INSERT INTO items(id, created_at, updated_at, description, link, name, parent_id, is_complete) VALUES(24, '2018-09-27 19:10:08.599000', '2018-09-27 19:10:08.599000', 'Enjoy it.', 'kickthebucket.net', 'Fly a plane', 13, false);


INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 1, 3);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(2, '2018-09-15 19:10:08.599000', '2018-09-15 19:10:08.599000', 2, 11);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(3, '2018-09-16 19:10:08.599000', '2018-09-16 19:10:08.599000', 3, 5);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(4, '2018-09-17 19:10:08.599000', '2018-09-17 19:10:08.599000', 4, 6);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(5, '2018-09-18 19:10:08.599000', '2018-09-18 19:10:08.599000', 5, 8);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(6, '2018-09-19 19:10:08.599000', '2018-09-19 19:10:08.599000', 6, 1);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(7, '2018-09-20 19:10:08.599000', '2018-09-20 19:10:08.599000', 7, 2);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(8, '2018-09-21 19:10:08.599000', '2018-09-21 19:10:08.599000', 4, 3);
INSERT INTO users_bucket_relationships(id, created_at, updated_at, follower_id, following_id) VALUES(9, '2018-09-22 19:10:08.599000', '2018-09-22 19:10:08.599000', 2, 12);
