package com.example.demo.JWT_Security;


public class SecurityConstants {


	private SecurityConstants() {

	}


	public static final String SECRET = "GenerateToken";
	

	public static final long EXPIRATION_TIME = 864_000_000; // 10 days
	

	public static final String TOKEN_PREFIX = "Bearer ";
	

	public static final String HEADER_STRING = "Authorization";

}
