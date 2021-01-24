package com.leo.cursoapirest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leo.cursoapirest.model.Usuario;
import com.leo.cursoapirest.repository.UsuarioRepository;

@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
//	@Autowired
//	private TelefoneRepository telefoneRepository;

	@GetMapping(value = "/{id}/relatorio/{venda}", produces = "application/json")
	public ResponseEntity<Usuario> relatorio(@PathVariable(value = "id") Long id, @PathVariable(value = "venda") Long venda) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return ResponseEntity.ok(usuario.get());
	}
	
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> index(@PathVariable(value = "id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return ResponseEntity.ok(usuario.get());
	}
	
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<Usuario>> usuarios() {
		List<Usuario> lista = (List<Usuario>) usuarioRepository.findAll();
		
		return ResponseEntity.ok(lista);
	}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
		usuario.getTelefones().forEach(x -> x.setUsuario(usuario));
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return ResponseEntity.ok(usuarioSalvo);
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		usuario.getTelefones().forEach(x -> x.setUsuario(usuario));
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return ResponseEntity.ok(usuarioSalvo);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> deletar(@PathVariable("id") Long id) {
		usuarioRepository.deleteById(id);
		
		return ResponseEntity.ok("ok");
	}
}
