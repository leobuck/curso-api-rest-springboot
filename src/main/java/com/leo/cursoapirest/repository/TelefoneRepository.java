package com.leo.cursoapirest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.leo.cursoapirest.model.Telefone;

@Repository
public interface TelefoneRepository extends CrudRepository<Telefone, Long> {

}
