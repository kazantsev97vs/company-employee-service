package spring.cloud.netflix.eureka.employee.statistic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SpringBoot2RestServiceBasicStatisticApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBoot2RestServiceBasicStatisticApplication.class, args);
	}
}