package com.ruppyrup.twitterintegration.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruppyrup.twitterintegration.dto.PersonDto;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

@NoArgsConstructor
public class PersonDeSerializer implements Deserializer {
    @Override
    public void configure(Map configs, boolean isKey) {

    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        ObjectMapper mapper = new ObjectMapper();
        PersonDto personDto = null;
        try {
            personDto = mapper.readValue(data, PersonDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return personDto;
    }

    @Override
    public void close() {

    }
}
