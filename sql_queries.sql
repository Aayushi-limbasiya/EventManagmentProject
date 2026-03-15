create database EventManagment;

use  EventManagment;

CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INT references roles(role_id),
    phone VARCHAR(15) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE venues (
    venue_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    location VARCHAR(200) NOT NULL,
    status VARCHAR(30) NOT NULL
);

CREATE TABLE events (
    event_id SERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    organizer_id INT  REFERENCES users(user_id),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE event_schedule (
    schedule_id SERIAL PRIMARY KEY,
    event_id INT  REFERENCES events(event_id),
    venue_id INT  REFERENCES venues(venue_id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL
);

CREATE TABLE registration (
    registration_id SERIAL PRIMARY KEY,
    event_id INT REFERENCES events(event_id),
    student_id INT REFERENCES users(user_id),
    status VARCHAR(20) NOT NULL,
    registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE approval (
    approval_id SERIAL PRIMARY KEY,
    event_id INT  REFERENCES events(event_id),
    admin_id INT  REFERENCES users(user_id),
    decision VARCHAR(20) NOT NULL,
    remark TEXT,
    decision_at TIMESTAMP NOT NULL
);

CREATE TABLE feedback (
    feedback_id SERIAL PRIMARY KEY,
    event_id INT  REFERENCES events(event_id),
    user_id INT REFERENCES users(user_id),
    rating INT NOT NULL,
    comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notifications (
    notification_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id),
    message TEXT NOT NULL,
    type VARCHAR(20) NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payments (
    payment_id SERIAL PRIMARY KEY,
    registration_id INT REFERENCES registration(registration_id),
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50),
    payment_status VARCHAR(20),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE certificates (
    certificate_id SERIAL PRIMARY KEY,
    event_id INT REFERENCES events(event_id),
    user_id INT REFERENCES users(user_id),
    registration_id INT  REFERENCES registration(registration_id),
    certificate_number VARCHAR(100) UNIQUE,
    issue_date DATE,
    certificate_file VARCHAR(255)
);

ALTER TABLE registration
ADD COLUMN attendance_status VARCHAR(20);

drop table users;

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INT REFERENCES roles(role_id),
    phone VARCHAR(15) NOT NULL,
    department VARCHAR(100),
    profile_photo VARCHAR(255),
    dob DATE,
    age INT,
    gender VARCHAR(10),
    address TEXT,
    organization_name VARCHAR(150),
    organization_type VARCHAR(100),
    organization_address TEXT,
    verified_status VARCHAR(20) DEFAULT 'Pending',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

drop table notifications;

CREATE TABLE notifications (
    notification_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id),
    message TEXT NOT NULL,
    type VARCHAR(30),
    channel VARCHAR(20),  -- Email / SMS / App
    status VARCHAR(20),   -- Sent / Failed
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);