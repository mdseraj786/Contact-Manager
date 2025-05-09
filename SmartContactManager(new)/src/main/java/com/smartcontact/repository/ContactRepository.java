package com.smartcontact.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.entities.Contact;
import com.smartcontact.entities.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

	@Query("FROM Contact as c  where c.user.id  =:userId ")
	public Page<Contact> findContactByUserId(@Param("userId") long userId, Pageable pageable);
	
	public List<Contact> findByNameIgnoreCaseContainingAndUser(String keywords,User user);
}
