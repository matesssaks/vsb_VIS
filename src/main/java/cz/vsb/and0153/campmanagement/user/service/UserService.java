package cz.vsb.and0153.campmanagement.user.service;

import cz.vsb.and0153.campmanagement.user.dto.UserDTO;
import cz.vsb.and0153.campmanagement.user.mapper.UserMapper;
import cz.vsb.and0153.campmanagement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository repository;
	private final UserMapper mapper;

	public List<UserDTO> findAll() {
		return repository.findAll().stream().map(mapper::toDTO).toList();
	}

	public UserDTO findById(int id) {
		return mapper.toDTO(repository.findById(id));
	}

	public UserDTO save(UserDTO user) {
		return mapper.toDTO(repository.save(mapper.toEntity(user)));
	}

	public boolean delete(long id) {
		return repository.delete(id);
	}
}
