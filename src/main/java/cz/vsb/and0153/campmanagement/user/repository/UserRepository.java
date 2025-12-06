package cz.vsb.and0153.campmanagement.user.repository;

import cz.vsb.and0153.campmanagement.user.entity.User;
import cz.vsb.and0153.campmanagement.user.gateway.UserGateway;
import cz.vsb.and0153.campmanagement.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {

	private final UserGateway gateway;
	private final UserMapper mapper;

	public List<User> findAll() {
		return gateway.findAll().stream().map(mapper::toEntity).toList();
	}

	public User findById(int id) {
		return mapper.toEntity(gateway.findById(id));
	}

	public User findByEmail(String email) {
		return mapper.toEntity(gateway.findByEmail(email));
	}

	public User save(User user) {
		gateway.save(mapper.toRecord(user));
		return user;
	}

	public boolean delete(long id) {
		return gateway.delete(id) > 0;
	}
}
