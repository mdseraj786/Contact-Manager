package com.smartcontact.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.hibernate.type.descriptor.jdbc.LocalDateTimeJdbcType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.smartcontact.entities.Contact;
import com.smartcontact.entities.User;
import com.smartcontact.helper.FileUploader;
import com.smartcontact.helper.Message;
import com.smartcontact.repository.ContactRepository;
import com.smartcontact.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	private UserRepository userRepository;
	private ContactRepository contactRepository;
	private FileUploader fileUploader;
	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserController(UserRepository userRepository, ContactRepository contactRepository,
			PasswordEncoder passwordEncoder, FileUploader fileUploader) {
		super();
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.contactRepository = contactRepository;
		this.fileUploader = fileUploader;
	}

//	method for adding common data to all handler
	@ModelAttribute
	public void addCommanData(Model model, Principal principal) {
		String username = principal.getName();
		System.out.println("username : " + username);

		// get the user by username

		User user = userRepository.getUserByUserName(username);

		System.out.println(user);

		// send data to user_dashboard
		model.addAttribute("title", user.getName());
		model.addAttribute("user", user);

	}

	// dashboard home
	@GetMapping("/index")
	public String index() { // principle object's used for get the login data to controller

		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {

		model.addAttribute("contact", new Contact());
		model.addAttribute("title", "Add Contact");
		return "normal/add_contact_form";
	}

	// add contact to user
	@PostMapping("/add-process")
	public String contactProcess(@ModelAttribute Contact contact, @RequestParam MultipartFile imageFile,
			HttpSession session, Principal principal) {

		try {
//			only for check when error occur what is giving message

//			if(3>2) throw new Exception();

			String dynamicImageUrl = System.currentTimeMillis() + imageFile.getOriginalFilename();

			System.out.println(dynamicImageUrl);

			if (imageFile.isEmpty()) {
				contact.setImageUrl("contact_default.jpg");
			} else {
				fileUploader.fileUploaded(imageFile, dynamicImageUrl);
				contact.setImageUrl(dynamicImageUrl);
			}

			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			contact.setUser(user);
			user.getContact().add(contact);

			System.out.println(contact);
			this.userRepository.save(user);
			System.out.println("contact added success");

			session.setAttribute("message", new Message("contect added !! add more...", "alert-success"));

		} catch (Exception e) {
			System.out.println("something went wrong.....");
			session.setAttribute("message", new Message("Something went wrong !! Try again....", "alert-danger"));

			e.printStackTrace();
		}

		return "normal/add_contact_form";
	}

	// contact per page = 5 {n}
	// current page = 0 {page}
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "show user contacts");

		// send contact list to client
		String userName = principal.getName();// name means email
		User user = userRepository.getUserByUserName(userName);

//		List<Contact> contacts = user.getContact(); here we can find this user added contact but I used contactRepository

		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = contactRepository.findContactByUserId(user.getId(), pageable);
		System.out.println(contacts);

		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		if (contacts.getTotalPages() > page) {
			model.addAttribute("contacts", contacts);
		}
		if (contacts.getTotalElements() == 0) {
			model.addAttribute("add", "add");
		}

		return "normal/show_contacts";
	}

	// showing particular contact details
	@GetMapping("/contact/{contactId}")
	public String showParticularContact(@PathVariable long contactId, Principal principal, Model model) {

		Optional<Contact> optional = this.contactRepository.findById(contactId);
		Contact contact = optional.get();

// solve the security problem when any person hidden trail and see other contact details by using user/contact/8844

		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		model.addAttribute("title", contact.getEmail());

		return "normal/contact_details";
	}

	@GetMapping("/delete-contact/{contactId}")
	public String deleteContact(@PathVariable long contactId, Principal principal, Model model, HttpSession session) {

		Optional<Contact> optional = this.contactRepository.findById(contactId);
		Contact contact = optional.get();

		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);

		if (user.getId() == contact.getUser().getId()) {
			this.contactRepository.delete(contact);
			// after this deletion we need to delete the contact profile pic when you will
			// (create this method for large project)
			if (!contact.getImageUrl().equals("contact_default.jpg"))
				fileUploader.fileDeleted(null, contact.getImageUrl());

			// and also use sweet alert to comformation

			session.setAttribute("message", new Message("contact deleted successfully", "alert-success"));
		} else {
			session.setAttribute("message", new Message("You are not allow to delete this contact...", "alert-danger"));
		}

		return "redirect:/user/show-contacts/0";
	}

	// open update form
	@PostMapping("/update-contact/{contactId}")
	public String openUpdateForm(@PathVariable long contactId, Model model, Principal principal) {

		Contact contact = this.contactRepository.findById(contactId).get();

		if (userRepository.getUserByUserName(principal.getName()).getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);

		} else {
			model.addAttribute("add", "You are not update other users contact");
		}
		model.addAttribute("title", "contact update");

		return "normal/update_contact_form";
	}

	// process update contact
	@PostMapping("/update-process")
	public String updateProcess(@ModelAttribute Contact contact, @RequestParam MultipartFile imageFile,
			Principal principal, HttpSession session, Model model) {

		Contact oldContact = this.contactRepository.findById(contact.getcId()).get();
		System.out.println("oldContact : " + oldContact);

		String dynamicImageUrl = System.currentTimeMillis() + imageFile.getOriginalFilename();

		if (imageFile.isEmpty()) {
			// set previous image if file is empty
			contact.setImageUrl(oldContact.getImageUrl());
		} else {
			// here we can delete old image and then add new image, but here I used only add
			// image

			// delete old image ( it should use when you upload file with unique name like
			// adding data and time and maka sure not delete default image)

			if (!oldContact.getImageUrl().equals("contact_default.jpg"))
				fileUploader.fileDeleted(imageFile, oldContact.getImageUrl());

			// add new image
			fileUploader.fileUploaded(imageFile, dynamicImageUrl);
			contact.setImageUrl(dynamicImageUrl);
		}
		// always save user id to contact information otherwise we loose the user
		// information
		contact.setUser(this.userRepository.getUserByUserName(principal.getName()));

		Contact saveContact = this.contactRepository.save(contact);
		System.out.println("savedContact : " + saveContact);

		session.setAttribute("message", new Message("contact update successful", "alert-success"));

		return "redirect:/user/contact/" + oldContact.getcId();
	}

	// your profile handler
	@GetMapping("/profile")
	public String profile(Model model) {

		model.addAttribute("title", "Profile");

		return "normal/profile";
	}

	// open setting
	@GetMapping("/setting")
	public String openSettring(Model model) {

		model.addAttribute("title", "Change password");
		return "normal/setting";
	}

	@PostMapping("/passwordChange")
	public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword,
			Principal principal, HttpSession session) {
		try {
//			first check password is correct or not
			User user = this.userRepository.getUserByUserName(principal.getName());
			String encodecPassword = user.getPassword();
			
			
			if (passwordEncoder.matches(oldPassword, encodecPassword)) {
				if (!newPassword.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
				    session.setAttribute("message",new Message("Password should contains UpperCase, Lowercase, Numbers & symbol", "alert-danger"));
				    user.setPassword(passwordEncoder.encode(newPassword));
					this.userRepository.save(user);
					
				}
				
				session.setAttribute("message", new Message("Password has Changed Successfully", "alert-success"));
			} else {
				session.setAttribute("message", new Message("Password hasn't Changed", "alert-danger"));

			}

		} catch (Exception e) {
			// TODO: handle exception
			session.setAttribute("message", new Message("Password hasn't Changed Successfully", "alert-danger"));
			e.printStackTrace();
		}

		return "redirect:/user/setting";
	}

}
