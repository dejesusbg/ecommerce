package com.edu.unimagdalena.productservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	@Override
	public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
		Object realmAccessObj = jwt.getClaims().get("realm_access");

		if (!(realmAccessObj instanceof Map)) {
			return List.of();
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> realmAccess = (Map<String, Object>) realmAccessObj;

		if (realmAccess == null || realmAccess.isEmpty()) {
			return List.of();
		}

		Object rolesObj = realmAccess.get("roles");

		if (!(rolesObj instanceof List)) {
			return List.of();
		}

		@SuppressWarnings("unchecked")
		List<String> roles = (List<String>) rolesObj;

		return roles.stream()
				.map(roleName -> "ROLE_" + roleName)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}
}