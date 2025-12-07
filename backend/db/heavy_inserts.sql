USE studyflow_forum;

INSERT INTO users (first_name, last_name, username, email, password, role, phone_number, profile_photo_url)
VALUES
    ('Ivan', 'Petrov', 'ivanp', 'ivan@example.com', 'pass1234', 'USER', '0888123456', NULL),
    ('Maria', 'Georgieva', 'mgeorgieva', 'maria@example.com', 'pass1234', 'USER', NULL, NULL),
    ('Todor', 'Krushkov', 'krushkov', 'krushkov@example.com', 'pass1234', 'ADMIN', '0888999000', NULL),
    ('Nikolay', 'Dimitrov', 'niko_d', 'niko@example.com', 'pass1234', 'USER', NULL, NULL),
    ('Elena', 'Koleva', 'ekoleva', 'elena@example.com', 'pass1234', 'USER', NULL, NULL),

    ('Petar','Stoyanov','p_stoy','petar@example.com','pass1234','USER',NULL,NULL),
    ('Kristina','Vasileva','kvas','kvas@example.com','pass1234','USER',NULL,NULL),
    ('Dimitar','Iliev','dido','dido@example.com','pass1234','USER',NULL,NULL),
    ('Aleks','Dobrev','adobrev','adobrev@example.com','pass1234','USER',NULL,NULL),
    ('Slavena','Nikolova','slavena_n','slavena@example.com','pass1234','USER',NULL,NULL),

    ('Mihail','Krastev','misho_k','misho@example.com','pass1234','USER',NULL,NULL),
    ('Victoria','Marinova','vmarinova','viki@example.com','pass1234','USER',NULL,NULL),
    ('Rumen','Kolev','kolev_r','kolev_r@example.com','pass1234','USER',NULL,NULL),
    ('Stefan','Yordanov','stefan_y','stefan@example.com','pass1234','USER',NULL,NULL),
    ('Polina','Yaneva','polly_y','polina@example.com','pass1234','USER',NULL,NULL),

    ('Asencho','Hristov','ahristov','asen@example.com','pass1234','USER',NULL,NULL),
    ('Iliana','Todorova','ili_t','ili@example.com','pass1234','USER',NULL,NULL),
    ('Martin','Gochev','martin_g','gochev@example.com','pass1234','USER',NULL,NULL),
    ('Stela','Popova','stel4e','stela@example.com','pass1234','USER',NULL,NULL),
    ('Georgi','Valkov','gvv','gvalkov@example.com','pass1234','USER',NULL,NULL);

INSERT INTO posts (title, content, author_id)
VALUES
    ('Understanding Java Streams in Depth and Their Practical Use',
     'Long explanation about Java Streams, covering intermediate operations, terminal operations, lazy evaluation and real-world patterns in modern applications...',
     1),

    ('How to Prepare for the Telerik Java Exam Like a Professional',
     'Study tips, resources, examples and structured preparation strategies used by top-performing students, including patterns, pitfalls and memory tricks...',
     3),

    ('Why REST APIs Need Proper Validation for Stable Systems',
     'Detailed article about validations, error handling conventions, security concerns and why validation layers save projects from absolute chaos...',
     2),

    ('Spring Boot Pagination Explained in a Practical and Simple Way',
     'Guide about pageable, sorting, slicing, limit-offset techniques and structuring data-heavy endpoints for optimal performance...',
     4),

    ('Best Practices for Database Indexing in High-Traffic Systems',
     'Important database tuning techniques, covering index design, B-tree behavior, composite keys, and query optimization scenarios...',
     1),

    ('Understanding JPA Fetch Types with Real Examples and Caveats',
     'Lazy vs eager loading explained with concrete cases, performance notes, N+1 problem illustrations and architectural recommendations...',
     6),

    ('Guide to Clean Architecture for Scalable Software Projects',
     'Breaking dependency rules, organizing application layers and applying principles that make systems easier to test and maintain...',
     7),

    ('Spring Security for Beginners and Auth Flow Overview',
     'Authentication, filters, chains, exceptions and realistic examples of securing different API endpoints in modern applications...',
     8),

    ('How Threads Work in Java and Why Concurrency Matters Today',
     'Concurrency basics and examples, race conditions, thread safety, locking, executor services and real-world usage patterns...',
     9),

    ('Mastering Git Flow for Team Collaboration and Stability',
     'Branching patterns and workflows, pull request discipline, versioning strategies and team development best practices...',
     10),

    ('RESTful URL Design and Why It Matters for Maintainable APIs',
     'Best practices for API paths, resource modeling, verb avoidance, link clarity and long-term service stability considerations...',
     11),

    ('Handling Exceptions in Spring Using Controller Advice',
     'Controller advice patterns, exception hierarchies, meaningful messages and improving debugging experience for developers...',
     9),

    ('Writing Effective Unit Tests with JUnit and Mockito',
     'Techniques for improving code reliability, isolating dependencies and building maintainable test suites...',
     13),

    ('SSH for Developers and Secure Workflow Practices',
     'Basics of secure connection, key management, tunneling and automation techniques used in DevOps...',
     14),

    ('Deploying Spring Boot Apps with Docker and CI/CD Pipelines',
     'Docker strategies, pipeline steps, environment promotion, container optimization and deployment traps to avoid...',
     15),

    ('Scaling Web Apps Through Load Balancing and Caching',
     'Load balancing, caching, horizontal scaling techniques, and architectural considerations for high availability systems...',
     16),

    ('Why You Should Use DTOs to Protect Your Application',
     'Decoupling and validation strategies, transport boundaries, mapping challenges and maintainability improvements...',
     17),

    ('Understanding SQL Joins and Their Impact on Performance',
     'Examples of joins, indexes, cardinality, execution plans and practical querying advice for developers...',
     18),

    ('Microservices: Good or Bad? Understanding the Tradeoffs',
     'Tradeoffs and complexity, communication overhead, failure isolation, organizational impact and when monoliths are better...',
     19),

    ('Java Collections Deep Dive for Developers Who Want Control',
     'HashMap, TreeMap, List structures, performance notes, internal implementation details and optimization best practices...',
     20);

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