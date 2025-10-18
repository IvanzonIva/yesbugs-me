package common.helpers;

import com.codeborne.selenide.Screenshots;
import io.qameta.allure.Allure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Attachments {

    public static void attachScreenshot(String name) {
        try {
            File screenshot = Screenshots.takeScreenShotAsFile();
            if (screenshot != null && screenshot.exists()) {
                Allure.addAttachment(name, Files.newInputStream(screenshot.toPath()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}