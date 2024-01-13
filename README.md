# SAMJ
SAMJ implemented with JavaFX (Project for the first Semester)

## Description

This purpose of this project is to have a UI in which you can manage a Oncall Duty plan. It is managed by a frontend and queried by a external telephone system (via IP / Curl requests).
The project consists of 2 parts, frontend and backend (and a Database). Both are in the same JavaFX Project. For our DB we use SQLite.

### Frontend

The frontend consists of 3 scenes:
1. login mask
2. Table where we can see the entries/data from the DB
3. Settings

In the UI you need to log in first. After a successful login you can see the oncall duty plan with its data. Where you can create, delete entries or modify them. In the settings you can set the IP and Port of the Server. 

| Telephone number | Start Time       | End Time         | Forward Number |
|------------------|------------------|------------------|----------------|
| 12312            | 02.02.2024 00:00 | 09.02.2024 23:59 | 333333         |
| 1231             | 01.01.2024 13:13 | 30.01.2024 15:49 | 35256256       |


### Backend

The main purpose of the server/backend package is to manage the database, HTTP Server and the logic of the requested features (coming from the telephone system).
* HTTP Server: is waiting for GET HTTP requests. Once a request is being received, the data is being validated and processed.
* Server: this is the backend, here happens the logic (features e.g. FORWARDCHECK). Manages the DB and HTTP Server. Checks periodically if the local dataset is up to date. 
* DB: SQLite, as the database will be lightweight. The class contains basic CRUD operations (Create/Read/Update/Delete). The DB is running on the local host.


## Important for the review 

We merged everything we have already done to MAIN branch. We have a GUI with dummy values, the logic is partly implemented but we did not connect the server/DB to the GUI yet. 

### How to start the Application
First you need to ensure that you are on the main branch and pulled all the latest commits.
To start the Application Class, you run it (you will need the JDK version 17). Then the GUI will start in a thread and the HTTP server in another. The GUI shows first a login window (we have dummy values in there you can use following credentials **user2:password2!** ) after a successful login you will see the table with our dummy values. There you can test out the filters and sorting features. The backend is started in parallel, you can test it via curl commands. (See Section Backend below )

### Current status: 

#### Frontend
In the User interface you can see our values, order them ascending, descending and also search/filter for values in every column. What needs to be done in the future is the edit mode and settings button and also the settings scene. (is being started in parallel via threads)

#### Backend
The backend (HTTP server) is started in parallel (threads) with the javafx application. You can test via the console and a curl command (in MacOS Terminal, Windows PowerShell, Linux Shell): 

```bash
# Terminal
╭─andi@MBPvonAndiDro ~/IdeaProjects/samj-javafx/samj  ‹dev*› 
╰─➤  curl http://localhost:8000/forwardcheck/\{number\=0123456789\;timestamp\=2023\}/
ERROR in logic!%
# The console Response is: ERROR in logic!% (because not implemented et)
# Application Console Reponse: number=0123456789;timestamp=2023 


╭─andi@MBPvonAndiDro ~/IdeaProjects/samj-javafx/samj  ‹dev*› 
╰─➤  curl http://localhost:8000/forwardcheck/number\=0123456789/
ERROR in logic!%
# The console Response is: ERROR in logic!% (because not implemented et)
# Application Console Reponse: number=0123456789

╭─andi@MBPvonAndiDro ~/IdeaProjects/samj-javafx/samj  ‹dev*› 
╰─➤  curl http://localhost:8000/sms/number\=0123456789/ 
NOT SUPPORTED%
# The console Response is: NOT SUPPORTED% (means the Feature you try to trigger does not exist)
# There is no Applications Response in this case.
```

```
#what happens in the Application console
number=0123456789;timestamp=2023
number=0123456789
```

See images:

<img width="1728" alt="image" src="https://github.com/SAMJ-CSDC26BB/samj-javafx/assets/45292760/17d53520-f5e1-4a48-89c8-fa5fe070628a">
<img width="1728" alt="image" src="https://github.com/SAMJ-CSDC26BB/samj-javafx/assets/45292760/909abe07-da3e-4fca-80cd-de82a8e66315">






