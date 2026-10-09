package com.course.jdbctemplate.repository;

import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

/* Runs the contract tests against the JdbcClient implementation. */
@JdbcTest(properties = "app.repository.type=jdbc-client")
@Import(JdbcClientStudentRepository.class)
class JdbcClientStudentRepositoryTest extends StudentRepositoryContractTest {
}
