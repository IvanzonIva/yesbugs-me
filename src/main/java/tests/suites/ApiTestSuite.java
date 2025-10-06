package tests.suites;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import tests.api.iteration2.negative.ChangeNameUserNegativeTest;
import tests.api.iteration2.negative.DepositNegativeTest;
import tests.api.iteration2.negative.TransferNegativeTest;
import tests.api.iteration2.positive.ChangeNameUserTest;
import tests.api.iteration2.positive.DepositTest;
import tests.api.iteration2.positive.TransferTest;

@Suite
@SelectClasses({
//        //Позитивные
        DepositTest.class,
        TransferTest.class,
        ChangeNameUserTest.class,

      //Негативные
        DepositNegativeTest.class,
        TransferNegativeTest.class,
        ChangeNameUserNegativeTest.class
})
public class ApiTestSuite {
}
