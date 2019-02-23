package ca.unb.ktb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KickTheBucketApplication {

	public static void main(String[] args) {
		SpringApplication.run(KickTheBucketApplication.class, args);
	}
}
