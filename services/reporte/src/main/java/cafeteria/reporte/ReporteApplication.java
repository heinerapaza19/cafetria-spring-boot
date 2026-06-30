package cafeteria.reporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ReporteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReporteApplication.class, args);
	}
}