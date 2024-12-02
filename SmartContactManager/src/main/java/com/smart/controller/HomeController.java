package com.smart.controller;
import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepositryDao;
import com.smart.message.Message;
import com.smart.model.User;

@Controller
public class HomeController {
	
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	@Autowired
	private UserRepositryDao dao;

	@RequestMapping("/")
	public String home(Model m)
	{
		m.addAttribute("title", "home-smart-manager");
		return "home";
		
	}
	
	@RequestMapping("/about")
	public String about(Model m)
	{
		m.addAttribute("title", "about-smart-manager");
		return "about";
		
	}
	
	@RequestMapping("/singup")
	public String singup(Model m)
	{
		m.addAttribute("title", "Registration-smart-manager");
		//userpass karege
		m.addAttribute("user",new User());
		return "singup";
		
	}
	
	//this handler for registeration User
	@RequestMapping(value = "/do_register",method = RequestMethod.POST)
	//modelAddtribute for get all object ,and requestparm for checkbox for data received pass name of checkbox agar check ni kiya to bydefault false send karge
	public String registerUser(@Validated @ModelAttribute("user") User user,BindingResult res,@RequestParam(value = "agreement",defaultValue = "false") boolean agreement,HttpSession session,Model m)
	{
		try {
			//agar ye message throw karge directly catch me ja ke msg show karega 
			if(!agreement)
			{
				System.out.println("you are not check term and condition");
				throw new Exception("you are not check term and condition");
			}
			/*
			 * //for vailation if(res.hasErrors()) {
			 * System.out.println("Error"+res.toString()); m.addAttribute("user", user);
			 * return "singup"; }
			 */
			
			System.out.println("agreement:"+agreement);
			System.out.println("User"+user);
			
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			
			User save = this.dao.save(user);
			m.addAttribute("save", save);
			
			//agar succeefully gya
//			m.addAttribute("user", new User());
//			session.setAttribute("message", new Message("Succefully register","alert-success"));
			

		} catch (Exception e) {
			// TODO: handle exception
			m.addAttribute("user", user);
			session.setAttribute("message", new Message("something went wrong"+e.getMessage(),"alert-danger"));
			return "singup";
		}
				
		return "home";
	}
	
	//handler for login page
	
	@GetMapping("/loginfrom")
	public String login(Model m)
	{
		m.addAttribute("title", "login page");
		return "loginfrom";
		
	}
	
}


