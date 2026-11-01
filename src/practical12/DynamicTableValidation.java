package practical12;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DynamicTableValidation {
    private static List<String> visibleNames(WebDriver driver) {
        List<String> names = new ArrayList<>();
        for (WebElement row : driver.findElements(By.cssSelector("#employeeTable tbody tr"))) {
            if (row.isDisplayed()) names.add(row.findElements(By.tagName("td")).get(1).getText());
        }
        return names;
    }

    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.getBoolean("headless")) options.addArguments("--headless=new");
        WebDriver driver = new ChromeDriver(options);
        try {
            driver.get(Path.of("web", "dynamic_table.html").toUri().toString());
            List<String> expected = visibleNames(driver);
            if (expected.size() != 4) throw new IllegalStateException("Expected four employees");
            for (WebElement row : driver.findElements(By.cssSelector("tbody tr"))) System.out.println(row.getText());
            Collections.sort(expected);
            driver.findElement(By.id("sortName")).click();
            if (!visibleNames(driver).equals(expected)) throw new IllegalStateException("Sorting failed");
            System.out.println("PASS - sorting: " + expected);
            WebElement search = driver.findElement(By.id("search"));
            search.sendKeys("Alice");
            driver.findElement(By.id("filter")).click();
            if (!visibleNames(driver).equals(List.of("Alice"))) throw new IllegalStateException("Filter leaked or lost records");
            System.out.println("PASS - filtering shows only Alice");
            search.clear(); search.sendKeys("Nobody");
            driver.findElement(By.id("filter")).click();
            if (!visibleNames(driver).isEmpty()) throw new IllegalStateException("No-match filter failed");
            search.clear(); driver.findElement(By.id("filter")).click();
            if (!visibleNames(driver).equals(expected)) throw new IllegalStateException("Filter reset failed");
            System.out.println("PASS - no-match filter and filter reset");
        } finally {
            driver.quit();
        }
    }
}
