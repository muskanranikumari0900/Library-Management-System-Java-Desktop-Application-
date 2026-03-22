# Library Management System

A robust Java Desktop Application to manage books, members, and library transactions. Built using Core Java, Swing, JDBC, and MySQL.

## Prerequisites

1.  **Java JDK 8 or higher** installed.
2.  **MySQL Server** installed and running.
3.  **MySQL Connector/J (.jar)**. You need to download the JDBC driver for MySQL (`mysql-connector-j-8.0.x.jar`) and place it inside the `lib/` directory of this project.

## Database Setup

1.  Open your MySQL command line client or Workbench.
2.  Execute the provided `schema.sql` script to create the database, tables, and the default admin user.
    ```sql
    source schema.sql;
    ```
    *Default credentials inserted are: Username: **admin**, Password: **admin123***
3.  Ensure your `src/com/library/dao/DatabaseConnection.java` has the correct `USER` and `PASSWORD` set for your local MySQL server.

## Building and Running

You can compile and run the project easily using the provided batch script on Windows:

1.  Place your `mysql-connector-j.jar` file in the `lib` directory.
2.  Double click on `run.bat` or run it from the Command Prompt.

This script will automatically compile all `.java` files into a `bin/` directory and execute the `App` class.

## Project Structure

*   **`model`**: Contains Data models (`Book`, `Member`, `Issue`, `User`).
*   **`dao`**: Contains Database utilities and CRUD operation classes using JDBC.
*   **`ui`**: Contains the Java Swing Graphical User Interfaces (Login, Dashboard, and nested Panels).
*   **`util`**: Contains utility implementations like UI layout styling and Session management.
*   **`main`**: Contains the application entry point (`App`).
