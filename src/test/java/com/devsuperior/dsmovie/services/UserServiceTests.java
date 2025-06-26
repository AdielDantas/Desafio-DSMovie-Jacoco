package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Mock
	private CustomUserUtil userUtil;

	private String existingUserName;
	private String nonExistingUserName;
	private UserEntity user;
	private List<UserDetailsProjection> userDetails;

	@BeforeEach
	void setUp() throws Exception {
		existingUserName = "maria@gmail.com";
		nonExistingUserName = "user@gmail.com";
		user = UserFactory.createUserEntity();
		userDetails = UserDetailsFactory.createCustomAdminUser(existingUserName);

		when(repository.findByUsername(existingUserName)).thenReturn(Optional.of(user));
		when(repository.findByUsername(nonExistingUserName)).thenReturn(Optional.empty());

		when(repository.searchUserAndRolesByUsername(existingUserName)).thenReturn(userDetails);
		when(repository.searchUserAndRolesByUsername(nonExistingUserName)).thenReturn(new ArrayList<>());
	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() throws Exception {
		when(userUtil.getLoggedUsername()).thenReturn(existingUserName);
		UserEntity result = service.authenticated();

		assertNotNull(result);
		assertEquals(result.getUsername(), existingUserName);
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() throws Exception {
		doThrow(ClassCastException.class).when(userUtil).getLoggedUsername();

		assertThrows(UsernameNotFoundException.class, () -> {
			service.authenticated();
		});
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() throws Exception {
		UserDetails result = service.loadUserByUsername(existingUserName);
		assertNotNull(result);
		assertEquals(result.getUsername(), existingUserName);
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() throws Exception {
		assertThrows(UsernameNotFoundException.class, () -> {
			service.loadUserByUsername(nonExistingUserName);
		});
	}
}
