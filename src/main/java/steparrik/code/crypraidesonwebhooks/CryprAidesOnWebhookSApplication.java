package steparrik.code.crypraidesonwebhooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import steparrik.code.crypraidesonwebhooks.telegram.TelegramProperties;

@SpringBootApplication
@EnableConfigurationProperties(TelegramProperties.class)
@EnableAspectJAutoProxy
public class CryprAidesOnWebhookSApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryprAidesOnWebhookSApplication.class, args);
    }

}
