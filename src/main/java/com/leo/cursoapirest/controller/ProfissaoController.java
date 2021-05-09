package com.leo.cursoapirest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leo.cursoapirest.model.Profissao;
import com.leo.cursoapirest.repository.ProfissaoRepository;

@RestController
@RequestMapping(value = "/profissao")
public class ProfissaoController {

	@Autowired
	private ProfissaoRepository profissaoRepository;
	
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<Profissao>> profissoes() {
		List<Profissao> lista = profissaoRepository.findAll();
		return ResponseEntity.ok(lista);
	}
}
