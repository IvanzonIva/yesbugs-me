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

public class TransferTests {

    @BeforeAll
    public static void setupRestAssured(){
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()));
    }
    // позитивные сценарии
    @ParameterizedTest
    @ValueSource(ints = {100, 1, 500, 10000})
    public void transferValidAmount_shouldReturn200(int amount){
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic SXZhbjE5OTZfMTpJdmFuMTk5NiQ=")
                .body("""
                 {
                    "senderAccountId": 1,
                    "receiverAccountId": 2,
                    "amount": %d
                 }
                  """.formatted(amount))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("message", Matchers.equalTo("Transfer successful"));
    }

    //негативные сценарии
    @ParameterizedTest(name = "Transfer: senderAccountId={0}, receiverAccountId={1}, amount={2}, auth={3} → expect {4}")
    @CsvSource(value = {
            // amount > 10000
            "1, 2, 10001, true, 400, 'Transfer amount cannot exceed 10000'",
            // amount = 0
            "1, 2, 0, true, 400, 'Invalid transfer: insufficient funds or invalid accounts'",
            // amount < 0
            "1, 2, -1, true, 400, 'Invalid transfer: insufficient funds or invalid accounts'",
            // перевод на тот же аккаунт
            "1, 1, 100, true, 400, 'Invalid transfer: insufficient funds or invalid accounts'",
            // amount > суммы остатка (преддусловия, на 3акк amount=50)
            "3, 2, 555, true, 400, 'Invalid transfer: insufficient funds or invalid accounts'",
            // нет авторизации
            "3, 2, 99.9, false, 401, ''",
            // перевод на несуществующий счет
            "1, 99, 100, true, 400, 'Invalid transfer: insufficient funds or invalid accounts'",
            // перевод с несущетсвющего счета
            "99, 1, 100, true, 403, 'Unauthorized access to account'",
    })
    public void transferInvalid_shouldReturnError(int senderAccountId,
                                                 int receiverAccountId,
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
                "senderAccountId": %d,
                "receiverAccountId": %d,
                "amount": %s
              }
              """.formatted(senderAccountId, receiverAccountId, amount))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(expectedStatus)
                .body(equalTo(expectedMessage));
    }
}
