package com.leo.cursoapirest.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leo.cursoapirest.ObjetoErro;
import com.leo.cursoapirest.model.Usuario;
import com.leo.cursoapirest.repository.UsuarioRepository;
import com.leo.cursoapirest.service.EmailService;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperaController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private EmailService emailService;
	
	@PostMapping(value = "/")
	public ResponseEntity<ObjetoErro> recuperar(@RequestBody Usuario login) throws MessagingException {
		ObjetoErro objetoErro = new ObjetoErro();
		
		Usuario usuario = usuarioRepository.findUserByLogin(login.getLogin());
		
		if (usuario == null) {
			objetoErro.setCode("404");
			objetoErro.setError("Usuário não encontrado");
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String senhaNova = dateFormat.format(Calendar.getInstance().getTime());
			
			usuarioRepository.atualizaSenha(new BCryptPasswordEncoder().encode(senhaNova), usuario.getId());
			
			emailService.enviarEmail("Recuperação de senha", usuario.getLogin(), "Sua nova senha é: " + senhaNova);
			
			objetoErro.setCode("200");
			objetoErro.setError("Recuperação enviado por e-mail");
		}
		
		return ResponseEntity.ok(objetoErro);
	}
}
