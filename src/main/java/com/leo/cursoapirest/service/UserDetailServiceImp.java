package com.leo.cursoapirest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.leo.cursoapirest.model.Usuario;
import com.leo.cursoapirest.repository.UsuarioRepository;

@Service
public class UserDetailServiceImp implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findUserByLogin(username);
		
		if (usuario == null) {
			throw new UsernameNotFoundException("Usuário não encontrado!");
		}
		
		return new User(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
	}
	
	public void insereRolePadrao(Long idUsuario) {
		String constraint = usuarioRepository.consultaConstraintRole();
		
		if (constraint != null && !constraint.isEmpty())
			jdbcTemplate.execute("alter table usuario_role drop constraint " + constraint);
		
		usuarioRepository.insereRolePadrao(idUsuario);		
	}

}
