package com.leo.cursoapirest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.leo.cursoapirest.ApplicationContextLoad;
import com.leo.cursoapirest.model.Usuario;
import com.leo.cursoapirest.repository.UsuarioRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	private static final long EXPIRATION_TIME = 172800000;
	
	private static final String SECRET = "SenhaExtremamenteSecreta";
	
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING = "Authorization";
	
	public void addAuthentication(HttpServletResponse response, String username) 
			throws IOException {
		
		String JWT = Jwts.builder()
				.setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET)
				.compact();
		
		String token = TOKEN_PREFIX + " " + JWT;
		
		response.addHeader(HEADER_STRING, token);
		
		ApplicationContextLoad.getApplicationContext()
			.getBean(UsuarioRepository.class)
			.atualizaToken(JWT, username);
		
		liberacaoCors(response);
		
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
	}
	
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
		String token = request.getHeader(HEADER_STRING);
		
		try {
			
			if (token != null) {
				String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();
				
				String user = Jwts.parser()
						.setSigningKey(SECRET)
						.parseClaimsJws(tokenLimpo)
						.getBody()
						.getSubject();
				if (user != null) {
					Usuario usuario = ApplicationContextLoad.getApplicationContext()
							.getBean(UsuarioRepository.class)
							.findUserByLogin(user);
					
					if (usuario != null) {
						
						if (tokenLimpo.equalsIgnoreCase(usuario.getToken())) {
							
							return new UsernamePasswordAuthenticationToken(
									usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
						}
					}
				}
			}
		
		} catch (ExpiredJwtException e) {
			try {
				response.getOutputStream().println("Seu TOKEN está expirado, faça novamente o login"
						+ " ou informe um novo TOKEN para autenticação");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			liberacaoCors(response);
		}
		
		return null;
	}

	private void liberacaoCors(HttpServletResponse response) {
		
		if (response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		if (response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		
		if (response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		
		if (response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}
}
