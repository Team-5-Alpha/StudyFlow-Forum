USE studyflow_forum;

INSERT INTO users (first_name, last_name, username, email, password, role, phone_number, profile_photo_url)
VALUES
    ('Ivan', 'Petrov', 'ivanp', 'ivan@example.com', 'pass123', 'USER', '0888123456', NULL),
    ('Maria', 'Georgieva', 'mgeorgieva', 'maria@example.com', 'pass123', 'USER', NULL, NULL),
    ('Todor', 'Krushkov', 'krushkov', 'krushkov@example.com', 'pass123', 'ADMIN', '0888999000', NULL),
    ('Nikolay', 'Dimitrov', 'niko_d', 'niko@example.com', 'pass123', 'USER', NULL, NULL),
    ('Elena', 'Koleva', 'ekoleva', 'elena@example.com', 'pass123', 'USER', NULL, NULL),

    ('Petar','Stoyanov','p_stoy','petar@example.com','pass123','USER',NULL,NULL),
    ('Kristina','Vasileva','kvas','kvas@example.com','pass123','USER',NULL,NULL),
    ('Dimitar','Iliev','dido','dido@example.com','pass123','USER',NULL,NULL),
    ('Aleks','Dobrev','adobrev','adobrev@example.com','pass123','USER',NULL,NULL),
    ('Slavena','Nikolova','slavena_n','slavena@example.com','pass123','USER',NULL,NULL),

    ('Mihail','Krastev','misho_k','misho@example.com','pass123','USER',NULL,NULL),
    ('Victoria','Marinova','vmarinova','viki@example.com','pass123','USER',NULL,NULL),
    ('Rumen','Kolev','kolev_r','kolev_r@example.com','pass123','USER',NULL,NULL),
    ('Stefan','Yordanov','stefan_y','stefan@example.com','pass123','USER',NULL,NULL),
    ('Polina','Yaneva','polly_y','polina@example.com','pass123','USER',NULL,NULL),

    ('Asen','Hristov','ahristov','asen@example.com','pass123','USER',NULL,NULL),
    ('Iliana','Todorova','ili_t','ili@example.com','pass123','USER',NULL,NULL),
    ('Martin','Gochev','martin_g','gochev@example.com','pass123','USER',NULL,NULL),
    ('Stela','Popova','stel4e','stela@example.com','pass123','USER',NULL,NULL),
    ('Georgi','Valkov','gvv','gvalkov@example.com','pass123','USER',NULL,NULL);

INSERT INTO posts (title, content, author_id)
VALUES
    ('Understanding Java Streams in Depth', 'Long explanation about Java Streams...', 1),
    ('How to Prepare for Telerik Java Exam', 'Study tips, resources, examples...', 3),
    ('Why REST APIs Need Proper Validation', 'Detailed article about validations...', 2),
    ('Spring Boot Pagination Explained', 'Guide about pageable, sorting...', 4),
    ('Best Practices for Database Indexing', 'Important database tuning techniques...', 1),

    ('Understanding JPA Fetch Types', 'Lazy vs eager loading explained...', 6),
    ('Guide to Clean Architecture', 'Breaking dependency rules...', 7),
    ('Spring Security for Beginners', 'Authentication, filters, chains...', 8),
    ('How Threads Work in Java', 'Concurrency basics and examples...', 9),
    ('Mastering Git Flow', 'Branching patterns and workflows...', 10),

    ('RESTful URL Design', 'Best practices for API paths...', 11),
    ('Handling Exceptions in Spring', 'Controller advice patterns...', 12),
    ('Writing Effective Unit Tests', 'JUnit, Mockito, Testcontainers...', 13),
    ('SSH for Developers', 'Basics of secure connection...', 14),
    ('Deploying Spring Boot Apps', 'Docker, CI/CD pipelines...', 15),

    ('Scaling Web Applications', 'Load balancing, caching...', 16),
    ('Why You Should Use DTOs', 'Decoupling and validation...', 17),
    ('Understanding SQL Joins', 'Examples of joins, indexes...', 18),
    ('Microservices: Good or Bad?', 'Tradeoffs and complexity...', 19),
    ('Java Collections Deep Dive', 'HashMap, TreeMap, List...', 20);

