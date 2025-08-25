package iteration2.user;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserUpdateTests {

    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()));
    }

    @ParameterizedTest(name = "UpdateUser: name={0}, auth={1} → expect {2}")
    @CsvSource(value = {
            //Позитивные проверки (две буквы-слова)
            "'Ivan Pavlov', true, 200, 'Profile updated successfully'",
            "'John Doe', true, 200, 'Profile updated successfully'",
            "'I P', true, 200, 'Profile updated successfully'",

            //Негативные проверки
            "'Ivan', true, 400, 'Name must contain two words with letters only'",
            "'Ivan123 Pavlov', true, 400, 'Name must contain two words with letters only'",
            "'Ivan Pavlov@', true, 400, 'Name must contain two words with letters only'",
            "'', true, 400, 'Name must contain two words with letters only'",
            "'Pavl Name', false, 401, ''"
    })
    public void updateUserName_shouldValidateRules(String name,
                                                   boolean withAuth,
                                                   int expectedStatus,
                                                   String expectedMessage) {

        var request = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);

        if (withAuth) {
            request.header("Authorization", "Basic SXZhbjE5OTZfMTpJdmFuMTk5NiQ=");
        }

        var response = request.body("""
              {
                "name": "%s"
              }
              """.formatted(name))
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(expectedStatus);

        if (expectedStatus == HttpStatus.SC_OK) {
            response.body("message", equalTo(expectedMessage));
        }

        if (expectedStatus != HttpStatus.SC_OK) {
            response.body(equalTo(expectedMessage));
        }
    }
}
