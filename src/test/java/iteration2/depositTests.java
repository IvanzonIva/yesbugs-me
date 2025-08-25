package iteration2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class depositTest {

    @BeforeAll
    public static void setupRestAssured(){
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()));
    }

    // позитивные сценарии
    @ParameterizedTest
    @ValueSource(ints = {100, 1, 5000})
    public void depositValidAmount_shouldReturn200(int amount){
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic SXZhbjE5OTZfMTpJdmFuMTk5NiQ=")
                .body("""
                  {
                   "id": 2,
                   "balance": %d
                  }
                  """.formatted(amount))
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("accountNumber", Matchers.equalTo("ACC2"));
    }

    //негативные сценарии
    @ParameterizedTest(name = "Deposit: id={0}, amount={1}, auth={2} → expect {3}")
    @CsvSource(value = {
            // amount > 5000
            "2, 5001, true, 400, 'Deposit amount exceeds the 5000 limit'",
            // amount = 0
            "2, 0, true, 400, 'Invalid account or amount'",
            // amount < 0
            "2, -1, true, 400, 'Invalid account or amount'",
            // нет авторизации
            "2, 99.9, false, 401, ''",
            // чужой акк
            "99, 1, true, 403, 'Unauthorized access to account'"
    })
    public void depositInvalid_shouldReturnError(int id,
                                                 String amount,
                                                 boolean withAuth,
                                                 int expectedStatus,
                                                 String expectedMessage) {

        var request = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);

        if (withAuth) {
            request.header("Authorization", "Basic SXZhbjE5OTZfMTpJdmFuMTk5NiQ=");
        }

        request.body("""
              {
               "id": %d,
               "balance": %s
              }
              """.formatted(id, amount))
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(expectedStatus)
                .body(equalTo(expectedMessage));
    }
}
