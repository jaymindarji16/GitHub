package com.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity bean with JPA annotations
 * Hibernate provides JPA implementation
 * 
 *
 */
@Entity
@Table(name="USER_DATA")
public class User {

	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String name;
	private String address;
	private String email;
	private String mobile_number;
	private String password;
	private String email_verify_flag;
	private String mobile_num_verify_flag;
	private String created_on;
	private String otp_num;
	private String block_user_flag;
	private String lock_account_flag;
	
	public User() {
		
	}
	
	public User(String name, String address, String email, String mobile_number, String password,
			String email_verify_flag, String mobile_num_verify_flag, String created_on, String otp_num, String block_user_flag, String lock_account_flag) {
		this.name = name;
		this.address = address;
		this.email = email;
		this.mobile_number = mobile_number;
		this.password = password;
		this.email_verify_flag = email_verify_flag;
		this.mobile_num_verify_flag = mobile_num_verify_flag;
		this.created_on = created_on;
		this.otp_num = otp_num;
		this.block_user_flag = block_user_flag;
		this.lock_account_flag = lock_account_flag;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile_number() {
		return mobile_number;
	}
	public void setMobile_number(String mobile_number) {
		this.mobile_number = mobile_number;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail_verify_flag() {
		return email_verify_flag;
	}
	public void setEmail_verify_flag(String email_verify_flag) {
		this.email_verify_flag = email_verify_flag;
	}
	public String getMobile_num_verify_flag() {
		return mobile_num_verify_flag;
	}
	public void setMobile_num_verify_flag(String mobile_num_verify_flag) {
		this.mobile_num_verify_flag = mobile_num_verify_flag;
	}
	public String getCreated_on() {
		return created_on;
	}
	public void setCreated_on(String created_on) {
		this.created_on = created_on;
	}
	public String getOtp_num() {
		return otp_num;
	}
	public void setOtp_num(String otp_num) {
		this.otp_num = otp_num;
	}
	public String getBlock_user_flag() {
		return block_user_flag;
	}
	public void setBlock_user_flag(String block_user_flag) {
		this.block_user_flag = block_user_flag;
	}
	public String getLock_account_flag() {
		return lock_account_flag;
	}
	public void setLock_account_flag(String lock_account_flag) {
		this.lock_account_flag = lock_account_flag;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", address=" + address + ", email=" + email + ", mobile_number="
				+ mobile_number + ", password=" + password + ", email_verify_flag=" + email_verify_flag
				+ ", mobile_num_verify_flag=" + mobile_num_verify_flag + ", created_on=" + created_on + "]";
	}


}
