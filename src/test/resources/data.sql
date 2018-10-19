DELETE FROM users;

INSERT INTO users(id, created_at, updated_at, bio, email, first_name, last_name, middle_name, password, role, username, user_address_id) VALUES(1, '2018-09-14 19:10:08.599000', '2018-09-14 19:10:08.599000', 'I am a person', 'test@test.com', 'TestFirst', 'TestLast', 'TestMiddle', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'USER', 'TestUser', NULL);