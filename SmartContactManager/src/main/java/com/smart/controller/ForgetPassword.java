package com.smart.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgetPassword {

	//open email form 
	@RequestMapping("/forgot")
	public String openEmsilForm()
	{
		return "forgetemailform";
		
	}
	
	@PostMapping("/sendotp")
	public String senOTP(@RequestParam("email") String email)
	{
		
		//genrate otp
		Random random=new Random(1000);
		 int otp = random.nextInt(99999);
		return "verifyotp";
		
	}
}
