package com.spring.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.dao.UserDAO;
import com.spring.model.User;

@Service
public class UserServiceImpl implements UserService {
	
	private UserDAO userDAO;

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	@Transactional
	public Boolean isEmailUnique(String email) {
		return this.userDAO.isEmailUnique(email);
	}
	
	@Override
	@Transactional
	public Boolean isMobileNumUnique(String number) {
		return this.userDAO.isMobileNumUnique(number);
	}
	
	@Override
	@Transactional
	public User addUser(User p) {
		return this.userDAO.addUser(p);
	}

	@Override
	@Transactional
	public void updateUser(User p) {
		this.userDAO.updateUser(p);
	}

	@Override
	@Transactional
	public User getUserById(String id) {
		return this.userDAO.getUserById(id);
	}
	
	@Override
	@Transactional
	public void blockUser(String hours) {
		this.userDAO.blockUser(hours);
	}
	
	@Override
	@Transactional
	public void cancelUserRegistration(String months) {
		this.userDAO.cancelUserRegistration(months);
	}
	
	@Override
	@Transactional
	public User validateUserLogin(String username, String passoword) {
		return this.userDAO.validateUserLogin(username, passoword);
	}
	
	@Override
	@Transactional
	public void lockUserAccountByUsername(String userName) {
		this.userDAO.lockUserAccountByUsername(userName);
	}
	
	@Override
	@Transactional
	public void unLockUserAccountById(String id) {
		this.userDAO.unLockUserAccountById(id);
	}
	
}
