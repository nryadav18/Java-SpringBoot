package com.course.dbsecurity.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * AppUserRepository - Spring Data JPA writes the SQL for us (topic 38/39).
 * findByUsername is the method our UserDetailsService needs at login time.
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);
}
