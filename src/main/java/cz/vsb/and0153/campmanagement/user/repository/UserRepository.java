package cz.vsb.and0153.campmanagement.user.repository;

import cz.vsb.and0153.campmanagement.server.identitymap.IdentityMap;
import cz.vsb.and0153.campmanagement.server.repository.RepositoryInterface;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWork;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;
import cz.vsb.and0153.campmanagement.user.entity.User;
import cz.vsb.and0153.campmanagement.user.gateway.UserGateway;
import cz.vsb.and0153.campmanagement.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository implements RepositoryInterface<User> {

	private final IdentityMap identityMap;
	private final UnitOfWork unitOfWork;
	private final UserGateway gateway;
	private final UserMapper mapper;

	public User findByEmail(String email) {
		return identityMap.merge(mapper.toEntity(gateway.findByEmail(email)), mapper);
	}

	@Override
	public List<User> findAll() {
		return unitOfWork.findAll(mapper, gateway);
	}

	@Override
	public User findById(int id) {
		return unitOfWork.findById(User.class, id, mapper, gateway);
	}

	@Override
	public User save(User user) {
		return unitOfWork.save(user, mapper, gateway);
	}

	@Override
	public void delete(int id) {
		unitOfWork.delete(User.class, id, gateway);
	}

	@Override
	public void commit() throws UnitOfWorkException {
		unitOfWork.commit();
	}

	@Override
	public void rollback() {
		unitOfWork.rollback();
	}
}
