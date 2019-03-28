/**
This SQL script is used to initialize the database with the AWS CodeDeploy administrator user.

The administrator user is used by CodeDeploy to interface with the server. In the future, this script can be used for other DB initialization tasks.

At the moment, this script is configured to run only when the production profile is enabled (spring.datasource.initialization-mode = always).
*/

DELETE FROM users WHERE username = 'codedeployadmin';
INSERT INTO users(bio, email, first_name, last_name, role, username, password, created_at, updated_at) VALUES (
  'CODE DEPLOY ADMIN',
  'cdadmin@ktb.brandonrichardson.ca',
  'CODE DEPLOY ADMIN',
  'CODE DEPLOY ADMIN',
  'ROLE_ADMIN',
  'codedeployadmin',
  '$2a$10$8EQG.AbSzGpcieOqapHB5eq1wmDEKKuyBt69woadwa/RNH9hRnc7S',
  current_timestamp,
  current_timestamp);