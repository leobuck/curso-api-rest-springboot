package com.leo.cursoapirest.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.leo.cursoapirest.model.Usuario;
import com.leo.cursoapirest.model.UsuarioDTO;
import com.leo.cursoapirest.repository.UsuarioRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;

//	@CrossOrigin(origins = "http://localhost:8080/")
	@GetMapping(value = "/{id}/relatorio/{venda}", produces = "application/json")
	public ResponseEntity<UsuarioDTO> relatorio(@PathVariable(value = "id") Long id, @PathVariable(value = "venda") Long venda) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return ResponseEntity.ok(new UsuarioDTO(usuario.get()));
	}
	
//	@GetMapping(value = "/v1/{id}", produces = "application/json")
//	public ResponseEntity<Usuario> indexV1(@PathVariable(value = "id") Long id) {
//		Optional<Usuario> usuario = usuarioRepository.findById(id);
//		
//		System.out.println("Versão 1");
//		
//		return ResponseEntity.ok(usuario.get());
//	}
//	
//	@GetMapping(value = "/v2/{id}", produces = "application/json")
//	public ResponseEntity<Usuario> indexV2(@PathVariable(value = "id") Long id) {
//		Optional<Usuario> usuario = usuarioRepository.findById(id);
//		
//		System.out.println("Versão 2");
//		
//		return ResponseEntity.ok(usuario.get());
//	}
	
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> index(@PathVariable(value = "id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return ResponseEntity.ok(usuario.get());
	}
	
	@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v1")
	public ResponseEntity<Usuario> indexV1(@PathVariable(value = "id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		System.out.println("Versão 1");
		
		return ResponseEntity.ok(usuario.get());
	}
	
	@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v2")
	public ResponseEntity<Usuario> indexV2(@PathVariable(value = "id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		System.out.println("Versão 2");
		
		return ResponseEntity.ok(usuario.get());
	}
	
	@GetMapping(value = "/", produces = "application/json")
//	@Cacheable("cacheusuarios")
	@CacheEvict(value = "cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuarios() {
		List<Usuario> lista = (List<Usuario>) usuarioRepository.findAll();
		
		return ResponseEntity.ok(lista);
	}
	
	@GetMapping(value = "/usuariosPorNome/{nome}", produces = "application/json")
	@CacheEvict(value = "cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuariosPorNome(@PathVariable("nome") String nome) {
		
		List<Usuario> lista = usuarioRepository.findUserByNome(nome);
		
		return ResponseEntity.ok(lista);
	}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws IOException {
		usuario.getTelefones().forEach(x -> x.setUsuario(usuario));
		
		// Consumindo API ViaCEP
		URL url = new URL("https://viacep.com.br/ws/" + usuario.getCep() + "/json/");
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		while ((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
		Usuario usuarioAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
		usuario.setLogradouro(usuarioAux.getLogradouro());
		usuario.setComplemento(usuarioAux.getComplemento());
		usuario.setBairro(usuarioAux.getBairro());
		usuario.setLocalidade(usuarioAux.getLocalidade());
		usuario.setUf(usuarioAux.getUf());
		
		usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return ResponseEntity.ok(usuarioSalvo);
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		usuario.getTelefones().forEach(x -> x.setUsuario(usuario));
		
		Usuario usuarioTemp = usuarioRepository.findById(usuario.getId()).get();
		if (!usuarioTemp.getSenha().equals(usuario.getSenha())) {
			usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		}
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return ResponseEntity.ok(usuarioSalvo);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> deletar(@PathVariable("id") Long id) {
		usuarioRepository.deleteById(id);
		
		return ResponseEntity.ok("ok");
	}
}
