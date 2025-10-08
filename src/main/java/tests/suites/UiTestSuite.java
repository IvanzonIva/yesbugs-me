package tests.suites;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import tests.ui.iteration2.negative.DepositNegativeUiTest;
import tests.ui.iteration2.negative.TransferNegativeUiTest;
import tests.ui.iteration2.positive.DepositUiTest;
import tests.ui.iteration2.positive.TransferUiTest;

@Suite
@SelectClasses({
        //Позитивные
        DepositUiTest.class,
        TransferUiTest.class,

        //Нигативные
        DepositNegativeUiTest.class,
        TransferNegativeUiTest.class
})
public class UiTestSuite {
}
