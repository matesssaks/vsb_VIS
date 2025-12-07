package cz.vsb.and0153.campmanagement.person.service;

import cz.vsb.and0153.campmanagement.person.dto.PersonDTO;
import cz.vsb.and0153.campmanagement.person.mapper.PersonMapper;
import cz.vsb.and0153.campmanagement.person.repository.PersonRepository;
import cz.vsb.and0153.campmanagement.server.exception.ListException;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;
import cz.vsb.and0153.campmanagement.user.dto.UserDTO;
import cz.vsb.and0153.campmanagement.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

	private final PersonRepository repository;
	private final UserMapper userMapper;
	private final PersonMapper personMapper;

	public List<PersonDTO> findAllByUser(UserDTO user) {
		return repository.findAllByUser(userMapper.toEntity(user)).stream().map(personMapper::toDTO).toList();
	}

	public PersonDTO findById(int id) {
		return personMapper.toDTO(repository.findById(id));
	}

	public PersonDTO save(PersonDTO person, UserDTO user) throws ListException, UnitOfWorkException {
		List<String> errors = new ArrayList<>();

		// Validate if all fields are filled
		if (person.getName() == null || person.getName().isBlank()) errors.add("Name is required.");
		if (person.getTown() == null || person.getTown().isBlank()) errors.add("Town is required.");
		if (person.getStreet() == null || person.getStreet().isBlank()) errors.add("Street is required.");
		if (person.getPostalCode() == null) errors.add("Postal code is required.");
		if (person.getBornDate() == null) errors.add("Born data is required.");

		if (!errors.isEmpty()) throw new ListException(errors);

		// Force user association
		person.setUser(user);

		// Save person
		PersonDTO dto = personMapper.toDTO(repository.save(personMapper.toEntity(person)));
		repository.commit();

		return dto;
	}

	public void delete(int id) throws UnitOfWorkException {
		repository.delete(id);
		repository.commit();
	}
}
