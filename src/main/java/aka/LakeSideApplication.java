package aka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"aka.security", "other.package"})
public class LakeSideApplication {

	public static void main(String[] args) {
		SpringApplication.run(LakeSideApplication.class, args);
		System.out.println("lakeSide");
	}

}
