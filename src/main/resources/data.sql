--DO $$
--BEGIN
--    -- Add default Roles
--    IF NOT EXISTS (SELECT 1 FROM role WHERE role_name = 'ROLE_ADMIN') THEN
--        INSERT INTO role (id, role_name) VALUES (1, 'ROLE_ADMIN');
--    END IF;
--
--    IF NOT EXISTS (SELECT 1 FROM role WHERE role_name = 'ROLE_USER') THEN
--        INSERT INTO role (id, role_name) VALUES (2, 'ROLE_USER');
--    END IF;
--
--    -- Add default User with pwd "adminpwd"
--    IF NOT EXISTS (SELECT 1 FROM users WHERE id = 0) THEN
--        INSERT INTO users(id, account, defeats, email, first_name, house, last_name, login, password, wins) VALUES(0, 0, 0, 'admin@admin.com', 'adminFirst', 'Gryffindor', 'adminLast', 'admin', '$2a$10$o/.dihpewQeGn7LHs.Xd2uN1hCNvyq9v/VfbKdLdikBgc4IDD4/Zu', 0);
--    END IF;
--
--    -- Add default User - Role Association
--    IF NOT EXISTS (SELECT 1 FROM user_role WHERE user_id = 0) THEN
--        INSERT INTO user_role(user_id,role_id) VALUES(0,1);
--        INSERT INTO USER_ROLE(user_id,role_id) VALUES(0,2);
--    END IF;
--END $$;

INSERT INTO role (id, role_name) VALUES (1, 'ROLE_ADMIN') ON CONFLICT (role_name) DO NOTHING;
INSERT INTO role (id, role_name) VALUES (2, 'ROLE_USER') ON CONFLICT (role_name) DO NOTHING;

INSERT INTO users(id, account, defeats, email, first_name, house, last_name, login, password, wins)
VALUES(0, 0, 0, 'admin@admin.com', 'adminFirst', 'Gryffindor', 'adminLast', 'admin', '$2a$10$o/.dihpewQeGn7LHs.Xd2uN1hCNvyq9v/VfbKdLdikBgc4IDD4/Zu', 0) ON CONFLICT (id) DO NOTHING;

INSERT INTO user_role(user_id,role_id) VALUES(0,1) ON CONFLICT (user_id, role_id) DO NOTHING;
INSERT INTO USER_ROLE(user_id,role_id) VALUES(0,2) ON CONFLICT (user_id, role_id) DO NOTHING;
