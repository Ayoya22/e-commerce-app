package com.example.demo.model.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.persistence.AppUser;

/**
 * The Interface UserRepository.
 */
public interface UserRepository extends JpaRepository<AppUser, Long> {
	
	/**
	 * Find by username.
	 *
	 * @param username the username
	 * @return the customer
	 */
	AppUser findByUsername(String username);
}
