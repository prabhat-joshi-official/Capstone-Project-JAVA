# Online Examination System

## 📌 Project Overview

The **Online Examination System** is a desktop-based application developed using **Java** that allows administrators to create and manage examinations while enabling students to attempt exams digitally. The system automatically evaluates objective-type questions (MCQs) and generates instant results.

This project aims to simplify the examination process by providing a secure and efficient platform for conducting tests, managing questions, tracking student performance, and generating reports.

---

## 🎯 Objectives

* Provide a digital platform for conducting examinations.
* Automate the evaluation of objective-type questions.
* Maintain a centralized database for exams, questions, and results.
* Generate instant results and performance reports.
* Reduce manual effort involved in traditional examination systems.

---

## 🚀 Features

### Admin Features

* Admin login authentication
* Create and manage exams
* Add, edit, and delete questions
* Set exam duration
* View student results
* Export results as reports (PDF/CSV)

### Student Features

* Student registration and login
* View available exams
* Attempt exams with a timer
* Automatic submission when time expires
* Instant result display

### System Features

* Secure login system
* Automatic evaluation of MCQ questions
* Timer-based examination
* Database management for storing questions and results
* Performance analytics and reporting

---

## 🛠 Technology Stack

| Layer                 | Technology          |
| --------------------- | ------------------- |
| Programming Language  | Java                |
| User Interface        | JavaFX / Swing      |
| Database              | MySQL / SQLite      |
| Database Connectivity | JDBC                |
| Build Tool            | Maven               |
| Reporting             | JasperReports       |
| Charts & Analytics    | JFreeChart          |
| Packaging             | Launch4j / jpackage |

---

## 🗂 Project Structure

```
OnlineExamSystem/
│
├── src/
│   ├── model/
│   │   ├── User.java
│   │   ├── Exam.java
│   │   ├── Question.java
│   │   └── Result.java
│   │
│   ├── controller/
│   │   ├── LoginController.java
│   │   ├── ExamController.java
│   │   └── AdminController.java
│   │
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── QuestionDAO.java
│   │   └── ResultDAO.java
│   │
│   ├── utils/
│   │   ├── DBConnection.java
│   │   └── PasswordUtil.java
│   │
│   ├── view/
│   │   ├── login.fxml
│   │   ├── dashboard.fxml
│   │   └── exam.fxml
│   │
│   └── Main.java
│
└── resources/
```

---

## 🗄 Database Design

### Users Table

| Field    | Description        |
| -------- | ------------------ |
| id       | User ID            |
| name     | User name          |
| email    | User email         |
| password | Encrypted password |
| role     | Admin / Student    |

### Exams Table

| Field    | Description   |
| -------- | ------------- |
| id       | Exam ID       |
| title    | Exam title    |
| duration | Exam duration |

### Questions Table

| Field          | Description    |
| -------------- | -------------- |
| id             | Question ID    |
| exam_id        | Exam reference |
| question       | Question text  |
| optionA        | Option A       |
| optionB        | Option B       |
| optionC        | Option C       |
| optionD        | Option D       |
| correct_answer | Correct option |

### Results Table

| Field      | Description       |
| ---------- | ----------------- |
| id         | Result ID         |
| student_id | Student reference |
| exam_id    | Exam reference    |
| score      | Student score     |
| date       | Exam date         |

---

## ⚙ Installation and Setup

### 1. Clone the Repository

```
git clone https://github.com/yourusername/online-examination-system.git
```

### 2. Open Project

Open the project using:

* IntelliJ IDEA
* Eclipse
* VS Code with Java extensions

### 3. Configure Database

1. Install MySQL or SQLite
2. Create a database
3. Update database credentials in:

```
DBConnection.java
```

### 4. Run the Application

Compile and run the main file:

```
Main.java
```

---

## 📦 Build Executable (.EXE)

Using **jpackage**:

```
jpackage --input target/ --name OnlineExamSystem --main-jar OnlineExamSystem.jar --type exe
```

This will generate a Windows installer for the application.

---

## 🔒 Security Considerations

* Password encryption
* Role-based access control
* Auto submission when exam time expires
* Prevention of unauthorized access

---

## 📈 Future Enhancements

* Support for descriptive questions
* Online server-based examination
* Webcam monitoring
* AI-based cheating detection
* Mobile application integration
* Cloud database support

---

## 👨‍💻 Author

Prabhat Joshi

---