INSERT INTO comments (content, author_id, post_id, parent_comment_id)
VALUES
    ('Great explanation, thanks!', 2, 1, NULL),
    ('Can you give an example?', 4, 1, 1),
    ('Very useful article!', 1, 2, NULL),
    ('I needed this, thanks!', 5, 3, NULL),
    ('Good job!', 3, 4, NULL),
    ('What about composite indexes?', 2, 5, NULL),
    ('Following up – include examples please.', 4, 5, 6),

    ('Nice breakdown!', 6, 6, NULL),
    ('More details please?', 7, 6, 8),
    ('This helps a lot!', 8, 7, NULL),
    ('Where do we use it?', 9, 7, 10),
    ('Great resource!', 10, 8, NULL),
    ('Needs more clarification.', 11, 9, NULL),
    ('Very technical!', 12, 10, NULL),
    ('Awesome write-up!', 13, 11, NULL),
    ('Explain more about filters.', 14, 12, NULL),
    ('Super helpful!', 15, 13, NULL),
    ('Can you show code?', 16, 14, NULL),
    ('This is gold!', 17, 15, NULL),
    ('I didn’t know this!', 18, 16, NULL),
    ('Epic!', 19, 17, NULL),
    ('Well explained.', 20, 18, NULL);

INSERT INTO tags (name)
VALUES
    ('java'),
    ('spring'),
    ('database'),
    ('rest'),
    ('validation'),
    ('testing'),
    ('clean-code'),
    ('architecture'),
    ('concurrency'),
    ('git');

INSERT INTO post_tags (post_id, tag_id)
VALUES
    (1, 1), (1, 2),
    (2, 1),
    (3, 4), (3, 5),
    (4, 2),
    (5, 3),

    (6, 1), (6, 2),
    (7, 8),
    (8, 2),
    (9, 9),
    (10, 10),

    (11, 4),
    (12, 2),
    (13, 6),
    (14, 10),
    (15, 8),

    (16, 9),
    (17, 5),
    (18, 3),
    (19, 1),
    (20, 7);

INSERT INTO likes_posts (user_id, post_id)
VALUES
    (2, 1), (4, 1),
    (1, 2),
    (5, 3),
    (3, 4),
    (1, 5),

    (6, 6), (7, 6),
    (8, 7),
    (9, 8),
    (10, 9),
    (11, 10),

    (12, 11),
    (13, 12),
    (14, 13),
    (15, 14),
    (16, 15),

    (17, 16),
    (18, 17),
    (19, 18),
    (20, 19),
    (1, 20);

INSERT INTO likes_comments (user_id, comment_id)
VALUES
    (1, 1),
    (3, 2),
    (4, 3),
    (2, 4),
    (5, 5),
    (1, 6),

    (6, 7),
    (7, 8),
    (8, 9),
    (9, 10),
    (10, 11),
    (11, 12),
    (12, 13),
    (13, 14),
    (14, 15),
    (15, 16);

INSERT INTO follows (follower_id, followed_id)
VALUES
    (1, 3),
    (2, 1),
    (4, 1),
    (5, 3),
    (3, 1),

    (6, 1),
    (7, 3),
    (8, 2),
    (9, 4),
    (10, 5),

    (11, 3),
    (12, 6),
    (13, 7),
    (14, 8),
    (15, 9),

    (16, 10),
    (17, 11),
    (18, 12),
    (19, 13),
    (20, 14);

INSERT INTO notifications (entity_id, entity_type, action_type, recipient_id, actor_id)
VALUES
    (1, 'POST', 'LIKE', 1, 2),
    (3, 'COMMENT', 'REPLY', 1, 4),
    (2, 'POST', 'COMMENT', 3, 1),
    (4, 'POST', 'LIKE', 2, 5),
    (5, 'COMMENT', 'LIKE', 3, 1),

    (6, 'POST', 'LIKE', 1, 7),
    (7, 'POST', 'LIKE', 3, 8),
    (8, 'COMMENT', 'REPLY', 2, 9),
    (9, 'POST', 'COMMENT', 4, 10),
    (10, 'POST', 'LIKE', 5, 6),

    (11, 'POST', 'LIKE', 6, 12),
    (12, 'COMMENT', 'LIKE', 7, 13),
    (14, 'POST', 'COMMENT', 9, 11),
    (16, 'POST', 'LIKE', 1, 15),
    (18, 'COMMENT', 'REPLY', 2, 16);