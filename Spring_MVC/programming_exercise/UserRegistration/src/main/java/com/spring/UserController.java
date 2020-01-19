package com.spring;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.spring.model.User;
import com.spring.service.UserService;
import com.twilio.sdk.Twilio;
import com.twilio.sdk.creator.api.v2010.account.MessageCreator;
import com.twilio.sdk.resource.api.v2010.account.Message;
import com.twilio.sdk.type.PhoneNumber;


@EnableScheduling
@Controller
public class UserController {
	
	//-------- Twilio SMS API Details ---------------
	public static final String ACCOUNT_SID =
            "AC8cdb573f885fb52c1b51a27a62166b23";
    public static final String AUTH_TOKEN =
            "66f839dda7e1a5876c6cb566c92bb17c";
    public static final String TRIAL_NUMBER = "+14178150591";
    //------- End of Twilio Details ---------
    
    //limit configurable number of hours - within which mobile number and email should be verified-------------
    public static final String CONF_NUM_HOURS = "10";
    
    //------- configurable number of months to unblock details -----------
    public static final String CONF_NUM_MONTHS = "6";
    //------- End of configurable numbers--------------
    
	private UserService userService;
	
	@Autowired(required=true)
	@Qualifier(value="userService")
	public void setUserService(UserService ps){
		this.userService = ps;
	}
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String restAPI() {
		return "home";
	}

