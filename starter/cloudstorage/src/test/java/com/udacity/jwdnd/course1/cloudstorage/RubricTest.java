package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

//order method was taken from this article https://www.baeldung.com/junit-5-test-order
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RubricTest {
    @LocalServerPort
    private int port;

    private WebDriver driver;

    private String url;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        this.driver = new ChromeDriver();
        this.url = "http://localhost:" + this.port;
    }

    @AfterEach
    public void afterEach() {
        if (this.driver != null) {
            driver.quit();
        }
    }

    private void doMockSignUp(String firstName, String lastName, String userName, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:" + this.port + "/signup");
        wait.until(ExpectedConditions.titleContains("Sign Up"));

        WebElement firstNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputFirstName")));
        WebElement lastNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputLastName")));
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("buttonSignUp")));

        firstNameInput.sendKeys(firstName);
        lastNameInput.sendKeys(lastName);
        usernameInput.sendKeys(userName);
        passwordInput.sendKeys(password);
        submitButton.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:" + this.port + "/login?success"));
    }

    private void doLogIn(String userName, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:" + this.port + "/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername"))).sendKeys(userName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword"))).sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button"))).click();

    }

    private void doLogOut() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(url + "/home");
        wait.until(ExpectedConditions.titleContains("Home"));

        driver.findElement(By.id("logout")).click();

        Assertions.assertEquals("http://localhost:" + this.port + "/login?logout", driver.getCurrentUrl());

        driver.get(url + "/home");
        Assertions.assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());
    }



    /**
     * Test flow from sign up, to login, to logout
     * Rubric:
     * Write a Selenium test that signs up a new user, logs that user in, verifies that they can access the home page,
     * then logs out and verifies that the home page is no longer accessible.
     */
    @Test
    @Order(1)
    void testFlowTillLogout() {
        doMockSignUp("rubric","test","Test","password");
        doLogIn("Test","password");
        doLogOut();
    }

    /**
     * Test that going to home without logging in redirects back to login.
     * Rubric:
     * Write a Selenium test that verifies that the home page is not accessible without logging in.
     */
    @Test
    void testHomeInaccessible() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(url + "/home");
        wait.until(ExpectedConditions.titleContains("Login"));

        Assertions.assertEquals(url + "/login", driver.getCurrentUrl());
    }

    /**
     * Rubric:
     * Write a Selenium test that logs in an existing user,
     * creates a note and verifies that the note details are visible in the note list.
     */
    @Test
    @Order(2)
    void testNoteCreation(){
        doLogIn("Test","password");

        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(2));
        driver.get(url + "/home");
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
        WebElement noteTabBtn = driver.findElement(By.id("nav-notes-tab"));
        noteTabBtn.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addNote")));
        WebElement addNoteBtn = driver.findElement(By.id("addNote"));
        addNoteBtn.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));
        WebElement noteTitleTextArea = driver.findElement(By.id("note-title"));
        noteTitleTextArea.click();
        noteTitleTextArea.sendKeys("hello");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-description")));
        WebElement noteDescTextArea = driver.findElement(By.id("note-description"));
        noteDescTextArea.click();
        noteDescTextArea.sendKeys("hello");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("saveChanges")));
        WebElement saveChangesBtn = driver.findElement(By.id("saveChanges"));
        saveChangesBtn.click();

        //Test success url
        Assertions.assertEquals(url + "/result?success=true", driver.getCurrentUrl());
        //test the note is visible
        driver.get(url + "/home");
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
        noteTabBtn = driver.findElement(By.id("nav-notes-tab"));
        noteTabBtn.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title-display")));
        WebElement noteTitleDisplay = driver.findElement(By.id("note-title-display"));
        WebElement noteDescDisplay = driver.findElement(By.id("note-description-display"));

        Assertions.assertEquals("hello",noteTitleDisplay.getText());
        Assertions.assertEquals("hello", noteDescDisplay.getText());
    }

    /**
     * Rubric:
     * Write a Selenium test that logs in an existing user with existing notes,
     * clicks the edit note button on an existing note, changes the note data,
     * saves the changes, and verifies that the changes appear in the note list.
     */
    @Test
    @Order(3)
    void testChangeNote(){
        doLogIn("Test","password");

        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(2));
        driver.get(url + "/home");
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
        WebElement noteTabBtn = driver.findElement(By.id("nav-notes-tab"));
        noteTabBtn.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editNote")));
        WebElement noteEditBtn = driver.findElement(By.id("editNote"));
        noteEditBtn.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));
        WebElement noteTitleTextArea = driver.findElement(By.id("note-title"));
        noteTitleTextArea.click();
        noteTitleTextArea.sendKeys("Title text");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-description")));
        WebElement noteDescTextArea = driver.findElement(By.id("note-description"));
        noteDescTextArea.click();
        noteDescTextArea.sendKeys("Desc text");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("saveChanges")));
        WebElement saveChangesBtn = driver.findElement(By.id("saveChanges"));
        saveChangesBtn.click();

        //test the note is visible
        driver.get(url + "/home");
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
        noteTabBtn = driver.findElement(By.id("nav-notes-tab"));
        noteTabBtn.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title-display")));
        WebElement noteTitleDisplay = driver.findElement(By.id("note-title-display"));
        WebElement noteDescDisplay = driver.findElement(By.id("note-description-display"));

        Assertions.assertEquals("helloTitle text",noteTitleDisplay.getText());
        Assertions.assertEquals("helloDesc text", noteDescDisplay.getText());
    }

    /**
     * Rubric:
     * Write a Selenium test that logs in an existing user with existing notes,
     * clicks the delete note button on an existing note,
     * and verifies that the note no longer appears in the note list.
     */
    @Test
    @Order(4)
    void testNoteDelete(){
        doLogIn("Test","password");

        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(2));
        driver.get(url + "/home");
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
        WebElement noteTabBtn = driver.findElement(By.id("nav-notes-tab"));
        noteTabBtn.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("deleteNote")));
        WebElement noteDeleteBtn = driver.findElement(By.id("deleteNote"));
        noteDeleteBtn.click();

        Assertions.assertEquals(url + "/result?success=true", driver.getCurrentUrl());
        driver.get(url + "/home");
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
        noteTabBtn = driver.findElement(By.id("nav-notes-tab"));
        noteTabBtn.click();

        Assertions.assertTrue(driver.findElements(By.id("note-title-display")).isEmpty());
    }

    /**
     * Rubric:
     * Write a Selenium test that logs in an existing user,
     * creates a credential and verifies that the credential details are visible
     * in the credential list.
     */
    @Test
    @Order(5)
    void testCredCreation(){
        doLogIn("Test","password");

        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(2));
        driver.get(url + "/home");
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
        WebElement credTabBtn = driver.findElement(By.id("nav-credentials-tab"));
        credTabBtn.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addCredential")));
        WebElement addCredBtn = driver.findElement(By.id("addCredential"));
        addCredBtn.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));
        WebElement credUrlTextArea = driver.findElement(By.id("credential-url"));
        credUrlTextArea.click();
        credUrlTextArea.sendKeys("test.url");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-username")));
        WebElement credUsernameTextArea = driver.findElement(By.id("credential-username"));
        credUsernameTextArea.click();
        credUsernameTextArea.sendKeys("test");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-password")));
        WebElement credPassTextArea = driver.findElement(By.id("credential-password"));
        credPassTextArea.click();
        credPassTextArea.sendKeys("test");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialSubmitBtn")));
        WebElement saveChangesBtn = driver.findElement(By.id("credentialSubmitBtn"));
        saveChangesBtn.click();

        //test the credential is visible
        driver.get(url + "/home");
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
        credTabBtn = driver.findElement(By.id("nav-credentials-tab"));
        credTabBtn.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url-display")));
        WebElement credUrlDisplay = driver.findElement(By.id("credential-url-display"));
        WebElement credUsernameDisplay = driver.findElement(By.id("credential-username-display"));
        WebElement credPassDisplay = driver.findElement(By.id("credential-password-display"));

        Assertions.assertEquals("test.url", credUrlDisplay.getText());
        Assertions.assertEquals("test", credUsernameDisplay.getText());
        Assertions.assertEquals("test", credPassDisplay.getText());

    }

    /**
     * Rubric:
     * Write a Selenium test that logs in an existing user with existing credentials,
     * clicks the edit credential button on an existing credential, changes the credential data,
     * saves the changes, and verifies that the changes appear in the credential list.
     */
    @Test
    @Order(6)
    void testCredEdit(){
        doLogIn("Test","password");

        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(2));
        driver.get(url + "/home");
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
        WebElement credTabBtn = driver.findElement(By.id("nav-credentials-tab"));
        credTabBtn.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editCredential")));
        WebElement editCredBtn = driver.findElement(By.id("editCredential"));
        editCredBtn.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));
        WebElement credUrlTextArea = driver.findElement(By.id("credential-url"));
        credUrlTextArea.click();
        credUrlTextArea.sendKeys("test.url");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-username")));
        WebElement credUsernameTextArea = driver.findElement(By.id("credential-username"));
        credUsernameTextArea.click();
        credUsernameTextArea.sendKeys("test");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-password")));
        WebElement credPassTextArea = driver.findElement(By.id("credential-password"));
        credPassTextArea.click();
        credPassTextArea.sendKeys("test");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialSubmitBtn")));
        WebElement saveChangesBtn = driver.findElement(By.id("credentialSubmitBtn"));
        saveChangesBtn.click();

        //Test success url
        Assertions.assertEquals(url + "/result?success=true", driver.getCurrentUrl());
        //test the note is visible
        driver.get(url + "/home");
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
        credTabBtn = driver.findElement(By.id("nav-credentials-tab"));
        credTabBtn.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url-display")));
        WebElement credUrlDisplay = driver.findElement(By.id("credential-url-display"));
        WebElement credUsernameDisplay = driver.findElement(By.id("credential-username-display"));
        WebElement credPassDisplay = driver.findElement(By.id("credential-password-display"));

        Assertions.assertEquals("test.urltest.url",credUrlDisplay.getText());
        Assertions.assertEquals("testtest", credUsernameDisplay.getText());
        Assertions.assertEquals("testtest", credPassDisplay.getText());
    }

    @Test
    @Order(7)
    void testCredDelete(){
        doLogIn("Test","password");

        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(2));
        driver.get(url + "/home");
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
        WebElement credTabBtn = driver.findElement(By.id("nav-credentials-tab"));
        credTabBtn.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("deleteCredential")));
        WebElement credDeleteBtn = driver.findElement(By.id("deleteCredential"));
        credDeleteBtn.click();

        Assertions.assertEquals(url + "/result?success=true", driver.getCurrentUrl());
        driver.get(url + "/home");
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
        credTabBtn = driver.findElement(By.id("nav-credentials-tab"));
        credTabBtn.click();

        Assertions.assertTrue(driver.findElements(By.id("credential-url-display")).isEmpty());
    }
}