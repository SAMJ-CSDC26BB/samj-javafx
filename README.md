# SAMJ
SAMJ implemented with JavaFX (Project for the first Semester)

## Description

This purpose of this project is to have a UI in which you can manage a Oncall Duty plan. It is managed by a frontend and queried by a external telephone system (via IP / Curl requests).
The project consists of 2 parts, frontend and backend (and a Database). Both are in the same JavaFX Project. For our DB we use SQLite.

### Frontend
The frontend consists of 3 scenes:
1. login mask
2. Table where we can see the queries
3. Settings

In the UI you need to log in first. After a successful login you can see the plan. can create, delete entries or modify them. In the settings you can set the IP and Port of the Server. 

| Telephone number | Start Time       | End Time         | Forward Number |
|------------------|------------------|------------------|----------------|
| 12312            | 02.02.2024 00:00 | 09.02.2024 23:59 | 333333         |
| 1231             | 01.01.2024 13:13 | 30.01.2024 15:49 | 35256256       |


### Backend


to test, curl comamnd in cli.



## Important for the review 

We merged everything we have already done to DEV. We have a GUI with dummy values, the logic is partly implemented but we did not connect it to the GUI yet. 

In the User interface you can see our values, order them ascending, descending and also search/filter for entries. What needs to be done is the edit mode and settings button and also the settings scene.

To start you go to the Application Class and run it (you will need the JDK version 17) then the GUI will start with a login window (we have dummy values in there you can user **user2:password2!** ) after a successful login you will see the table with our dummy values. There you test out the filters and ordering features.



