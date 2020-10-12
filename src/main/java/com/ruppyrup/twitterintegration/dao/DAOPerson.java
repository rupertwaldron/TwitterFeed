package com.ruppyrup.twitterintegration.dao;

import com.ruppyrup.twitterintegration.model.Person;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "persons")
@NoArgsConstructor
public class DAOPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @Column
    private int age;

    @Column
    private String comment;

    @Column
    private String timestamp;

    @Column
    private long kafkaOffset;

    public DAOPerson(Person person) {
        name = person.getName();
        age = person.getAge();
        comment = person.getComment();
        timestamp = person.getTimestamp();
        kafkaOffset = person.getKafkaOffset();
    }
}
