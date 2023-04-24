package iob;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class DuetApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(DuetApplication.class);
		
		Properties properties = new Properties();
		properties.put("logging.file.name", "logs/main/" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now()) + ".log");
		application.setDefaultProperties(properties);
		
		application.run(args);
	}

}
