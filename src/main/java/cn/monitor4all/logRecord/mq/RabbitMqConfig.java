package cn.monitor4all.logRecord.mq;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableRabbit
public class RabbitMqConfig {

    private final String rabbitHost;
    private final int rabbitPort;
    private final String exchange;
    private final String queue;
    private final String routingKey;
    private final String username;
    private final String password;

    public RabbitMqConfig(
            @Value("${log-record.rabbitmq.host}") String rabbitHost,
            @Value("${log-record.rabbitmq.port}") int rabbitPort,
            @Value("${log-record.rabbitmq.username}") String username,
            @Value("${log-record.rabbitmq.password}") String password,
            @Value("${log-record.rabbitmq.queue-name}") String queue,
            @Value("${log-record.rabbitmq.routing-key}") String routingKey,
            @Value("${log-record.rabbitmq.exchange-name}") String exchange) {
        log.info("LogRecord RabbitMqConfig rabbitHost [{}] rabbitPort [{}] exchange [{}] queue [{}] routingKey [{}]",
                rabbitHost, rabbitPort, exchange, queue, routingKey);
        this.rabbitHost = rabbitHost;
        this.rabbitPort = rabbitPort;
        this.queue = queue;
        this.routingKey = routingKey;
        this.exchange= exchange;
        this.username= username;
        this.password= password;
    }

    @Bean
    ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(rabbitHost, rabbitPort);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);
        return cachingConnectionFactory;
    }

    @Bean
    DirectExchange rubeExchange() {
        return new DirectExchange(exchange, true, false);
    }

    @Bean
    public Queue rubeQueue() {
        return new Queue(queue, true);
    }

    @Bean
    Binding rubeExchangeBinding(DirectExchange rubeExchange, Queue rubeQueue) {
        return BindingBuilder.bind(rubeQueue).to(rubeExchange).with(routingKey);
    }

    @Bean
    public RabbitTemplate rubeExchangeTemplate(ConnectionFactory rabbitConnectionFactory) {
        RabbitTemplate r = new RabbitTemplate(rabbitConnectionFactory);
        r.setExchange(exchange);
        r.setRoutingKey(routingKey);
        r.setConnectionFactory(rabbitConnectionFactory);
        return r;
    }
}
