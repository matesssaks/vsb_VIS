package cz.vsb.and0153.campmanagement.camp.repository;

import cz.vsb.and0153.campmanagement.camp.entity.Camp;
import cz.vsb.and0153.campmanagement.camp.gateway.CampGateway;
import cz.vsb.and0153.campmanagement.camp.mapper.CampMapper;
import cz.vsb.and0153.campmanagement.server.repository.RepositoryInterface;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWork;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CampRepository implements RepositoryInterface<Camp> {

	private final CampGateway gateway;
	private final CampMapper mapper;
	private final UnitOfWork unitOfWork;

	@Override
	public List<Camp> findAll() {
		return unitOfWork.findAll(mapper, gateway);
	}

	@Override
	public Camp findById(int id) {
		return unitOfWork.findById(Camp.class, id, mapper, gateway);
	}

	@Override
	public Camp save(Camp entity) {
		return unitOfWork.save(entity, mapper, gateway);
	}

	@Override
	public void delete(int id) {
		unitOfWork.delete(Camp.class, id, gateway);
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
