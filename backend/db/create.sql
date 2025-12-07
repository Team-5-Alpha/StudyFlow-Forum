DROP DATABASE IF EXISTS studyflow_forum;
CREATE DATABASE studyflow_forum CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE studyflow_forum;

CREATE TABLE users
(
    user_id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    username VARCHAR(32) NOT NULL UNIQUE,
    email VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role              ENUM ('USER','ADMIN') DEFAULT 'USER',
    phone_number      VARCHAR(32) UNIQUE,
    profile_photo_url VARCHAR(255),
    is_blocked        BOOLEAN               DEFAULT FALSE,
    created_at        DATETIME              DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE posts
(
    post_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(64) NOT NULL,
    content TEXT NOT NULL,
    is_deleted BOOLEAN  DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    author_id  BIGINT       NOT NULL,
    CONSTRAINT fk_posts_author
        FOREIGN KEY (author_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE comments
(
    comment_id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    content           TEXT   NOT NULL,
    is_deleted        BOOLEAN  DEFAULT FALSE,
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    author_id         BIGINT NOT NULL,
    post_id           BIGINT NOT NULL,
    parent_comment_id BIGINT,
    CONSTRAINT fk_comments_author
        FOREIGN KEY (author_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_post
        FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_parent
        FOREIGN KEY (parent_comment_id) REFERENCES comments (comment_id) ON DELETE SET NULL
);

CREATE TABLE tags
(
    tag_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE post_tags
(
    post_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_post_tags_post
        FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE,
    CONSTRAINT fk_post_tags_tag
        FOREIGN KEY (tag_id) REFERENCES tags (tag_id) ON DELETE CASCADE
);

CREATE TABLE likes_posts
(
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, post_id),
    CONSTRAINT fk_likes_posts_user
        FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_likes_posts_post
        FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE
);

CREATE TABLE likes_comments
(
    user_id    BIGINT NOT NULL,
    comment_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, comment_id),
    CONSTRAINT fk_likes_comments_user
        FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_likes_comments_comment
        FOREIGN KEY (comment_id) REFERENCES comments (comment_id) ON DELETE CASCADE
);

CREATE TABLE follows
(
    follower_id BIGINT NOT NULL,
    followed_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, followed_id),
    CONSTRAINT fk_follows_follower
        FOREIGN KEY (follower_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_follows_followed
        FOREIGN KEY (followed_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT chk_no_self_follow
        CHECK (follower_id <> followed_id)
);

CREATE TABLE notifications
(
    notification_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id    BIGINT      NOT NULL,
    entity_type  VARCHAR(32) NOT NULL,
    action_type  VARCHAR(32) NOT NULL,
    is_read      BOOLEAN  DEFAULT FALSE,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    recipient_id BIGINT      NOT NULL,
    actor_id     BIGINT      NOT NULL,
    CONSTRAINT fk_notifications_recipient
        FOREIGN KEY (recipient_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_actor
        FOREIGN KEY (actor_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE INDEX idx_posts_author ON posts (author_id);
CREATE INDEX idx_comments_post ON comments (post_id);
CREATE INDEX idx_notifications_recipient ON notifications (recipient_id);
CREATE INDEX idx_notifications_actor ON notifications (actor_id);
CREATE INDEX idx_posts_created_at ON posts (created_at);
CREATE INDEX idx_comments_created_at ON comments (created_at);