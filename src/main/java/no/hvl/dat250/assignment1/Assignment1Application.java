package no.hvl.dat250.assignment1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Assignment1Application {
    public static void main(String[] args) {
      SpringApplication.run(Assignment1Application.class, args);
    }
    @GetMapping("/")
    public String rootGreeting() {
        return "Successful demo for DAT250 🚀";
    }
}