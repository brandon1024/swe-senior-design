package com.unb.beforeigo.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class APIIntegrationTestSuite {

    @Autowired TestRestTemplate restTemplate;

    @Test void testServerHeartbeat() {
        String body = this.restTemplate.getForObject("/", String.class);
        Assertions.assertEquals("Success", body);
    }
}
