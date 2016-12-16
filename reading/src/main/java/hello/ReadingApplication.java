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
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.web.client.RestTemplate;

@EnableCircuitBreaker
@RestController
@SpringBootApplication
public class ReadingApplication {

  @Autowired
  private BookService bookService;

  @Bean
  public RestTemplate rest(RestTemplateBuilder builder) {
    return builder.build();
  }

  @RequestMapping("/to-read")
  public String toRead() {
    return bookService.readingList();
  }
  
  @RequestMapping("to-read/{genre}")
  public String toRead(@PathVariable String genre) {
	  
	  // first we get all genres
	  List<String> genres = bookService.genres();
	  
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

  public static void main(String[] args) {
    SpringApplication.run(ReadingApplication.class, args);
  }
}