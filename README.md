# **Task Management System**

This project is a Task Management System built using Maven and Spring Boot in Java. It provides a set of RESTful APIs to manage tasks.

## Features

The system supports the following operations:

- `GET /tasks`: Retrieve all tasks.
- `POST /tasks`: Create a new task.
- `GET /tasks/{id}`: Retrieve a task by ID.
- `PUT /tasks/{id}`: Update a task by ID.
- `DELETE /tasks/{id}`: Delete a task by ID.
- `PATCH /tasks/{id}/status`: Update the status of a task.
- `GET /tasks/status/{status}`: Retrieve tasks by status.
- `GET /tasks/assignee/{assigneeId}`: Retrieve tasks assigned to a specific user.
- `POST /tasks/{id}/comments`: Add a comment to a task.
- `GET /tasks/{id}/comments`: Retrieve all comments of a task.

## Setup

To set up the project locally, follow these steps:

1. Clone the repository to your local machine.
2. Navigate to the project directory.
3. Run `mvn clean install` to build the project and install dependencies.
4. Start the application by running `mvn spring-boot:run`.
5. The server should now be running locally! You can access the APIs at `localhost` on the specified port (e.g., `http://localhost:8080/tasks`).

## Testing

To test the APIs, you can use any API testing tool like Postman or curl. Make sure your server is running, and then send HTTP requests to the API endpoints.

Remember to replace `{id}`, `{status}`, and `{assigneeId}` in the URLs with actual values when testing specific tasks or users.

