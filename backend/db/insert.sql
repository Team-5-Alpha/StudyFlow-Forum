USE studyflow_forum;

INSERT INTO users (first_name, last_name, username, email, password, role, phone_number, profile_photo_url)
VALUES
    ('Ivan', 'Petrov', 'ivanp', 'ivan@example.com', 'pass123', 'USER', '0888123456', NULL),
    ('Maria', 'Georgieva', 'mgeorgieva', 'maria@example.com', 'pass123', 'USER', NULL, NULL),
    ('Todor', 'Krushkov', 'krushkov', 'krushkov@example.com', 'pass123', 'ADMIN', '0888999000', NULL),
    ('Nikolay', 'Dimitrov', 'niko_d', 'niko@example.com', 'pass123', 'USER', NULL, NULL),
    ('Elena', 'Koleva', 'ekoleva', 'elena@example.com', 'pass123', 'USER', NULL, NULL);

INSERT INTO posts (title, content, author_id)
VALUES
    ('Understanding Java Streams in Depth', 'Long explanation about Java Streams...', 1),
    ('How to Prepare for Telerik Java Exam', 'Study tips, resources, examples...', 3),
    ('Why REST APIs Need Proper Validation', 'Detailed article about validations...', 2),
    ('Spring Boot Pagination Explained', 'Guide about pageable, sorting...', 4),
    ('Best Practices for Database Indexing', 'Important database tuning techniques...', 1);

INSERT INTO comments (content, author_id, post_id, parent_comment_id)
VALUES
    ('Great explanation, thanks!', 2, 1, NULL),
    ('Can you give an example?', 4, 1, 1),
    ('Very useful article!', 1, 2, NULL),
    ('I needed this, thanks!', 5, 3, NULL),
    ('Good job!', 3, 4, NULL),
    ('What about composite indexes?', 2, 5, NULL),
    ('Following up – include examples please.', 4, 5, 6);

INSERT INTO tags (name)
VALUES
    ('java'),
    ('spring'),
    ('database'),
    ('rest'),
    ('validation');

INSERT INTO post_tags (post_id, tag_id)
VALUES
    (1, 1),
    (1, 2),
    (2, 1),
    (3, 4),
    (3, 5),
    (4, 2),
    (5, 3);

INSERT INTO likes_posts (user_id, post_id)
VALUES
    (2, 1),
    (4, 1),
    (1, 2),
    (5, 3),
    (3, 4),
    (1, 5);

INSERT INTO likes_comments (user_id, comment_id)
VALUES
    (1, 1),
    (3, 2),
    (4, 3),
    (2, 4),
    (5, 5),
    (1, 6);

INSERT INTO follows (follower_id, followed_id)
VALUES
    (1, 3),
    (2, 1),
    (4, 1),
    (5, 3),
    (3, 1);

INSERT INTO notifications (entity_id, entity_type, action_type, recipient_id, actor_id)
VALUES
    (1, 'POST', 'LIKE', 1, 2),
    (3, 'COMMENT', 'REPLY', 1, 4),
    (2, 'POST', 'COMMENT', 3, 1),
    (4, 'POST', 'LIKE', 2, 5),
    (5, 'COMMENT', 'LIKE', 3, 1);