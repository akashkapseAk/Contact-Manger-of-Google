package com.smart.controller;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactResposityDao;
import com.smart.dao.UserRepositryDao;
import com.smart.message.Message;
import com.smart.model.Contact;
import com.smart.model.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepositryDao dao;
	
	@Autowired
	private ContactResposityDao contactResposityDao;
	@Autowired
	private BCryptPasswordEncoder bcrypass;
	
	
	//method for adding common data response using modelattribute
	
	@ModelAttribute
	public void commonDataAdd(Model m,Principal principal)
	{
		//String username = pr.getName();
		
				String username = principal.getName();
				//System.out.println(principal.getName());
				System.out.println("USERNAME"+username);
				
				//passing name of user
				User userByName = dao.getUserByName(username);
				m.addAttribute("userByName", userByName);
	}


	//user dashboard handler
	
	@GetMapping("/dashboard")
	public String dashboard(Model m,Principal principal)
	{
		
		
		return "normal/dashboard";
		
	}
	
	//addcontact handler
	@GetMapping("/addContact")
	public String addContact(Model m)
	{
		m.addAttribute("title","add-contact");
		m.addAttribute("contact", new Contact());
		
	
		return "normal/addContactForm";
		
	}
	//adding contact in database and img and mesg
	
	@PostMapping("/process-contact")
	public String proccessContanct(@ModelAttribute Contact contact,@RequestParam("profileimage") MultipartFile file,Principal principal,HttpSession session) 
	{
		try {
		String name = principal.getName();
		
		 User user = this.dao.getUserByName(name);
		 
		 if(file.isEmpty()) {
			 contact.setImage("contact.png");
		 }
		 else
		 {
			 //file name dega and set karege contact mai
			 contact.setImage(file.getOriginalFilename());
			 //ek class hai use apne file upload kar skthe hai
			 File file2 = new ClassPathResource("static/image").getFile();
			Path path = Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
			 
		 }
		 
		 contact.setUser(user);
		 user.getContacts().add(contact);
		 this.dao.save(user);
		 
		 
		System.out.println("data"+contact);
//for msg uplod succefully 
		session.setAttribute("msg", new Message("your contact is added", "success"));
		System.out.println("data add");
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("error"+e.getMessage());
			e.printStackTrace();
			
			session.setAttribute("msg", new Message("Something went wrong", "danger"));
		}
		return "normal/addContactForm";
		
	}
	
	//contact view handler
	
	@GetMapping("/viewContact")
	public String viewContact(Model m,Principal principal)
	{
		
		//perinal pe user ke details mil jegi
		String userName = principal.getName();
		//ye user de dega 
	
		User user = this.dao.getUserByName(userName);
		
	
		//ye user ke id  de dega method se
		List<Contact> Contact = this.contactResposityDao.findContactByUser(user.getId());
		//send kar dege contact view pe
		m.addAttribute("contact", Contact);
		m.addAttribute("title", "view Contact");
		return "normal/viewContact";
		
	}
	
	//view profile after click
	@RequestMapping("/{cid}/contact")
	public String viewProfile(Model m,@PathVariable("cid") Integer cid)
	{
		Optional<Contact> contactoptional = this.contactResposityDao.findById(cid);
		Contact contact = contactoptional.get();
		m.addAttribute("contact", contact);
		return "normal/viewProfile";
	}
	
	//delete contact handler
	
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid)
	{
		//yaha se id se delete kar dege 
		Optional<Contact> contoptional = contactResposityDao.findById(cid);
		//and get karege 
		Contact contact = contoptional.get();
		
		//cascding kiya hai apne all  to apne ko user ko unlink karana hoga 
		contact.setUser(null);
	       this.contactResposityDao.delete(contact);
		return "redirect:/user/viewContact";
		
	}
	
	// open updateform handler
	
	@RequestMapping("/update-contact/{cid}")
	public String updateform(@PathVariable("cid") Integer cid,Model m)
	{
		m.addAttribute("title", "update contact");
		
		Optional<Contact> findById = this.contactResposityDao.findById(cid);
		Contact contact = findById.get();
		
		m.addAttribute("contact", contact);
		return "normal/update-form";
		
	}
	//update form handler
	
	@RequestMapping(value = "/process-update",method = RequestMethod.POST)
	public String updateHander(@RequestParam("profileimage") MultipartFile file,@ModelAttribute Contact contact,Principal principal,HttpSession session,Model m)
	{
		try {
			//delete photo
			
			//update form photo
			if(!file.isEmpty())
			{
				//update profile new
				 File file2 = new ClassPathResource("static/image").getFile();
			   Path path = Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
				
			//new image update
				contact.setImage(file.getOriginalFilename());
				
			}
			//current user nikalo
			 User user= this.dao.getUserByName(principal.getName());
			//contact mai set kar dege
			 contact.setUser(user); //yaha se update hoga
			 
			 //msg 
			 session.setAttribute("msg", new Message("update success fully", "success"));
			 
			this.contactResposityDao.save(contact);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return "redirect:/user/update-contact/"+contact.getCid();
		
	}
	
	//view your profile
	
	@RequestMapping("/profile")
	public String yourProfile()
	{
		return "normal/checkyourProfile";
		
	}
	
	//open setting handler
	
	@GetMapping("/setting")
	public String setting()
	{
		return "normal/settings";
		
	}
	
	//change password  handler
	
	@PostMapping("/changepassword")
	public String passwordChange(@RequestParam("oldpassword") String oldpassword,@RequestParam("newpassword") String newpassword,Principal principal,HttpSession session)
	{
		//user nikalo
		String name = principal.getName();
		//store kar dege
		User currentuser = this.dao.getUserByName(name);
		System.out.println(currentuser.getPassword());
		
		
		//checking old pass
		//mahting using BCryptPasswordEncoder using method
		
		if(this.bcrypass.matches(oldpassword, currentuser.getPassword()))
		{
			//change pass
			currentuser.setPassword(this.bcrypass.encode(newpassword));
			this.dao.save(currentuser);
			session.setAttribute("msg", new Message("succeefully change password", "success"));
			 
		}
		else
		{
			session.setAttribute("msg", new Message("plesas Correct old password", "error"));
			return "redirect:/user/setting";
		}
		
		
		return "redirect:/user/dashboard";
		
	}
	
}
