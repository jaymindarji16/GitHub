Design and programming exercise

User Registration Service

Design and implement REST API for a basic, generic user registration service that can be used by
various applications such as online shopping or scoial networking site. Scope of this assignment is
only registration service.

Requirements:

1. A user will register using the name, address, email Id ,mobile number and password

2. Only one registration is allowed using for a single emailId or a mobile number

3. Email Id must be verified by sending a URL at the email id and then marking the email id as
verified when the url is clicked by the user

4. Mobile number must be verified by sending an OTP (one time password) via SMS to the
mobile number and then marking the mobile number as verified once the user enters the
unique OTP sent to the user's mobile number

5. There will be a limit (cofigurable number of hours) within which mobile number and email
should be verified. If the verification does not take place within the stipulated time frame,
the registration will be canceled and the email id and mobile numbers will be invalid for any
future registrations upto six months

6. Once the user has registered, and the email id AND mobile numbers are verified, s/he can
login using either emailId or mobile number + password

7. Three consecutive incorrect attempts should lock the user account

8. There should be a separate service using which administrator can unlock an account