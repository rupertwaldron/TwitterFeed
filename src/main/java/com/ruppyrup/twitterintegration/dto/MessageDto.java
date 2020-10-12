package com.ruppyrup.twitterintegration.dto;

import com.ruppyrup.twitterintegration.dto.PersonDto;
import lombok.Data;
import org.springframework.messaging.MessageHeaders;

@Data
public class MessageDto {
    private final MessageHeaders headers;
    private final PersonDto personDto;

    public MessageDto(MessageHeaders headers, PersonDto personDto) {
        this.headers = headers;
        this.personDto = personDto;
    }
}
