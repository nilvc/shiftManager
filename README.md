# Shift Manager

Shift Manager is a Spring Boot application with an Angular frontend. Employees can request shift swaps, and managers can review open requests and approve or reject them with a comment.

## Quick Guide 

1. Deployed site link  -  https://shiftmanager-0kli.onrender.com/

2. UML Diagrams folder location - shiftManager\UML_Diagrams

3. API Endpoints summary is added in this readme file in below sections

4. Deployment and rollback discussion document - shiftManager\DEPLOYMENT_AND_ROLLBACK_STRATEGY.md

5. On the deployed site if data is not populated due to serivce restart please use "Seed demo data" button to populate Employees .


## Tech Stack

- Java 17
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- H2 file database ( We will move it to a standard sql database like Postgres)
- Angular 20
- Maven Wrapper

## Project Structure

```text
src/main/java/com/shiftmanager/demo   Spring Boot backend
src/main/frontend                     Angular frontend source
src/main/resources/static             Built Angular files served by Spring Boot
data/mydb.mv.db                       Local H2 database file
```

## Prerequisites

- Java 17
- Node.js and npm
- Angular CLI, or use the Angular CLI from `node_modules` after installing dependencies

Check versions:

```powershell
java -version
node --version
npm --version
```

## Setup

Install frontend dependencies:

```powershell
cd src\main\frontend
npm install
```

Build the Angular frontend into Spring Boot static resources:

```powershell
npm run build
```

Return to the project root:

```powershell
cd ..\..\..
```

Run backend tests:

```powershell
.\mvnw.cmd test
```

## Run as a Single Service

From the project root:

```powershell
.\mvnw.cmd spring-boot:run
```

Open the app:

```text
http://localhost:8080
```

The Angular app is served from `src/main/resources/static` by Spring Boot.

## Angular Development Mode

Use this when you want live frontend reloads while Spring Boot runs separately.

Terminal 1, from project root:

```powershell
.\mvnw.cmd spring-boot:run
```

Terminal 2:

```powershell
cd src\main\frontend
npm start
```

Open:

```text
http://localhost:4200
```

The Angular dev server uses `proxy.conf.json` to forward API calls to `http://localhost:8080`.

## Demo Data

The UI has a `Seed demo data` button. You can also seed data through the API:

```powershell
Invoke-RestMethod -Method Post http://localhost:8080/employees/setupDummyEmployees
Invoke-RestMethod -Method Post http://localhost:8080/shift/addDummyShifts
```

Current dummy employees include managers with `managerId = -1` and employees assigned to managers. Current dummy shifts are assigned to employee IDs `3`, `4`, and `5`.

## API Endpoints

### Employees

#### `POST /employees/setupDummyEmployees`

Creates sample employees.

Response:

```text
Employees added
```

#### `GET /employees/`

Returns all employees.

Example response:

```json
[
  {
    "employeeID": 1,
    "name": "Gaurav",
    "email": "Gaurav@gmail.com",
    "managerId": -1
  }
]
```

#### `GET /employees/{employeeId}`

Returns one employee by ID.

Example:

```text
GET /employees/3
```

### Shifts

#### `GET /shift/`

Health-style test endpoint for the shift controller.

Response:

```text
Hello
```

#### `POST /shift/addDummyShifts`

Creates sample shifts.

Response:

```text
Shifts added
```

#### `GET /shift/{shiftId}`

Returns a shift by ID.

Example response:

```json
{
  "shiftId": 1,
  "startTime": "2026-01-01T13:00:00",
  "endTime": "2026-01-01T15:00:00",
  "status": "PENDING",
  "employeeId": 3
}
```

#### `GET /shift/employee/{employeeId}`

Returns all shifts assigned to an employee.

Example:

```text
GET /shift/employee/3
```

### Shift Swap Requests

#### `POST /shiftsSwap/swap`

Creates a shift swap request.

Request body:

```json
{
  "changeShiftId1": 1,
  "changeShiftId2": 2,
  "employeeId1": 3,
  "employeeId2": 4
}
```

Field meaning:

- `employeeId1`: employee requesting the swap
- `changeShiftId1`: shift currently owned by `employeeId1`
- `employeeId2`: target employee
- `changeShiftId2`: shift currently owned by `employeeId2`

Response:

```text
Shift swap change request created
```

#### `GET /shiftsSwap/open`

Returns all pending shift swap requests for manager review.

Example response:

```json
[
  {
    "requestId": 1,
    "status": "PENDING",
    "updatedBy": null,
    "updateTime": null,
    "changeShiftId1": 1,
    "changeShiftId2": 2,
    "employeeId1": 3,
    "employeeId2": 4,
    "comment": null,
    "createdAt": "2026-07-09T02:30:00"
  }
]
```

#### `GET /shiftsSwap/employee/{employeeId}`

Returns shift swap requests where the employee is either the requesting employee or target employee.

Example:

```text
GET /shiftsSwap/employee/3
```

#### `POST /shiftsSwap/resolve`

Approves or rejects a pending shift swap request.

Request body:

```json
{
  "requestID": 1,
  "status": "APPROVED",
  "updatedBy": 1,
  "comment": "Approved for coverage"
}
```

Use `status: "REJECTED"` to reject a request.

When a request is approved, the backend swaps the owners of the two shifts.

Response:

```text
Shift change request approved
```

## H2 Database

The app uses a file-based H2 database:

```properties
spring.datasource.url=jdbc:h2:file:./data/mydb;AUTO_SERVER=TRUE
spring.datasource.username=sa
spring.datasource.password=p
```

H2 console:

```text
http://localhost:8080/h2-console
```

Use:

```text
JDBC URL: jdbc:h2:file:./data/mydb
User Name: sa
Password: p
```

## Useful Commands

Build frontend:

```powershell
cd src\main\frontend
npm run build
```

Run tests:

```powershell
.\mvnw.cmd test
```

Run app:

```powershell
.\mvnw.cmd spring-boot:run
```
