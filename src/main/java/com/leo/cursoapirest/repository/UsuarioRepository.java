package com.leo.cursoapirest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.leo.cursoapirest.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	@Query("SELECT u FROM Usuario u WHERE u.login = ?1")
	Usuario findUserByLogin(String login);
	
	@Query("SELECT u FROM Usuario u WHERE u.nome LIKE %?1%")
	List<Usuario> findUserByNome(String nome);
	
	@Transactional
	@Modifying
	@Query(value = "update usuario set token = ?1 where login = ?2", nativeQuery = true)
	void atualizaToken(String token, String login);
	
	@Query(value = "select constraint_name from information_schema.constraint_column_usage "
			+ "where table_name = 'usuario_role' and column_name = 'role_id' "
			+ "and constraint_name <> 'unique_usuario_role';", nativeQuery = true)
	String consultaConstraintRole();
	
	@Transactional
	@Modifying
	@Query(value = "alter table usuario_role drop constraint ?1;", nativeQuery = true)
	void removeConstraintRole(String constraint);
	
	@Transactional
	@Modifying
	@Query(value = "insert into usuario_role (usuario_id, role_id) "
			+ "values (?1, (select id from \"role\" where nome = 'ROLE_USER'));", nativeQuery = true)
	void insereRolePadrao(Long idUsuario);
}
