package com.leo.cursoapirest.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.leo.cursoapirest.model.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

	@Query("SELECT u FROM Usuario u WHERE u.login = ?1")
	Usuario findUserByLogin(String login);
}
