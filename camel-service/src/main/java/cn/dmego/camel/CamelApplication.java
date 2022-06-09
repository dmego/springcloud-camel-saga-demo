package cn.dmego.camel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author dmego
 * @date 2022/06/07 17:21
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class CamelApplication {

    public static void main(String[] args) {
        SpringApplication.run(CamelApplication.class, args);
    }
}
