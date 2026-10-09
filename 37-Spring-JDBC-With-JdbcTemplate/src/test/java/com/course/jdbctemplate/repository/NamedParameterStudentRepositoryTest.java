package com.course.jdbctemplate.repository;

import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

/* Runs the contract tests against the NamedParameterJdbcTemplate implementation. */
@JdbcTest(properties = "app.repository.type=named")
@Import(NamedParameterStudentRepository.class)
class NamedParameterStudentRepositoryTest extends StudentRepositoryContractTest {
}
