package Ivancompany.nbanktest.tests.suites;

import Ivancompany.nbanktest.tests.functional.account.DepositTest;
import Ivancompany.nbanktest.tests.functional.account.TransferTest;
import Ivancompany.nbanktest.tests.functional.user.UserUpdateTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        DepositTest.class,
        TransferTest.class,
        UserUpdateTest.class
})
public class ApiTestSuite {
}
