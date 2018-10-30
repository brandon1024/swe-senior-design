package com.unb.beforeigo.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BucketControllerIntegrationTest extends APIIntegrationTestSuite {

    @BeforeEach void setup() {

    }

    @Nested
    class CreateBucketTest {

        @Test void createBucketWithUnknownOwnerTest() {

        }

        @Test void createBucketWithNonNullIdTest() {

        }

        @Test void createBucketSuccessTest() {

        }
    }

    @Nested
    class RetrieveBucketTest {

        @Test void retrieveBucketsByUserTest() {

        }

        @Test void retrieveUnknownBucketTest() {

        }

        @Test void retrieveBucketByIdTest() {

        }
    }

    @Nested
    class UpdateBucketTest {

        @Test void updateBucketTest() {

        }
    }

    @Nested
    class DeleteBucketTest {

        @BeforeEach void setup(){

        }

        @Test void deleteBucketTest() {

        }

        @Test void deleteUnknownBucketTest() {

        }
    }
}
