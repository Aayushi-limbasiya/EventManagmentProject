

-- drop table roles;
-- drop table users;
-- drop table approvals;
-- drop table certificates;
-- drop table event_schedule;
-- drop table events;
-- drop table feedback;
-- drop table notifications;
-- drop table payments;
-- drop table registrations;
-- drop table venues;


CREATE DATABASE EventManagement;
USE eventmanagment;
SHOW DATABASES;


CREATE TABLE roles (
    role_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users (
    user_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id INT UNSIGNED, -- Must match the type and sign of roles.role_id
    phone VARCHAR(15),
    dob DATE,
    age INT,
    gender VARCHAR(10),
    address TEXT,
    profile_photo VARCHAR(255),
    organization_name VARCHAR(150),
    organization_type VARCHAR(100),
    organization_address TEXT,
    verified_status VARCHAR(20) DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE events (
    event_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    user_id INT UNSIGNED, -- Matches users.user_id
    status VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_organizer FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE venues (
    venue_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    location VARCHAR(200)
);

CREATE TABLE event_schedule (
    schedule_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    event_id INT UNSIGNED, -- Matches events.event_id
    venue_id INT UNSIGNED, -- Matches venues.venue_id
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    capacity INT,
    CONSTRAINT fk_schedule_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT fk_schedule_venue FOREIGN KEY (venue_id) REFERENCES venues(venue_id) ON DELETE CASCADE
);

CREATE TABLE registrations (
    registration_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    event_id INT UNSIGNED,
    user_id INT UNSIGNED,
    status VARCHAR(20),
    attendance_status VARCHAR(20),
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reg_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT fk_reg_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE approvals (
    approval_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    event_id INT UNSIGNED,
    user_id INT UNSIGNED,
    decision VARCHAR(20),
    remark TEXT,
    decision_at TIMESTAMP,
    CONSTRAINT fk_approval_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT fk_approval_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE feedback (
    feedback_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    event_id INT UNSIGNED,
    user_id INT UNSIGNED,
    rating INT,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_feedback_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE notifications (
    notification_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNSIGNED,
    message TEXT NOT NULL,
    type VARCHAR(30),
    channel VARCHAR(20),
    status VARCHAR(20),
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE payments (
    payment_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    registration_id INT UNSIGNED,
    amount DECIMAL(10,2),
    payment_method VARCHAR(50),
    payment_status VARCHAR(20),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_reg FOREIGN KEY (registration_id) REFERENCES registrations(registration_id) ON DELETE CASCADE
);

CREATE TABLE certificates (
    certificate_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    registration_id INT UNSIGNED,
    certificate_number VARCHAR(100) UNIQUE,
    issue_date DATE,
    certificate_file VARCHAR(255),
    CONSTRAINT fk_cert_reg FOREIGN KEY (registration_id) REFERENCES registrations(registration_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS auth_tokens (
    token_id     INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    user_id      INT UNSIGNED NOT NULL,
    token        TEXT NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at   TIMESTAMP NOT NULL,
    is_revoked   TINYINT(1) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Add reset token columns to users table for forgot password
ALTER TABLE users
    ADD COLUMN reset_token VARCHAR(255) DEFAULT NULL,
    ADD COLUMN reset_token_expiry TIMESTAMP DEFAULT NULL;














