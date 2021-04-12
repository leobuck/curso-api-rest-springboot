package com.leo.cursoapirest.model;

import java.io.Serializable;

public class UsuarioDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String login;
	private String nome;

	public UsuarioDTO(Usuario usuario) {
		this.login = usuario.getLogin();
		this.nome = usuario.getNome();
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
