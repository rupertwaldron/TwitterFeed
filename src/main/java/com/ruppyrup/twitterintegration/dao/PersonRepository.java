package com.ruppyrup.twitterintegration.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CrudRepository<DAOPerson, Long> {
}