	/**
	 * Requirements : 1) A user will register using the name, address, email Id ,mobile number and password
	 * 2) Only one registration is allowed using for a single emailId or a mobile number
	 * 3) Email Id must be verified by sending a URL at the email id and then marking the email id as
	 *    verified when the url is clicked by the user
	 * 4) Mobile number must be verified by sending an OTP (one time password) via SMS to the
		  mobile number and then marking the mobile number as verified once the user enters the
		  unique OTP sent to the user's mobile number
	 * @param jsonData
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value= "/adduser", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public ResponseEntity<String> addUser(@RequestBody String jsonData) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(jsonData);
		
		String restUrlForAuth = "";
		String emailMessage = "";
		if(null != json.get("name") && !json.get("name").equals("") &&
			null != json.get("address") && !json.get("address").equals("") && 
			null != json.get("email") && !json.get("email").equals("") &&  
			null != json.get("mobile_number") && !json.get("mobile_number").equals("") && 
			null != json.get("password") && !json.get("password").equals(""))
		{
			// one registration is allowed using for a single emailId or a mobile number
			if(this.userService.isEmailUnique(""+json.get("email")) && this.userService.isMobileNumUnique(""+json.get("mobile_number")))
			{
				User user = new User();
				String randomNum = ""+((int)(Math.random()*9000)+1000);
				//Get current date time
		        LocalDateTime now = LocalDateTime.now();
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		        String currentDateTime = ""+now.format(formatter);
		        
				user = this.userService.addUser(
						new User("" + json.get("name"),
								 "" + json.get("address"), 
								 ("" + json.get("email")).toLowerCase(),
								 "" + json.get("mobile_number"),
								 "" + json.get("password"),
								 "0",
								 "0",
								 "" + currentDateTime,
								 randomNum,
								 "0",
								 "0"
								)
				);
				
				// Email Id must be verified by sending a URL at the email id and then marking the email id as
				//    verified when the url is clicked by the user
				//---------------- Create email authentication URL ------------
				// Note: authentication URL can can be better to encode before sending due to security
				// but at current stage url passed as plan message
				restUrlForAuth = "http://localhost:8080/UserRegistration/authemail/"+user.getId();
				emailMessage = "User registred sucessfully. <br/>Click on link to verify email:"+restUrlForAuth;
				
				//---------------- Email Send Process -----------------------
				final String toEmail = user.getEmail(); // can be any email id 
				final String fromEmail = "mtest7608@gmail.com"; //requires valid gmail id
				final String password = "mtest7608QWERTY"; // correct password for gmail id
				
				Properties props = new Properties();
				props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
				props.put("mail.smtp.port", "587"); //TLS Port
				props.put("mail.smtp.auth", "true"); //enable authentication
				props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
				
		        //create Authenticator object to pass in Session.getInstance argument
				Authenticator auth = new Authenticator() {
					//override the getPasswordAuthentication method
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(fromEmail, password);
					}
				};
				Session session = Session.getInstance(props, auth);
				 
				EmailUtil.sendEmail(session, toEmail,"User Registration", emailMessage);
				//------------- End of email message send Process ---------------------
				
				//------------- Mobile Number OTP Send Process ------------------
				String smsData = "User registred sucessfully. Your OTP is:"+randomNum+"<br/>Verify your number using POST request API: "
        				+ "http://localhost:8080/UserRegistration/autheotp/"+user.getId();
				
				Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

			    MessageCreator message = Message.create(ACCOUNT_SID, 
			    		 					new PhoneNumber("+91"+user.getMobile_number()), 
			    		 					new PhoneNumber(TRIAL_NUMBER),
			    		 					smsData);
			        
				//----------------------------------------------------------------
				return new ResponseEntity<String>("Success", HttpStatus.OK);
			}
		}
		return new ResponseEntity<String>("Error", HttpStatus.OK);
	}
	
	/**
	 * When user click on link from there mail account that link will send request to this rest method
	 * for authenticate email
	 * @param id
	 * @return
	 */
	@RequestMapping("/authemail/{id}")
	@ResponseBody
	public String authenticateEmail(@PathVariable("id") String id){
		User user = new User();
		
		if(null == id || id.equals(""))
		{
			return "ERROR Invalid Data!";
		}
		try
		{
        	user = (User)this.userService.getUserById(id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
        if(null == user.getEmail() || user.getEmail().equals(""))
        {
        	return "ERROR User not exists! Invalid Data.";
        }
        else if(user.getEmail_verify_flag().equals("1"))
        {
        	return "User Email verification already done!";
        }
        else
        {
        	user.setEmail_verify_flag("1");
        	this.userService.updateUser(user);
        	return "User informatation Sucessfully verified";
        }
    }
	

	/**
	 * When user click on link from there phone that link will send request to this rest method
	 * for authenticate mobile number
	 * @param id
	 * @return
	 */
	@RequestMapping(value= "/autheotp/{id}", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public String authenticateMobileNum(@PathVariable("id") String id, @RequestBody String jsonData) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(jsonData);
		
		String restUrlForAuth = "";
		String emailMessage = "";
		String otpReceived = ""+json.get("otp");
		// Check if otp is null or blank or otp length is proper if any of the
		// condition is not satisfied than display error
		if(null == json.get("otp") || json.get("otp").equals("") || otpReceived.length() != 4)
		{
			return "Error: Wrong OTP.";
		}
		
		User user = new User();
		
		// Check if id is proper or not
		if(null == id || id.equals(""))
		{
			return "ERROR: Invalid Data!";
		}
		
		// Get user data based on id
		try
		{
        	user = (User)this.userService.getUserById(id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		// Verify user information
        if(null == user.getMobile_number() || user.getMobile_number().equals(""))
        {
        	return "ERROR: User not exists! Invalid Data.";
        }
        // If user mobile number is already verified than send message
        else if(user.getMobile_num_verify_flag().equals("1"))
        {
        	return "ERROR: User Mobile number verification already done!";
        }
        else
        {
        	// Check that OTP is correct or Not.
        	if(user.getOtp_num().equals(otpReceived))
        	{
	        	user.setMobile_num_verify_flag("1");
	        	this.userService.updateUser(user);
	        	return "Success:User informatation Sucessfully verified";
        	}
        	else
        	{
        		return "ERROR: Wrong OTP entered!";
        	}
        }
    }
	
	/**
	 * This is method as scheduler which will run every day it will block all new registred users who
	 * did not verified there email and mobile number within predefined hours time period
	 * Requirements:
	 * 5) There will be a limit (cofigurable number of hours)-> "CONF_NUM_HOURS" within which 
	 * mobile number and email should be verified.
	 */
	@RequestMapping(value = "/userBlockScheduler", method = RequestMethod.GET)
	@Scheduled(fixedRate=86400000)	// Runs every 24 hours (Once in day)
	public void userBlockScheduler() {
		this.userService.blockUser(CONF_NUM_HOURS);
		System.out.println("userBlockScheduler Running");
	}
	
	
	/**
	 * This is method will work as scheduler which will run every month it will delete 
	 * all blocked (canceled) users if there six months is over.
	 * 
	 * Requirements:
	 * 5) If the verification does not take place within the stipulated time frame,
	 * the registration will be canceled and the email id and mobile numbers will be invalid for any
	 * future registrations "upto six months"->CONF_NUM_MONTHS
	 */
	@RequestMapping(value = "/cancelUserRegistration", method = RequestMethod.GET)
	@Scheduled(cron="0 0 1 1 * ?")	// Runs At 1 AM on the 1st day of every month:
	public void cancelUserRegistration() {
		this.userService.cancelUserRegistration(CONF_NUM_MONTHS);
	}
	
	
	/**
	 * Requirements :
	 * 6) Once the user has registered, and the email id AND mobile numbers are verified, s/he can
	 *  login using either emailId or mobile number + password
	 * 7) Three consecutive incorrect attempts should lock the user account
	 * @param request
	 * @return
	 */
	@RequestMapping(value= "/user_login/login", method = RequestMethod.POST)
	public ModelAndView loginUserSubmit(HttpServletRequest request){
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		int lockCounter = Integer.parseInt(request.getParameter("lockCounter"));
		
		ModelAndView mv = new ModelAndView();
		
		if(null != userName && !userName.equals("") && null != password && !password.equals(""))
		{
			User user = new User();
			
			try {
				user = (User)this.userService.validateUserLogin(userName, password);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			if(null != user.getEmail() && !user.getEmail().equals(""))
			{
				//session.setAttribute("user", user);
				mv.getModelMap().put("user", user);
				mv.setViewName("dashboard");
			}
			else
			{
				lockCounter += 1;
                request.setAttribute("lockCounter", lockCounter);
                //Three consecutive incorrect attempts should lock the user account
                if (lockCounter >= 3) {
                    //----
                	// Lock User flag over here
                	this.userService.lockUserAccountByUsername(userName);
                	//----
                	request.setAttribute("faliureMessage",
                            "Error : Your account is locked as You have attemped Three times.");
                } else {
                    request.setAttribute("faliureMessage",
                            "Error : Username or Password is not valid or exist.");
                }
                mv.setViewName("user_login");
			}
		}
		else
		{
		    request.setAttribute("faliureMessage",
                    "Error : Username or Password is not valid or exist.");

            request.setAttribute("lockCounter", 0);
		    mv.setViewName("user_login");
		}
		System.out.println("qwerty...."+ userName +"...." + password);
		//return "redirect:/";
		return mv;
	}
	
	@RequestMapping(value = "/user_login")
	public String userLogin_click(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		
		if(null == session)
		{
			return redirectToLogin(request);
		}
		else
		{
			return "dashboard";
		}
	}
	

	@RequestMapping(value = "/log_out", method = RequestMethod.GET)
	public String userLogOut(HttpServletRequest request) {
		HttpSession session = request.getSession();
		
		session.invalidate();
		return "redirect:/";
	}
	
	public String redirectToLogin(HttpServletRequest request) {
        request.setAttribute("lockCounter", 0);
        return "user_login";
    }
	
	
	// Requirements: 
	// 8)There should be a separate service using which administrator can unlock an account
	@RequestMapping(value = "/unlockUserAccount/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String unLockUserAccount(@PathVariable("id") String id) {
		if(null != id && !id.equals(""))
		{
			try {
				this.userService.unLockUserAccountById(id);
				return "Sucess";
			}
			catch(Exception e)
			{
				return "Error";
			}
		}
		else
		{
			return "Error";
		}
	}
}
