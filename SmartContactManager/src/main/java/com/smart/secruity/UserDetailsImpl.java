package com.smart.secruity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepositryDao;
import com.smart.model.User;

public class UserDetailsImpl implements UserDetailsService {
	
	@Autowired
	private UserRepositryDao repositryDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
		//fetaching user from database
		
		User user = repositryDao.getUserByName(username);
		
		if(user==null)
		{
			throw new UsernameNotFoundException("colud not found user");
		}
		
		CustomUserDeatails cud=new CustomUserDeatails(user);
		return cud;
	}

}
