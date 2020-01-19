This project is compiled using Eclipse IDE for Enterprise Java Developers.
Version: 2019-06 (4.12.0) BUILD

In these project we can insert or update employees using REST API and Web page

//============================ WEB View Form for Login Process ================
Login Page : http://localhost:8080/UserRegistration/user_login
Home Page  : http://localhost:8080/UserRegistration/

//============================ RESTAPI ================================================
Add new Users
http://localhost:8080/UserRegistration/adduser

Sample Post Request : 
Content-Type - application/json
POST Input:
{
	"name" : "Suresh shyam",
	"address" : "B-67 Samesh nagar vitat road vadodara",
	"email" : "sureshshyam@gmail.com",
	"mobile_number" : "9033545874",
	"password" : "123456"
}


When user click on link from there mail account that link will send request to this rest method for authenticate email
http://localhost:8080/UserRegistration/authemail/{id}

GET Request : http://localhost:8080/UserRegistration/userBlockScheduler

Manually cancel user registration
GET Request : http://localhost:8080/UserRegistration/cancelUserRegistration

separate service using which administrator can unlock an account
GET Request : http://localhost:8080/UserRegistration//unlockUserAccount/1