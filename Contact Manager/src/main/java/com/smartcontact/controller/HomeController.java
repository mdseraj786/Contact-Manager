package com.smartcontact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.entities.User;
import com.smartcontact.helper.Message;
import com.smartcontact.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@GetMapping("/")
	public String home(Model model) {
		
		System.out.println("this is home controller");
		model.addAttribute("title", "Home - Smart Contact Manager");

		return "home";
	}

	// this is about controller
	@GetMapping("/about")
	public String about(Model model) {
		System.out.println("this is about controller");
		model.addAttribute("title", "Home - Smart Contact Manager");

		return "about";
	}

	// this is signup controller
	@GetMapping("/signup")
	public String signUp(Model model) {

		System.out.println("this is signUp controller");
		model.addAttribute("title", "SignUp - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	// this is login controller
	@GetMapping("/login")
	public String login(Model model) {
//		System.out.println(username + " this is " + password);
		System.out.println("this is Login controller 	1");
		model.addAttribute("title", "Login - Smart Contact Manager");
		return "login";
	}
	
//	@PostMapping("/signin")
//	public String login2(@RequestParam String username,@RequestParam String password,Model model) {
//		System.out.println(username + " this is " + password);
//		System.out.println("this is Login controller 	2");
//		model.addAttribute("title", "Login - Smart Contact Manager");
//		return "login";
//	}

	// register
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
			Model model, HttpSession session) {
		try {
			System.out.println("this is register controller");
			model.addAttribute("title", "SignUp - Smart Contact Manager");

			//
			if(result.hasErrors()){
				System.out.println("error occured");
				model.addAttribute("user", user);
				return "signup";
			}

			if (!agreement) {
				throw new Exception("you are not agree term and condition");
			}

			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			System.out.println(agreement);

			// save in db
			User save = null;
			// if(sessionHelper.getSession() == null){
			// save = (User) this.userRepository.save(user);
			// }
			save = (User) this.userRepository.save(user);
			System.out.println("saved user");
			System.out.println(save);

			model.addAttribute("user", new User());

			session.setAttribute("message", new Message("Registered successfully !! ", "alert-success"));
			return "login";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong !! " + e.getMessage(), "alert-danger"));
		}
		return "signup";
	}

//	@PostMapping("/login")
//	public String login(@RequestBody ) {
//		
//	}
//	
}

