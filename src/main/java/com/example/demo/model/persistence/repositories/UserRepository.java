package com.example.demo.model.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.persistence.ApplicationUser;

/**
 * The Interface UserRepository.
 */
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
	
	/**
	 * Find by username.
	 *
	 * @param username the username
	 * @return the customer
	 */
	ApplicationUser findByUsername(String username);
}
