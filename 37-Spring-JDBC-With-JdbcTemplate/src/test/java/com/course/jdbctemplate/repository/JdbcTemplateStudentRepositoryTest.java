package com.course.jdbctemplate.repository;

import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

/* Runs the contract tests against the classic JdbcTemplate implementation. */
@JdbcTest(properties = "app.repository.type=jdbc-template")
@Import(JdbcTemplateStudentRepository.class)
class JdbcTemplateStudentRepositoryTest extends StudentRepositoryContractTest {
}
