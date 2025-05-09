package com.smartcontact.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	UserDetailsService getUserDetailsService() {
		return new CustomeUserDetailsService();
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

		return bCryptPasswordEncoder;
	}

	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
		dao.setUserDetailsService(this.getUserDetailsService());
		dao.setPasswordEncoder(passwordEncoder());

		return dao;
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/user/**").hasRole("USER").requestMatchers("/admin/**")
				.hasAuthority("ADMIN").requestMatchers("/**").permitAll())

				.formLogin(form -> form.loginPage("/login") // Custom login page URL
						.defaultSuccessUrl("/user/index", true) // Redirect to index on successful login
						.permitAll())
				.logout(logout -> logout.logoutUrl("/logout") // Logout URL
						.logoutSuccessUrl("/login?logout") // Redirect after logout
						.permitAll())
				.csrf(csrf -> csrf.disable());

		return http.build();
	}
}
