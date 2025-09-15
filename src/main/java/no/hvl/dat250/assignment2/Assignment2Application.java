package no.hvl.dat250.assignment2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Assignment2Application {
    public static void main(String[] args) {
      SpringApplication.run(Assignment2Application.class, args);
    }
}