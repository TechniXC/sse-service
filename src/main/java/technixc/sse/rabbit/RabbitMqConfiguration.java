package technixc.sse.rabbit;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.*;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class RabbitMqConfiguration {

    private final RabbitProperties rabbitProperties;

    @Bean
    Mono<Connection> rabbitMqConnection() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(rabbitProperties.getHost());
        connectionFactory.setPort(rabbitProperties.getPort());
        connectionFactory.useNio();
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        return Mono.fromCallable(() -> connectionFactory.newConnection("reactor-rabbit"))
                .cache();
    }

    @Bean
    Sender sender(Mono<Connection> rabbitMqConnection) {
        return RabbitFlux.createSender(new SenderOptions().connectionMono(rabbitMqConnection));
    }

    @Bean
    Receiver receiver(Mono<Connection> rabbitMqConnection) {
        return RabbitFlux.createReceiver(new ReceiverOptions().connectionMono(rabbitMqConnection));
    }

    @PreDestroy
    public void close() throws Exception {
        Objects.requireNonNull(rabbitMqConnection().block()).close();
    }
}
