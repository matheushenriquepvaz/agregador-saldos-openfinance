package ada.teste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AgredadorSaldos {

    public static void main(String[] args) {
        SpringApplication.run(AgredadorSaldos.class, args);
    }
}