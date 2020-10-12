package com.ruppyrup.twitterintegration.transformers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruppyrup.twitterintegration.dto.MessageDto;
import com.ruppyrup.twitterintegration.model.Person;
import com.ruppyrup.twitterintegration.dto.PersonDto;
import com.ruppyrup.twitterintegration.service.StarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class MessageConverter {

    @Autowired
    StarService starService;

    @Transformer
    public Person createPerson(Message<PersonDto> event) throws IOException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        PersonDto result = objectMapper.readValue(event.getPayload(), PersonDto.class);
        Person person = new Person(event.getPayload());
        person.setKafkaOffset((Long) event.getHeaders().get("kafka_offset"));
        return person;
    }

    @Transformer
    public PersonDto extractObject(Message<byte[]> event) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = new String(event.getPayload());
        return objectMapper.readValue(payload, PersonDto.class);
    }

    @Transformer
    public PersonDto extractPerson(MessageDto messageDto) {
        Person person = new Person(messageDto.getPersonDto());
        Long creationTime = (Long) messageDto.getHeaders().get("sendTime");
        person.setKafkaOffset(creationTime);
        return messageDto.getPersonDto();
    }

    @Transformer
    public Person changeAge(Message<Person> message) {
        message.getPayload().setAge(15);
        return message.getPayload();
    }

    @Transformer
    public Person enrichObject(Message<Person> message) {
        Person person = message.getPayload();
        String s = starService.getStars(person.getName());
        person.setName(s);
        return person;
    }

}
