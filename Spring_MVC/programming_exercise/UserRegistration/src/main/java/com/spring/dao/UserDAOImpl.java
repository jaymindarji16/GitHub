package com.spring.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.spring.model.User;

@Repository
public class UserDAOImpl implements UserDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf){
		this.sessionFactory = sf;
	}

	@Override
	public Boolean isEmailUnique(String email) {
		Session session = this.sessionFactory.getCurrentSession();
		List<?> usersList = session.createQuery("FROM User user WHERE user.email = '"+ email +"'").list();
		
		if(usersList.isEmpty())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public Boolean isMobileNumUnique(String number) {
		Session session = this.sessionFactory.getCurrentSession();
		List<?> usersList = session.createQuery("FROM User user WHERE user.mobile_number = '"+ number +"'").list();
		
		if(usersList.isEmpty())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public User addUser(User p) {
		Session session = this.sessionFactory.getCurrentSession();
		session.persist(p);
		logger.info("User saved successfully, User Details="+p.getId());
		return p;
	}

	@Override
	public void updateUser(User p) {
		Session session = this.sessionFactory.getCurrentSession();
		session.update(p);
		logger.info("User updated successfully, User Details="+p);
	}
	
	/*
	 * This HQL query blocks all users whoes email or mobile number is nor verified
	 * within fixed time period
	 */
	@Override
	public void blockUser(String hours) {
		Session session = this.sessionFactory.getCurrentSession();
		
		LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        String currentDateTime = ""+now.format(formatter);
        
        String queryString = "UPDATE user_data SET block_user_flag=1 WHERE "
				+" block_user_flag=0 AND (email_verify_flag=0 OR mobile_num_verify_flag=0) AND "
				+" (STR_TO_DATE('"+currentDateTime+"','%Y-%m-%d %H:%i:%s') NOT BETWEEN STR_TO_DATE(created_on,'%Y-%m-%d %H:%i:%s') AND DATE_ADD(created_on, INTERVAL '"+hours+"' HOUR))";
		
		Query query = session.createSQLQuery(queryString);
		int result = query.executeUpdate();
	}
	
	
	/**
	 * Requirement: s/he can login using either emailId or mobile number + password
	 */
	@Override
	public User validateUserLogin(String username, String password) {
		User user = new User();
		
		Session session = this.sessionFactory.getCurrentSession();
		
		String queryString = "";
		String condition = "";
		
		if(username.contains("@"))
		{
			condition += " user.email = :username";
		}
		else
		{
			condition += " user.mobile_number = :username";
		}
		
        queryString = "SELECT user FROM User user WHERE user.block_user_flag=0 AND "+ condition +""
        		+ " AND user.password = :password AND user.lock_account_flag=0";
        
		List<?> userList = session.createQuery(queryString)
				.setParameter("username", username)
				.setParameter("password", password).list();
		
		if(userList.isEmpty())
		{
			return user;
		}
		else
		{
			return (User)userList.get(0);
		}
	}
	/*
	 * Requirement Statement : 
	 * If the verification does not take place within the stipulated time frame,
	 * the registration will be canceled and the email id and mobile numbers will be invalid for any
	 * future registrations upto six months(taken configurable month)
	 * 
	 * This SQL query deletes all blocked users(registration canceled) who created account 
	 * configurable month before.
	 * 
	 */
	@Override
	public void cancelUserRegistration(String months) {
		Session session = this.sessionFactory.getCurrentSession();
		
		LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        String currentDateTime = ""+now.format(formatter);
        
		String queryString = "DELETE FROM user_data WHERE "
				+" block_user_flag=1 AND "
				+" (STR_TO_DATE('"+currentDateTime+"','%Y-%m-%d %H:%i:%s') NOT BETWEEN STR_TO_DATE(created_on,'%Y-%m-%d %H:%i:%s') AND DATE_ADD(created_on, INTERVAL '"+months+"' MONTH))";		
		Query query = session.createSQLQuery(queryString);
		int result = query.executeUpdate(); 
	}
	
	@Override
	public void lockUserAccountByUsername(String userName) {
		Session session = this.sessionFactory.getCurrentSession();
		
		String Condition = "";
		if(userName.contains("@"))
		{
			Condition += " user.email=:userName";
		}
		else
		{
			Condition += " user.mobile_number=:userName";
		}
		String queryString = "UPDATE User user SET user.lock_account_flag=1 WHERE "
				+" user.block_user_flag=0 AND "+ Condition ;
		
		Query query = session.createQuery(queryString).setParameter("userName", userName);
		int result = query.executeUpdate(); 
	}
	
	@Override
	public void unLockUserAccountById(String id) {
	Session session = this.sessionFactory.getCurrentSession();
		
		String queryString = "UPDATE User user SET user.lock_account_flag=0 WHERE "
				+" user.block_user_flag=0 AND user.id=:id";
		
		Query query = session.createQuery(queryString).setParameter("id", Integer.parseInt(id));
		int result = query.executeUpdate();
	}
	
	@Override
	public User getUserById(String id) {
		Session session = this.sessionFactory.getCurrentSession();		
		User p = (User) session.load(User.class, new Integer(id));
		logger.info("User loaded successfully, User details="+p);
		return p;
	}
}
