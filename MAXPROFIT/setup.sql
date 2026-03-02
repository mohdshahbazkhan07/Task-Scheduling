-- =============================================
-- MAXPROFIT Predictive Scheduler - Database Setup
-- Run this on your 'maxprofit' PostgreSQL database
-- =============================================

-- 1. Drop existing tables if needed (CAREFUL!)
DROP TABLE IF EXISTS week_history;
DROP TABLE IF EXISTS projects;

-- 2. Create projects table with new columns
CREATE TABLE projects (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    deadline INT NOT NULL,
    revenue DOUBLE PRECISION NOT NULL,
    submission_day VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    actual_revenue DOUBLE PRECISION DEFAULT 0
);

-- 3. Create week_history table for tracking averages
CREATE TABLE week_history (
    id SERIAL PRIMARY KEY,
    week_number INT NOT NULL,
    year INT NOT NULL,
    total_profit DOUBLE PRECISION NOT NULL,
    projects_completed INT NOT NULL,
    week_start_date DATE
);

-- =============================================
-- DUMMY DATA: 8 weeks of history (~ 2 months)
-- Current date: 2026-02-25 (Wednesday)
-- =============================================

-- Week 1: Jan 5-9, 2026
INSERT INTO week_history (week_number, year, total_profit, projects_completed, week_start_date)
VALUES (2, 2026, 185000, 4, '2026-01-05');

-- Week 2: Jan 12-16, 2026
INSERT INTO week_history (week_number, year, total_profit, projects_completed, week_start_date)
VALUES (3, 2026, 210000, 5, '2026-01-12');

-- Week 3: Jan 19-23, 2026
INSERT INTO week_history (week_number, year, total_profit, projects_completed, week_start_date)
VALUES (4, 2026, 165000, 3, '2026-01-19');

-- Week 4: Jan 26-30, 2026
INSERT INTO week_history (week_number, year, total_profit, projects_completed, week_start_date)
VALUES (5, 2026, 240000, 5, '2026-01-26');

-- Week 5: Feb 2-6, 2026
INSERT INTO week_history (week_number, year, total_profit, projects_completed, week_start_date)
VALUES (6, 2026, 195000, 4, '2026-02-02');

-- Week 6: Feb 9-13, 2026
INSERT INTO week_history (week_number, year, total_profit, projects_completed, week_start_date)
VALUES (7, 2026, 220000, 5, '2026-02-09');

-- Week 7: Feb 16-20, 2026 (last week)
INSERT INTO week_history (week_number, year, total_profit, projects_completed, week_start_date)
VALUES (8, 2026, 175000, 4, '2026-02-16');

-- Week 8: Feb 23-27, 2026 (this week - in progress)
INSERT INTO week_history (week_number, year, total_profit, projects_completed, week_start_date)
VALUES (9, 2026, 0, 0, '2026-02-23');

-- =============================================
-- DUMMY PROJECTS: Mix of pending, completed, carried_over
-- =============================================

-- Completed projects from previous weeks
INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue)
VALUES ('Mobile App UI Design', 3, 50000, 'monday', 'completed', 48000);

INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue)
VALUES ('Backend API Development', 4, 80000, 'tuesday', 'completed', 82000);

INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue)
VALUES ('Database Optimization', 2, 35000, 'wednesday', 'completed', 35000);

INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue)
VALUES ('Cloud Deployment', 5, 60000, 'monday', 'completed', 55000);

INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue)
VALUES ('Testing & QA', 3, 40000, 'thursday', 'completed', 42000);

-- Currently pending projects (to be scheduled this/next week)
INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue)
VALUES ('E-Commerce Website', 7, 95000, 'monday', 'pending', 0);

INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue)
VALUES ('Payment Gateway Integration', 5, 70000, 'tuesday', 'pending', 0);

INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue)
VALUES ('React Dashboard', 4, 55000, 'wednesday', 'pending', 0);

INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue)
VALUES ('SEO Optimization', 3, 30000, 'monday', 'pending', 0);

INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue)
VALUES ('DevOps Pipeline Setup', 6, 75000, 'tuesday', 'pending', 0);

INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue)
VALUES ('AI Chatbot Module', 8, 110000, 'wednesday', 'pending', 0);

INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue)
VALUES ('Inventory Management System', 10, 85000, 'thursday', 'pending', 0);

-- Carried over from last week
INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue)
VALUES ('Security Audit Report', 4, 45000, 'friday', 'carried_over', 0);

INSERT INTO projects (title, deadline, revenue, submission_day, status, actual_revenue)
VALUES ('Data Migration Script', 6, 65000, 'thursday', 'carried_over', 0);
