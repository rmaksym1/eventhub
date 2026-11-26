package com.userservice.controller;

import com.testcommon.test.AbstractControllerTest;
import org.springframework.test.context.jdbc.Sql;

@Sql(
        scripts = "/sql/controller/controller-data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = "/sql/cleanup.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
public abstract class AbstractUserServiceControllerTest extends AbstractControllerTest {
}
