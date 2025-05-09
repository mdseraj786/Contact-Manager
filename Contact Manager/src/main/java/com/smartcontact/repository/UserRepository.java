package com.smartcontact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.smartcontact.entities.User;

@Repository
@EnableTransactionManagement
public interface UserRepository  extends JpaRepository<User,Integer>{
	
	@Query("SELECT u FROM User u WHERE u.email = :email")
    public User getUserByUserName(@Param("email") String email);

}

