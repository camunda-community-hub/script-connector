package io.camunda.community.connector.script;

import java.util.Arrays;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
    if (Arrays.asList(args).contains("test")) {
      System.exit(0);
    }
  }
}
