package com.course.scheduling.user;

import org.springframework.data.jpa.repository.JpaRepository;

/* Spring Data JPA repository (topic 38): save, findAll, count ... for free. */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByEmail(String email);
}
