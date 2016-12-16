package hello;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

  private final RestTemplate restTemplate;

  private List<String>convertStringToList(String listAsString) {
    List<String> returnList = new ArrayList<String>();
    for(String value : listAsString.split(",")) {
    	returnList.add(value.trim());
    }
    return returnList;
  }
  
  public BookService(RestTemplate rest) {
    this.restTemplate = rest;
  }

  @HystrixCommand(fallbackMethod = "reliable")
  public String readingList() {
    URI uri = URI.create("http://localhost:8090/recommended");

    return this.restTemplate.getForObject(uri, String.class);
  }
  
  @HystrixCommand(fallbackMethod = "reliableAll")
  public String allBooks() {
    URI uri = URI.create("http://localhost:8090/all");

	return this.restTemplate.getForObject(uri, String.class);	  
  }
  
  @HystrixCommand(fallbackMethod = "reliableGenres")
  public List<String> genres() {
    URI uri = URI.create("http://localhost:8090/genres");
    return convertStringToList(this.restTemplate.getForObject(uri, String.class));
  } 
  
  @HystrixCommand(fallbackMethod = "reliableBooksByGenres")
  public List<String> booksByGenre(String genre) {
    URI uri = URI.create("http://localhost:8090/all/" + genre);

    return convertStringToList(this.restTemplate.getForObject(uri, String.class));
  }   

  public String reliableAll() {
	  return "all books";  
  }
  
  public String reliable() {
    return "Cloud Native Java (O'Reilly)";
  }
  
  public List<String> reliableGenres() {
	  return convertStringToList("Fiction, Computer");
  }
  
  public List<String> reliableBooksByGenres(String genre) {
	  return convertStringToList("Book 1 in " + genre);
  } 
}