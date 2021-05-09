package com.leo.cursoapirest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leo.cursoapirest.ObjetoErro;
import com.leo.cursoapirest.model.Usuario;
import com.leo.cursoapirest.repository.UsuarioRepository;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperaController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@PostMapping(value = "/")
	public ResponseEntity<ObjetoErro> recuperar(@RequestBody Usuario login) {
		ObjetoErro objetoErro = new ObjetoErro();
		
		Usuario usuario = usuarioRepository.findUserByLogin(login.getLogin());
		
		if (usuario == null) {
			objetoErro.setCode("404");
			objetoErro.setError("Usuário não encontrado");
		} else {
			objetoErro.setCode("200");
			objetoErro.setError("Recuperação enviado por e-mail");
		}
		
		return ResponseEntity.ok(objetoErro);
	}
}
