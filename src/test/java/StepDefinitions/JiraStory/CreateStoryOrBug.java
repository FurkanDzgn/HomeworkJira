package StepDefinitions.JiraStory;

import API.Pojo.ResponseBodyRestApi;
import Pages.com.Localhost8080.BacklogPage;
import Pages.com.Localhost8080.IssuePage;
import Pages.com.Localhost8080.LoginPage;
import Utils.ConfigReader;
import Utils.Driver;
import Utils.PayloadUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.bs.A;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class CreateStoryOrBug {

    WebDriver driver = Driver.getDriver();
    LoginPage loginPage = new LoginPage();
    BacklogPage backlogPage=new BacklogPage();
    IssuePage issuePage=new IssuePage();
    public static String desc;
    public static String sumr;

    HttpClient httpClient;
    URIBuilder uriBuilder;
    HttpPost httpPost;
    HttpEntity httpEntity;
    HttpResponse httpResponse;
    ObjectMapper objectMapper;
    ResponseBodyRestApi parsedObject;

    @Given("The user enter  {string}, {string} and {string}")
    public void the_user_enter_and(String summary, String description, String name) throws IOException, URISyntaxException, InterruptedException {

        desc=description;
        sumr=summary;
        httpClient = HttpClientBuilder.create().build();
        uriBuilder = new URIBuilder();
        uriBuilder.setScheme("http").setHost("localhost").setPort(8080).setPath("rest/api/2/issue");
        httpPost = new HttpPost(uriBuilder.build());
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Cookie", PayloadUtils.getJsessionCookie());
        httpEntity = new StringEntity(PayloadUtils.getJiraIssuePayload(summary, description, name));
        httpPost.setEntity(httpEntity);

        httpResponse = httpClient.execute(httpPost);
        Assert.assertEquals(HttpStatus.SC_CREATED, httpResponse.getStatusLine().getStatusCode());
        objectMapper = new ObjectMapper();
        parsedObject = objectMapper.readValue(httpResponse.getEntity().getContent(),
                ResponseBodyRestApi.class);
        driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);

    }


        @When("The user goes to the LocalHost8080")
        public void the_user_goes_to_the_local_host8080() throws InterruptedException {

        driver.navigate().to(ConfigReader.getProperty("urlJira"));
        loginPage.username.sendKeys(ConfigReader.getProperty("userName"));
        loginPage.password.sendKeys(ConfigReader.getProperty("password"));
        loginPage.submitButton.click();
        Thread.sleep(1000);

        int length=backlogPage.stories.size()-1;

        Assert.assertTrue(sumr.contains(backlogPage.stories.get(length).getText()));
    }

    @When("The user click last created one")
    public void the_user_click_last_created_one() throws InterruptedException {

        Thread.sleep(500);
        int length=backlogPage.stories.size()-1;

        Actions actions=new Actions(driver);
        actions.moveToElement(backlogPage.stories.get(length)).click().perform();

    }

    @Then("click viewWorkflow")
    public void click_viewWorkflow() {
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        backlogPage.viewWorkflow.click();
    }

    @Then("validate information")
    public void validate_information() throws InterruptedException {
        Assert.assertTrue(desc.contains(issuePage.description.getText()));
        System.out.println(parsedObject.getKey());
        System.out.println(issuePage.keyValue.getText());
        Assert.assertEquals(parsedObject.getKey(),issuePage.keyValue.getText());

        Thread.sleep(500);
        issuePage.owl.click();
        issuePage.logOutButton.click();

    }


}
