package com.ruppyrup.twitterintegration.config;

import com.ruppyrup.twitterintegration.model.Person;
import com.ruppyrup.twitterintegration.serializers.PersonDeSerializer;
import com.ruppyrup.twitterintegration.service.PersonService;
import com.ruppyrup.twitterintegration.transformers.MessageConverter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter.ListenerMode;

@Configuration
public class KafkaConfig {

    private static final String PERSON_PUBLISHER_CHANNEL = "person_publisher_channel";
    private static final String INCOMING_MESSAGE_CHANNEL = "incoming_message_channel";
    private static final String ENRICHMENT_MESSAGE_CHANNEL = "enrichment_message_channel";
    private static final String WIRE_TAP_CHANNEL = "wire_tap_channel";

    @Autowired
    MessageConverter messageConverter;

    @Autowired
    PersonService personService;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.topic}")
    private String springIntegrationKafkaTopic;

    @Bean
    public IntegrationFlow kafkaReader() throws Exception {
        return IntegrationFlows
                .from(Kafka.messageDrivenChannelAdapter(listener(), ListenerMode.record))
                .log(LoggingHandler.Level.INFO)
                .transform(messageConverter, "createPerson")
                .log(LoggingHandler.Level.INFO)
                .channel(PERSON_PUBLISHER_CHANNEL)
                .get();
    }

    @Bean
    public IntegrationFlow processMessage() {
        return IntegrationFlows.from(PERSON_PUBLISHER_CHANNEL)
                .log(LoggingHandler.Level.INFO)
                .transform(Message.class, m -> {
                    ((Person) m.getPayload()).setAge(20);
                    return m;
                })
                .log(LoggingHandler.Level.INFO)
                .transform(messageConverter, "changeAge")
                .channel(ENRICHMENT_MESSAGE_CHANNEL)
                //.channel("queuereader")
                .get();
    }

    @Bean
    public IntegrationFlow processMessage2() {
        return IntegrationFlows.from(WIRE_TAP_CHANNEL)
                .log(LoggingHandler.Level.INFO)
                .transform(personService, "savePerson")
                //.channel("queuereader")
                .handle(msg -> System.out.println("Wiretap channel : " + msg.getPayload()))
                .get();
    }

    @Bean
    public IntegrationFlow enrichmentFlow() {
        return IntegrationFlows.from(ENRICHMENT_MESSAGE_CHANNEL)
                .transform(messageConverter, "enrichObject")
                .log(LoggingHandler.Level.INFO)
                .wireTap(WIRE_TAP_CHANNEL)
                .handle(msg -> System.out.println("After enrichment process: " + msg.getPayload()))
                .get();
    }

    @Bean(name = ENRICHMENT_MESSAGE_CHANNEL)
    public ExecutorChannel enrichmentChannel() {
        return new ExecutorChannel(getAsyncExecutor());
    }

    @Bean
    public Executor getAsyncExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean(name = INCOMING_MESSAGE_CHANNEL)
    public MessageChannel getMessageChannel() {
        return MessageChannels.direct().get();
    }

    @Bean(name = PERSON_PUBLISHER_CHANNEL)
    public SubscribableChannel httpInAdapterPubSubChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean(name = WIRE_TAP_CHANNEL)
    public MessageChannel getWireTapChannel() {
        return MessageChannels.direct().get();
    }

    @ServiceActivator(inputChannel = "queuereader")
    public void Print(Message<?> msg)  {

        System.out.println(msg.getPayload());
    }

    @Bean
    public KafkaMessageListenerContainer listener() {
        return new KafkaMessageListenerContainer(consumerFactory(), new ContainerProperties(this.springIntegrationKafkaTopic));
    }

    @Bean
    public ConsumerFactory consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PersonDeSerializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafkaListener");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory(props);
    }

}
