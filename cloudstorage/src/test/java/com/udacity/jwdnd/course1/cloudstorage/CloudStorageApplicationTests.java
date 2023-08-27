package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.File;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

	private static String firstName = "Nguyen";
	private static String lastName = "An";
	private static String userName = "annt";
	private static String password = "123";
	private static String noteTile = "Test Note Title";
	private static String noteDescription = "Test Note Description";
	private static String credentialURL = "google.com";


	@LocalServerPort
	private int port;

	private WebDriver driver;

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	@Test
	public void getLoginPage() {
		driver.get("http://localhost:" + this.port + "/login");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	public void unauthorizedUserSignUp(){
		driver.get("http://localhost:" + this.port + "/signup");
		Assertions.assertEquals("Sign Up", driver.getTitle());
	}
	@Test
	public void unauthorizedUserHome(){
		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	/**
	 * PLEASE DO NOT DELETE THIS method.
	 * Helper method for Udacity-supplied sanity checks.
	 **/
	private void doMockSignUp(String firstName, String lastName, String userName, String password){
		// Create a dummy account for logging in later.

		// Visit the sign-up page.
		WebDriverWait webDriverWait =new WebDriverWait(driver, 10);
		driver.get("http://localhost:" + this.port + "/signup");
		webDriverWait.until(ExpectedConditions.titleContains("Sign Up"));

		// Fill out credentials
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputFirstName")));
		WebElement inputFirstName = driver.findElement(By.id("inputFirstName"));
		inputFirstName.click();
		inputFirstName.sendKeys(firstName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputLastName")));
		WebElement inputLastName = driver.findElement(By.id("inputLastName"));
		inputLastName.click();
		inputLastName.sendKeys(lastName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUserName")));
		WebElement inputUserName = driver.findElement(By.id("inputUserName"));
		inputUserName.click();
		inputUserName.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
		WebElement inputPassword = driver.findElement(By.id("inputPassword"));
		inputPassword.click();
		inputPassword.sendKeys(password);

		// Attempt to sign up.
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonSignUp")));
		WebElement buttonSignUp = driver.findElement(By.id("buttonSignUp"));
		buttonSignUp.click();

		/* Check that the sign up was successful.*/
		Assertions.assertTrue(driver.findElement(By.id("success-msg")).getText().contains("You successfully signed up!"));
	}

	/**
	 * PLEASE DO NOT DELETE THIS method.
	 * Helper method for Udacity-supplied sanity checks.
	 **/
	private void doLogIn(String userName, String password)
	{
		// Log in to our dummy account.
		driver.get("http://localhost:" + this.port + "/login");
		WebDriverWait webDriverWait =new WebDriverWait(driver, 10);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUserName")));
		WebElement loginUserName = driver.findElement(By.id("inputUserName"));
		loginUserName.click();
		loginUserName.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
		WebElement loginPassword = driver.findElement(By.id("inputPassword"));
		loginPassword.click();
		loginPassword.sendKeys(password);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginBtn")));
		WebElement loginButton = driver.findElement(By.id("loginBtn"));
		loginButton.click();

		webDriverWait.until(ExpectedConditions.titleContains("Home"));

	}

	@Test
	public void testRedirection() {
		// Create a test account
		doMockSignUp(firstName, lastName, userName + "9", password);

		// Check if we have been redirected to the log in page.
		Assertions.assertEquals("http://localhost:" + this.port + "/signup", driver.getCurrentUrl());
	}

	@Test
	public void testBadUrl() {
		try {
			// Create a test account
			doMockSignUp(firstName, lastName, userName, password);
			doLogIn("Url", "123");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// Try to access a random made-up URL.
		driver.get("http://localhost:" + this.port + "/some-random-page");
		Assertions.assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
	}

	@Test
	public void testLargeUpload() {
		// Create a test account
		doMockSignUp(firstName, lastName, userName, password);
		doLogIn(userName, password);
		// Try to upload an arbitrary large file
		WebDriverWait webDriverWait = new WebDriverWait(driver, 1);
		String fileName = "upload5m.zip";

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fileUpload")));
		WebElement fileSelectButton = driver.findElement(By.id("fileUpload"));
		fileSelectButton.sendKeys(new File(fileName).getAbsolutePath());

		WebElement uploadButton = driver.findElement(By.id("uploadButton"));
		uploadButton.click();
		try {
			webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("success")));
		} catch (org.openqa.selenium.TimeoutException e) {
			System.out.println("Large File upload failed");
		}
		Assertions.assertTrue(driver.getPageSource().contains("HTTP Status 403 – Forbidden"));

	}

	@Test
	public void testNewUserAccessPage() throws InterruptedException {

		WebDriverWait webDriverWait =new WebDriverWait(driver, 10);

		// create User and SignUp
		doMockSignUp(firstName, lastName, userName + "3", password);
		doLogIn(userName + "3", password);

		// Log out
		WebElement logoutButton= driver.findElement(By.id("logoutBtn"));
		logoutButton.click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginBtn")));
		Assertions.assertEquals("Login", driver.getTitle());
	}

	public void checkRedirectNotes() throws InterruptedException {
		WebDriverWait webDriverWait =new WebDriverWait(driver, 10);

		driver.get("http://localhost:" + this.port + "/home");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

		driver.findElement(By.id("nav-notes-tab")).click();

		Thread.sleep(1000);
	}

	@Test
	public void createNewNote() throws InterruptedException {

		// Create User and signUp
		doMockSignUp(firstName, lastName, userName + "1", password);
		doLogIn(userName + "1", password);

		// redirect to Note tab
		WebElement notesTab = driver.findElement(By.id("nav-notes-tab"));

		notesTab.click();

		WebDriverWait webDriverWait =new WebDriverWait(driver, 10);

		// check is note tabe
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes")));
		Assertions.assertTrue(driver.findElement(By.id("nav-notes")).isDisplayed());

		// press btn create note
		WebElement addNoteButton= driver.findElement(By.id("addNoteBtn"));
		addNoteButton.click();

		// Insert data for note title and note description
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteTitle")));
		WebElement inputTitle = driver.findElement(By.id("noteTitle"));
		inputTitle.click();
		inputTitle.sendKeys(noteTile);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteDescription")));
		WebElement inputDescription = driver.findElement(By.id("noteDescription"));
		inputDescription.click();
		inputDescription.sendKeys(noteDescription);

		// Press button for create new note
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createNewNoteBtn")));
		WebElement submitNote = driver.findElement(By.id("createNewNoteBtn"));
		submitNote.click();

		// Redirect to note tab
		checkRedirectNotes();

		// Check note title
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteTable")));
		Assertions.assertTrue(driver.findElement(By.id("table-noteTitle")).getText().contains(noteTile));

		Thread.sleep(1000);
	}

	@Test
	public void editNote() throws InterruptedException {
		// Create User and signUp
		doMockSignUp(firstName, lastName, userName + "5", password);
		doLogIn(userName + "5", password);

		// redirect to Note tab
		WebElement notesTab = driver.findElement(By.id("nav-notes-tab"));

		notesTab.click();

		WebDriverWait webDriverWait =new WebDriverWait(driver, 10);

		// check is note tabe
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes")));
		Assertions.assertTrue(driver.findElement(By.id("nav-notes")).isDisplayed());

		// press btn create note
		WebElement addNoteButton= driver.findElement(By.id("addNoteBtn"));
		addNoteButton.click();

		// Insert data for note title and note description
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteTitle")));
		WebElement inputTitle = driver.findElement(By.id("noteTitle"));
		inputTitle.click();
		inputTitle.sendKeys(noteTile);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteDescription")));
		WebElement inputDescription = driver.findElement(By.id("noteDescription"));
		inputDescription.click();
		inputDescription.sendKeys(noteDescription);

		// Press button for create new note
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createNewNoteBtn")));
		WebElement submitNote = driver.findElement(By.id("createNewNoteBtn"));
		submitNote.click();

		// Redirect to note tab
		checkRedirectNotes();

		// Check note title
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteTable")));
		Assertions.assertTrue(driver.findElement(By.id("table-noteTitle")).getText().contains(noteTile));

		Thread.sleep(1000);


		// open model create/edit note
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editNoteBtn")));
		WebElement editNote = driver.findElement(By.id("editNoteBtn"));
		editNote.click();

		// Edit the note description
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteTitle")));
		WebElement inputTitle1 = driver.findElement(By.id("noteTitle"));
		inputTitle1.click();
		inputTitle1.clear();
		inputTitle1.sendKeys("Edited Title test");

		// Save
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createNewNoteBtn")));
		WebElement submitNote1 = driver.findElement(By.id("createNewNoteBtn"));
		submitNote1.click();

		// Redirect to note tab
		checkRedirectNotes();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteTable")));
		Assertions.assertTrue(driver.findElement(By.id("table-noteTitle")).getText().contains("Edited Title test"));
		Thread.sleep(1000);
	}

	@Test
	public void deleteNote() throws InterruptedException {

		WebDriverWait webDriverWait =new WebDriverWait(driver, 30);

		doMockSignUp(firstName, lastName, userName + "7", password);
		doLogIn(userName + "7", password);

		// redirect to Note tab
		WebElement notesTab = driver.findElement(By.id("nav-notes-tab"));

		notesTab.click();


		// check is note tabe
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes")));
		Assertions.assertTrue(driver.findElement(By.id("nav-notes")).isDisplayed());

		// press btn create note
		WebElement addNoteButton= driver.findElement(By.id("addNoteBtn"));
		addNoteButton.click();

		// Insert data for note title and note description
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteTitle")));
		WebElement inputTitle = driver.findElement(By.id("noteTitle"));
		inputTitle.click();
		inputTitle.sendKeys(noteTile);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteDescription")));
		WebElement inputDescription = driver.findElement(By.id("noteDescription"));
		inputDescription.click();
		inputDescription.sendKeys(noteDescription);

		// Press button for create new note
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createNewNoteBtn")));
		WebElement submitNote = driver.findElement(By.id("createNewNoteBtn"));
		submitNote.click();

		// Redirect to note tab
		checkRedirectNotes();

		// Check note title
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteTable")));
		Assertions.assertTrue(driver.findElement(By.id("table-noteTitle")).getText().contains(noteTile));

		Thread.sleep(1000);

		// press on delete btn
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("deleteNoteBtn")));
		WebElement deleteNote = driver.findElement(By.id("deleteNoteBtn"));
		deleteNote.click();

		// Redirect to note tab
		checkRedirectNotes();

		// check note deleted
		WebElement notesTable = driver.findElement(By.id("noteTable"));
		List<WebElement> notesList = notesTable.findElements(By.tagName("tbody"));

		Assertions.assertEquals(0, notesList.size());

		Thread.sleep(1000);
	}

	public void checkRedirectCredentialsTab(){
		WebDriverWait webDriverWait =new WebDriverWait(driver, 10);

		driver.get("http://localhost:" + this.port + "/home");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
		driver.findElement(By.id("nav-credentials-tab")).click();
	}

	@Test
	public void createNewCredential() throws InterruptedException {
		// create user and login
		doMockSignUp(firstName, lastName, userName + "2", password);
		doLogIn(userName + "2", password);

		// go to credentials-tab
		WebElement credentialsTab= driver.findElement(By.id("nav-credentials-tab"));
		credentialsTab.click();

		WebDriverWait webDriverWait =new WebDriverWait(driver, 30);
		String inputCredentialPassword = password;

		// open popup create credential
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addCredentialsBtn")));
		WebElement addCredentialsButton= driver.findElement(By.id("addCredentialsBtn"));
		addCredentialsButton.click();

		// Insert data for new credential
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialUrl")));
		WebElement inputURL = driver.findElement(By.id("credentialUrl"));
		inputURL.click();
		inputURL.sendKeys(credentialURL);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialUserName")));
		WebElement inputUserName = driver.findElement(By.id("credentialUserName"));
		inputUserName.click();
		inputUserName.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialPassword")));
		WebElement inputPassword = driver.findElement(By.id("credentialPassword"));
		inputPassword.click();
		inputPassword.sendKeys(inputCredentialPassword);

		// Press button create new credential
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createCredentialBtn")));
		WebElement submitNote = driver.findElement(By.id("createCredentialBtn"));
		submitNote.click();

		// Redirect to credential tabe
		checkRedirectCredentialsTab();

		// Check credentials are appended
		WebElement credentialsTable = driver.findElement(By.id("credentialTable"));
		List<WebElement> credList = credentialsTable.findElements(By.tagName("tbody"));

		Assertions.assertEquals(1, credList.size());

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));
		Assertions.assertNotEquals(driver.findElement(By.id("table-cred-password")).getText(), inputCredentialPassword);
		Thread.sleep(1000);
	}

	@Test
	public void editCredential() throws InterruptedException {
		// create user and login
		doMockSignUp(firstName, lastName, userName + "11", password);
		doLogIn(userName + "11", password);

		// go to credentials-tab
		WebElement credentialsTab= driver.findElement(By.id("nav-credentials-tab"));
		credentialsTab.click();

		WebDriverWait webDriverWait =new WebDriverWait(driver, 30);
		String inputCredentialPassword = password;

		// open popup create credential
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addCredentialsBtn")));
		WebElement addCredentialsButton= driver.findElement(By.id("addCredentialsBtn"));
		addCredentialsButton.click();

		// Insert data for new credential
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialUrl")));
		WebElement inputURL = driver.findElement(By.id("credentialUrl"));
		inputURL.click();
		inputURL.sendKeys(credentialURL);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialUserName")));
		WebElement inputUserName = driver.findElement(By.id("credentialUserName"));
		inputUserName.click();
		inputUserName.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialPassword")));
		WebElement inputPassword = driver.findElement(By.id("credentialPassword"));
		inputPassword.click();
		inputPassword.sendKeys(inputCredentialPassword);

		// Press button create new credential
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createCredentialBtn")));
		WebElement submitNote = driver.findElement(By.id("createCredentialBtn"));
		submitNote.click();

		// Redirect to credential tabe
		checkRedirectCredentialsTab();

		// Check credentials are appended
		WebElement credentialsTable = driver.findElement(By.id("credentialTable"));
		List<WebElement> credList = credentialsTable.findElements(By.tagName("tbody"));

		Assertions.assertEquals(1, credList.size());

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));
		Assertions.assertNotEquals(driver.findElement(By.id("table-cred-password")).getText(), inputCredentialPassword);
		Thread.sleep(1000);


		//  open popup edit credential
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editCredentialBtn")));
		WebElement editCredentialsButton= driver.findElement(By.id("editCredentialBtn"));
		editCredentialsButton.click();

		// make changes
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialUrl")));
		WebElement inputURL1 = driver.findElement(By.id("credentialUrl"));
		inputURL1.click();
		inputURL1.clear();
		inputURL1.sendKeys("https://github.com/AnNT180396/jwd_project1");

		// get unencrypted password
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialPassword")));
		String inputPassword1 = driver.findElement(By.id("credentialPassword")).getAttribute("value");


		// Press button create/update credential
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createCredentialBtn")));
		WebElement submitCredential = driver.findElement(By.id("createCredentialBtn"));
		submitCredential.click();

		// Redirect to credentials tab
		checkRedirectCredentialsTab();

		// check data is updated
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));
		Assertions.assertTrue(driver.findElement(By.id("table-cred-url")).getText().contains("https://github.com/AnNT180396/jwd_project1"));

		// Verify password is unencrypted
		Assertions.assertNotEquals(driver.findElement(By.id("table-cred-password")).getText(), inputPassword1);
		Thread.sleep(1000);
	}

	@Test
	public void deleteCredential() throws InterruptedException {
		doMockSignUp(firstName, lastName, userName + "6", password);
		doLogIn(userName + "6", password);

		// go to credentials-tab
		WebElement credentialsTab= driver.findElement(By.id("nav-credentials-tab"));
		credentialsTab.click();

		WebDriverWait webDriverWait =new WebDriverWait(driver, 30);
		String inputCredentialPassword = password;

		// open popup create credential
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addCredentialsBtn")));
		WebElement addCredentialsButton= driver.findElement(By.id("addCredentialsBtn"));
		addCredentialsButton.click();

		// Insert data for new credential
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialUrl")));
		WebElement inputURL = driver.findElement(By.id("credentialUrl"));
		inputURL.click();
		inputURL.sendKeys(credentialURL);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialUserName")));
		WebElement inputUserName = driver.findElement(By.id("credentialUserName"));
		inputUserName.click();
		inputUserName.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialPassword")));
		WebElement inputPassword = driver.findElement(By.id("credentialPassword"));
		inputPassword.click();
		inputPassword.sendKeys(inputCredentialPassword);

		// Press button create new credential
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createCredentialBtn")));
		WebElement submitNote = driver.findElement(By.id("createCredentialBtn"));
		submitNote.click();

		// Redirect to credential tabe
		checkRedirectCredentialsTab();

		// Check credentials are appended
		WebElement credentialsTable = driver.findElement(By.id("credentialTable"));
		List<WebElement> credList = credentialsTable.findElements(By.tagName("tbody"));

		Assertions.assertEquals(1, credList.size());

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));
		Assertions.assertNotEquals(driver.findElement(By.id("table-cred-password")).getText(), inputCredentialPassword);
		Thread.sleep(1000);


		// press btn delete credential
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("deleteCredentialBtn")));
		WebElement deleteCredentialsButton= driver.findElement(By.id("deleteCredentialBtn"));
		deleteCredentialsButton.click();

		// Redirect to credentials tab
		checkRedirectCredentialsTab();

		// check credential is deleted
		WebElement credentialTable = driver.findElement(By.id("credentialTable"));
		List<WebElement> credList1 = credentialTable.findElements(By.tagName("tbody"));

		Assertions.assertEquals(0, credList1.size());

		Thread.sleep(1000);
	}
}
