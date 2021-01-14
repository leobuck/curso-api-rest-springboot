package com.leo.cursoapirest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity index(@RequestParam(value = "nome", defaultValue = "Nome não informado", required = true) String nome,
			@RequestParam("salario") Long salario) {
		System.out.println("Parâmetro nome: " + nome);
		System.out.println("Parâmetro salario: " + salario);
		return new ResponseEntity("Olá " + nome + ", salário R$ " + salario, HttpStatus.OK);
	}
}
