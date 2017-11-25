package selenium.test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class NewSelTest01 {
	public static void main(String[] args) {
		System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
		System.setProperty("webdriver.chrome.driver", "Chromedriver.exe");
		WebDriver driver;
		
		if (args.length != 0)
		{
			//System.out.println("args.length = " + args.length + ", args[0] = " + args[0]);
			if (args[0].equals("chrome")) {
				driver = new ChromeDriver();
			}
			else {
				if (args[0].equals("firefox")) {
					driver = new FirefoxDriver();
				}
				else {
					driver = new HtmlUnitDriver();
				}
			}
		}
		else
			driver = new HtmlUnitDriver();

		driver.get("https://goodline.info/");
		
		try {
			System.out.println("\n\tTesting started!");
			FindNews(driver);
			CheckNews(driver);
			System.out.println("\n\tTesting complete!");
		}
		catch(Exception exp) {
			System.out.println("\n\tError: Unable to find news");
		}

		driver.close();
    }
	
	public static String isLinkBroken(URL url) throws Exception 
	{
		String response = "";
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try
		{
		    connection.connect();
		    response = connection.getResponseMessage();	        
		    connection.disconnect();
		    return response;
		}
		catch(Exception exp)
		{
			return exp.getMessage();
		}  				
	}
	
	public static void FindNews(WebDriver driver) {
		WebElement newsA = driver.findElement(By.xpath("//*[text()[contains(.,'Новости')]]"));
		System.out.println("\n\tNews found, proceeding to " + newsA.getAttribute("href"));
		driver.get(newsA.getAttribute("href"));
	}
	
	public static void CheckNews(WebDriver driver) {
		List<WebElement> newsAll = new ArrayList<WebElement>();
		List<WebElement> images = new ArrayList<WebElement>();
		int newsNumVis = 0, newsNum = 0;
		
		newsNumVis = driver.findElements(By.cssSelector("div[class='block alt']")).size() + driver.findElements(By.cssSelector("div[class='block']")).size();
		if (newsNumVis == 10) {
			System.out.println("\n\tNumber of visible news blocks is " + newsNumVis + " as expected");
		}
		else {
			System.out.println("\n\tError: Number of visible news blocks is not 10 but " + newsNumVis);
		}
		
		try {
			driver.findElement(By.cssSelector("div.more p span")).click();
			newsNum = driver.findElements(By.cssSelector("div[class='block alt']")).size() + driver.findElements(By.cssSelector("div[class='block']")).size();
			if (newsNum > newsNumVis) {
				System.out.println("\n\tNumber of visible news blocks after loading more is " + newsNumVis);
			}
			else {
				System.out.println("\n\tError: Number of visible news blocks did not increase after loading more");
			}
		}
		catch(Exception exp) {
			System.out.println("\n\tError: Unable to load more news");
		}
		
		newsAll = driver.findElements(By.cssSelector("div.block"));
		images = driver.findElements(By.cssSelector("div.news_img img"));
		if (newsAll.size() == images.size()) {
			System.out.println("\n\tAll news have an image, verifying links...");
		}
		
		String lnkStatus = "";
		String imgUrlStr = "";
		for (int i = 0; i < images.size(); i++) {
			imgUrlStr = images.get(i).getAttribute("src");
			try {
				lnkStatus = isLinkBroken(new URL(imgUrlStr));
				if (!(lnkStatus.contains("OK"))) {
					System.out.println("\tImage: " + imgUrlStr + ", Link status: " + lnkStatus);
				}
			}
			catch(Exception exp) {
				System.out.println("\tError: unable to verify link accessibility for image №" + i);
			}
		}
	}
}
