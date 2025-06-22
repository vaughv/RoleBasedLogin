# 📚 Role-Based Login System for LMS

An advanced, full-stack **Role-Based Authentication System** for a **Learning Management System (LMS)**, featuring a **cyberpunk-inspired UI**, secure backend with **Spring Boot**, and persistent data handling with **MySQL**.

---

## 🚀 Features

- 🔐 Secure **Role-Based Authentication** (ADMIN, TEACHER, STUDENT, INSTRUCTOR)
- 🎨 Cyber-themed **interactive UI grid background**
- 📦 Built with **Spring Boot + Java + Jakarta**
- 🗂️ Database-backed session management
- ⚡ Glowing ripple effects on grid interactions
- ✨ Cleanly structured code with **Lombok annotations**
- 🧠 UI designed using **Figma**
- 📁 SQL schema included

---

## 🧰 Tech Stack

| Layer        | Tech Used                     |
|--------------|-------------------------------|
| Backend      | Java, Spring Boot, Jakarta    |
| UI/Frontend  | HTML5, CSS3, JavaScript       |
| UI Design    | Figma                         |
| Styling      | Custom CSS with filter/transition effects |
| ORM & Utility| JPA, Lombok                   |
| Database     | MySQL                         |

---

admin can not be created using register so use this in MySQL 

INSERT INTO users (email, name, password, role)
VALUES (
  'admin@example.com',
  'Admin User',
  '$2a$12$evMqGJ1qKt3a.21LYs7kNujPepS7jbXArW6O/tbg6iPjVYp/bUNxK',  >>>>> bCrypt password : 1234
  'ADMIN'
);
