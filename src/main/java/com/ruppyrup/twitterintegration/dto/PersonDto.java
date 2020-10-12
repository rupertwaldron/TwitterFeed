package com.ruppyrup.twitterintegration.dto;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto implements Serializable {
    private String name;
    private int age;
    private String comment;
    private String timestamp;
}
