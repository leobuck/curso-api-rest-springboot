package com.leo.cursoapirest.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import com.leo.cursoapirest.repository.TelefoneRepository;
import com.leo.cursoapirest.repository.UsuarioRepository;
import com.leo.cursoapirest.service.UserDetailServiceImp;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private UserDetailServiceImp userDetailServiceImp;

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
	public ResponseEntity<Page<Usuario>> usuarios() {
		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));
		
		Page<Usuario> lista = usuarioRepository.findAll(page);
		
		return ResponseEntity.ok(lista);
	}
	
	@GetMapping(value = "/page/{pagina}", produces = "application/json")
	@CacheEvict(value = "cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuariosPorPagina(@PathVariable("pagina") int pagina) {
		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
		
		Page<Usuario> lista = usuarioRepository.findAll(page);
		
		return ResponseEntity.ok(lista);
	}
	
	@GetMapping(value = "/usuariosPorNome/{nome}", produces = "application/json")
	@CacheEvict(value = "cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuariosPorNome(@PathVariable("nome") String nome) {
		Page<Usuario> lista = null;
		
		PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
		
		if (nome == null || (nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) {
			lista = usuarioRepository.findAll(pageRequest);
		} else {
			lista = usuarioRepository.findUserByNomePage(nome, pageRequest);
		}
		
		return ResponseEntity.ok(lista);
	}
	
	@GetMapping(value = "/usuariosPorNome/{nome}/page/{pagina}", produces = "application/json")
	@CacheEvict(value = "cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuariosPorNomePorPagina(
			@PathVariable("nome") String nome,
			@PathVariable("pagina") int pagina) {
		Page<Usuario> lista = null;
		
		PageRequest pageRequest = PageRequest.of(pagina, 5, Sort.by("nome"));
		
		if (nome == null || (nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) {
			lista = usuarioRepository.findAll(pageRequest);
		} else {
			lista = usuarioRepository.findUserByNomePage(nome, pageRequest);
		}
		
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
		
		userDetailServiceImp.insereRolePadrao(usuarioSalvo.getId());
		
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
	
	@DeleteMapping(value = "/deletarTelefone/{id}", produces = "application/text")
	public ResponseEntity<String> deletarTelefone(@PathVariable("id") Long id) {
		telefoneRepository.deleteById(id);
		
		return ResponseEntity.ok("ok");
	}
}
