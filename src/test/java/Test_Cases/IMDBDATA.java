package Test_Cases;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
public class IMDBDATA {
	private WebDriver driver;
	@Parameters("browser")
	@BeforeClass
	public void beforeClass(String browser) {
		if(browser.equalsIgnoreCase("firefox"))
		{
			System.setProperty("webdriver.gecko.driver","/Learn/IMDB/Drivers/geckodriver-v0.11.1-win64/geckodriver.exe");
			driver = new FirefoxDriver();
		}
		else if(browser.equalsIgnoreCase("chrome"))
		{
			System.setProperty("webdriver.chrome.driver","/Learn/IMDB/Drivers/chromedriver.exe");
			driver = new ChromeDriver();
		}
	}
	@Test
	public void IMDB_DATA() throws ClassNotFoundException, InterruptedException, SQLException {
		Class.forName("org.sqlite.JDBC");
		Connection connection = null;
		connection = DriverManager.getConnection("jdbc:sqlite:/Learn/IMDB/Database/ImdbData.db");
		Statement statement = connection.createStatement();
		try
		{
			// create a database connection

			//Navigate to URL
			driver.get("http://www.imdb.com/search/title?groups=top_250&sort=user_rating");
			Thread.sleep(20);
			//Drop existing table
			statement.executeUpdate("DROP TABLE IF EXISTS IMDB");
			//Create Table
			statement.executeUpdate("CREATE TABLE IMDB (Name varchar(255),Year varchar(255),rating varchar(255))");
			PreparedStatement p;
			int i=1;
			while(i<=50){
				String Moviename= driver.findElement(By.xpath(".//*[@id='main']/div/div/div[3]/div["+i+"]/div[3]/h3/a")).getText();
				String Year = driver.findElement(By.xpath(".//*[@id='main']/div/div/div[3]/div["+i+"]/div[3]/h3/span[2]")).getText();
				String rating_star = driver.findElement(By.xpath(".//*[@id='main']/div/div/div[3]/div["+i+"]/div[3]/div/div[1]/strong")).getText();
				String updatedMoviename=Moviename.replaceAll("\'","");
				//System.out.println(updatedMoviename+Year+"\t"+rating_star);

				p = connection.prepareStatement("INSERT INTO IMDB(Name,Year,rating) values('"+updatedMoviename+"','"+Year+"','"+rating_star+"')");
				p.executeUpdate();

				if(Moviename.matches("Dog Day Afternoon"))
				{
					System.out.println("Done data collected");
					break;
				}
				Thread.sleep(30);
				if(i==50 && driver.findElement(By.className("next-page")).isDisplayed())
				{

					driver.findElement(By.className("next-page")).click();
					i=0;
				}
				i++;
				/*boolean myLink =false;*/
				/*p = connection.prepareStatement("INSERT INTO IMDB(Name,Year,rating) values('"+Moviename+"','"+Year+"','"+rating_star+"')");
				statement.setQueryTimeout(30); 
				p.addBatch();*/
			} 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		ResultSet resultSet = statement.executeQuery("SELECT * from IMDB");
		while(resultSet.next())
		{
			// iterate & read the result set
			System.out.println("name = " + resultSet.getString("Name"));
			System.out.println("Year = " + resultSet.getString("Year"));
			System.out.println("rating = " + resultSet.getString("rating"));

		}
	}
	@AfterClass
	public void afterClass() {
		driver.close();
	}
}
