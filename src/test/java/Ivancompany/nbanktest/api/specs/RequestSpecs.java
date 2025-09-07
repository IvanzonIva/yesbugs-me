package Ivancompany.nbanktest.core.specs;

import Ivancompany.nbanktest.core.config.TestConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpHeaders;

import java.util.Arrays;

public class RequestSpecs {

    private RequestSpecs() {}

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setBaseUri(TestConfig.BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(Arrays.asList(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter()));
    }

    public static RequestSpecification unauthSpec() {
        return defaultRequestBuilder().build();
    }

    public static RequestSpecification authSpec(String authHeader) {
        return defaultRequestBuilder()
                .addHeader(HttpHeaders.AUTHORIZATION, authHeader)
                .build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestBuilder()
                .addHeader(HttpHeaders.AUTHORIZATION, TestConfig.ADMIN_AUTH)
                .build();
    }
}