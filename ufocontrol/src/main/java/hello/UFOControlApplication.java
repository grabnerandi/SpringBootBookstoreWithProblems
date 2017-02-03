package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.web.client.RestTemplate;

@EnableCircuitBreaker
@RestController
@SpringBootApplication
public class UFOControlApplication {

  // versioning!
  @Value("${reading.version}")
  private String currentVersion;
  static String VERSION_V1 = "1";
  static String VERSION_V2 = "2";
  
  @Value("${reading.processingtime}")
  private int processingTime;
  
  /**
   * Just simulates some processing. time can be configured through reading.processingtime
   */
  public void doProcessing() {
	  if(processingTime <= 0) processingTime = 100;
	  try {
		java.lang.Thread.sleep(processingTime);
	} catch (InterruptedException e) { 
		// there should really be no problem wkth this :-)
	}
  }  
	
  @Autowired
  private BookService bookService;

  @Bean
  public RestTemplate rest(RestTemplateBuilder builder) {
    return builder.build();
  }

  @RequestMapping(path = "/", produces = "text/html")
  public String defaultHandler() {
	  doProcessing();
	  return "<html><body>Here are the available REST APIs:" + 
           "<br><a href=\"/genres\">/genres</a>" + 
           "<br><a href=\"/to-read\">/to-read</a>" + 
           "<br><a href=\"/to-read/Fiction\">/to-read/{Genre}</a>" + 
           "<br><a href=\"/to-read/SomeOtherGenre/v1\">/to-read/{Genre}/v1</a>" + 
           "<br><a href=\"/to-read/SomeOtherGenre/v2\">/to-read/{Genre}/v2</a>" + 
           "<br>Current URL to Bookstore is: " + bookService.getBookstoreUrl() +
    	   "<body></html>";
  }
  
  @RequestMapping(value = "/version")
  public String setDefaultVersion() {
	  return currentVersion;
  }
      
  @RequestMapping("/genres")
  public String genres() {
	  doProcessing();
	  return BookService.convertListToString(bookService.genres());
  }
    
  @RequestMapping("/to-read")
  public String toRead() {
	  doProcessing();
	  return bookService.readingList();
  }
  
  @RequestMapping("to-read/{genre}")
  public String toRead(@PathVariable String genre) {
	  return toReadImpl(genre, currentVersion);
  }
  
  @RequestMapping("to-read/{genre}/v1")
  public String toReadV1(@PathVariable String genre) {
	  return toReadImpl(genre, VERSION_V1);
  }
  
  @RequestMapping("to-read/{genre}/v2")
  public String toReadV2(@PathVariable String genre) {
	  return toReadImpl(genre, VERSION_V2);
  }  
  
  /**
   * This method actually implements the logic of reading
   * It provides two versions that can be used via the version / feature toggle parameter
   * @param genre
   * @param version
   * @return
   */
  protected String toReadImpl(String genre, String version) {
	  doProcessing();
	  
	  // first we get all genres and validate if the requested genre actually exists
	  boolean genreExists = false;
	  List<String> genres = bookService.genres();
	  for(String genreItem : genres) {
		  if(genreItem.equalsIgnoreCase(genre)) genreExists = true;
	  }
	  
	  // version 1 is more efficient as it directly calls the backend
	  if(version.equalsIgnoreCase(VERSION_V1)) {
		  if(!genreExists)
			  return bookService.allBooks();
		  
		  return BookService.convertListToString(bookService.booksByGenre(genre));
	  } else {
		  // now we do something very inefficient - we iterate through the list of genres - get the books by genre and stop that list if we found the genre we are looking for
		  // this will result in potentially several calls to the backend
		  for(String genreItem : genres) {
			  String readingList = bookService.readingList();
			  List<String> booksByGenre = bookService.booksByGenre(genreItem);
			  if(genreItem.equalsIgnoreCase(genre)) {
				  return readingList + "," + booksByGenre.toString();
			  }
		  }
		  
		  // if we havent found anything we simply return the full list of books
		  return bookService.allBooks();
	  }
  }

  public static void main(String[] args) {
    SpringApplication.run(UFOControlApplication.class, args);
  }
}