package com.ruppyrup.twitterintegration.service;

import com.ruppyrup.twitterintegration.dao.DAOPerson;
import com.ruppyrup.twitterintegration.dao.PersonRepository;
import com.ruppyrup.twitterintegration.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Transformer
    public DAOPerson savePerson(Message<Person> message) {
        DAOPerson daoPerson = new DAOPerson(message.getPayload());
        personRepository.save(daoPerson);
        return daoPerson;
    }
}
