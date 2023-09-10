Title: Software 2 GUI-Based Database Program
Author: Trevor Bower
Contact Information: Email - tbow133@wgu.edu | Phone - (804) 525-8099
Student Application Version: 1.0 (Final Draft)
Date: June 9, 2023

IDE + Version Number: IntelliJ IDEA 2021.1.3 Community Edition
JDK + Version Used: Java SE 17.0.1
JavaFX Version Used: JavaFX-SDK-17.0.1
MySQL Connector Driver Version Number + Update Number: mysql-connector-java-8.0.25

Purpose: This program is a GUI-based scheduling application to be used with a SQL database. It allows the user to manage
customer and appointment records and assists with appointment scheduling. It can also allow different options for
appointment table views and has localization for English and French languages and multiple time zones, based on system
settings. The program also includes login documentation.

Directions: Once the program has started, the user will be prompted to log in with a username and password. Once the
correct credentials have been validated, the user will be brought to the main form, which displays customer information
as well as appointment information that can be filtered by the times denoted by the associated radio buttons. The user
may add, update, or delete customers or appointments by selecting the corresponding button (after selecting a customer
or appointment in the appropriate table, in the case of update or delete). The user will then have access to input
fields for customer or appointment values, and those values can be saved to the SQL database by clicking save. Certain
checks are implemented to ensure valid data as well as avoid overlapping appointments, and if one of the checks fail,
a detailed message will be displayed in addition to the field in question being highlighted red. To cancel the
operation without saving, click the cancel button. The user may also check various reports by clicking the reports
button and may return to the login screen by clicking the logout button.


Description of Additional Report: The additional report added (Report 3) displays all customers and their contact
information. This can be especially useful if a customer needs to be reached in regard to cancelling or rescheduling an
appointment, and it can also be used for potential marketing or appointment reminder purposes.
