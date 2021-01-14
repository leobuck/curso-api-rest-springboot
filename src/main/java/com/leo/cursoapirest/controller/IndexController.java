package com.leo.cursoapirest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leo.cursoapirest.model.Usuario;

@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<Usuario>> index() {
		Usuario usuario = new Usuario();
		usuario.setId(1L);
		usuario.setLogin("leo");
		usuario.setNome("Leo");
		usuario.setSenha("123");
		
		Usuario usuario2 = new Usuario();
		usuario2.setId(2L);
		usuario2.setLogin("ana");
		usuario2.setNome("Ana");
		usuario2.setSenha("123");
		
		List<Usuario> usuarios = new ArrayList<Usuario>();
		usuarios.add(usuario);
		usuarios.add(usuario2);
		
		return ResponseEntity.ok(usuarios);
	}
}
