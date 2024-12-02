package com.smart.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.smart.model.Contact;

public interface ContactResposityDao extends JpaRepository<Contact, Integer> {
//
	//database ek ek user nikalne ke liye ek custome method jo append karege
	
    @Query("from Contact as c where c.user.id=:userid")
	public List<Contact>  findContactByUser(@Param("userid") int userid);
}
