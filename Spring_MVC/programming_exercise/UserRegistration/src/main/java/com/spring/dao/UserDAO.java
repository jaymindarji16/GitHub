package com.spring.dao;

import java.util.List;

import com.spring.model.User;

public interface UserDAO {

	public Boolean isEmailUnique(String email);
	public Boolean isMobileNumUnique(String number);
	public User addUser(User p);
	public void updateUser(User p);
	public User getUserById(String id);
	public void blockUser(String hours);
	public void cancelUserRegistration(String months);
	public User validateUserLogin(String username, String passoword);
	public void lockUserAccountByUsername(String userName);
	public void unLockUserAccountById(String id);
}
