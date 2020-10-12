package com.ruppyrup.twitterintegration.model;


import com.ruppyrup.twitterintegration.dto.PersonDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Person implements Serializable {
    private String name;
    private int age;
    private String comment;
    private String timestamp;
    private Long kafkaOffset;

    public Person(PersonDto personDto) {
        name = personDto.getName();
        age = personDto.getAge();
        comment = personDto.getComment();
        timestamp = personDto.getTimestamp();
    }
}
