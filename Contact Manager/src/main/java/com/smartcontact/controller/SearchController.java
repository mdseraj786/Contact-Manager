package com.smartcontact.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartcontact.entities.Contact;
import com.smartcontact.entities.User;
import com.smartcontact.repository.ContactRepository;
import com.smartcontact.repository.UserRepository;

@RestController
public class SearchController {
//	search handler
	private UserRepository userRepository;
	private ContactRepository contactRepository;
	
	@Autowired
	public SearchController(UserRepository userRepository, ContactRepository contactRepository) {
		super();
		this.userRepository = userRepository;
		this.contactRepository = contactRepository;
	}



	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable String query,Principal principal ){
		System.out.println(query);
		
		User user = this.userRepository.getUserByUserName(principal.getName());
		List<Contact> contacts = this.contactRepository.findByNameIgnoreCaseContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts) ;
	}
}
