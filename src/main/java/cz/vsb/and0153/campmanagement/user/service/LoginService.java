package cz.vsb.and0153.campmanagement.user.service;

import cz.vsb.and0153.campmanagement.server.security.PasswordEncryptor;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;
import cz.vsb.and0153.campmanagement.user.dto.UserDTO;
import cz.vsb.and0153.campmanagement.user.entity.User;
import cz.vsb.and0153.campmanagement.server.exception.ListException;
import cz.vsb.and0153.campmanagement.user.mapper.UserMapper;
import cz.vsb.and0153.campmanagement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

	private final PasswordEncryptor	encryptor;
	private final UserRepository repository;
	private final UserMapper mapper;

	public UserDTO register(
		String email,
		String firstName,
		String lastName,
		String password,
		String password2
	) throws ListException, GeneralSecurityException, UnitOfWorkException {
		List<String> errors = new ArrayList<>();

		// Validate if all fields are filled
		if (email == null || email.isBlank()) errors.add("Email is required.");
		if (firstName == null || firstName.isBlank()) errors.add("First name is required.");
		if (lastName == null || lastName.isBlank()) errors.add("Last name is required.");
		if (password == null || password.isBlank()) errors.add("Password is required.");
		if (password2 == null || password2.isBlank()) errors.add("Password confirmation is required.");

		if (!errors.isEmpty()) throw new ListException(errors);

		// Validate field data
		if (!password.equals(password2)) errors.add("Passwords do not match.");
		if (repository.findByEmail(email) != null) errors.add("Email is already registered.");

		if (!errors.isEmpty()) throw new ListException(errors);

		User user = new User();
		user.setEmail(email);
		user.setName(firstName + " " + lastName);
		user.setPassword(encryptor.hash(password));

		// Save user
		UserDTO dto = mapper.toDTO(repository.save(user));
		repository.commit();

		return dto;
	}

	public UserDTO login(String email, String password) throws ListException, GeneralSecurityException {
		User user = repository.findByEmail(email);

		if (user == null || !encryptor.verify(password, user.getPassword())) {
			throw new ListException(List.of("Invalid email or password."));
		}

		return mapper.toDTO(user);
	}
}
