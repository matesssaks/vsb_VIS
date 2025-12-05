package cz.vsb.and0153.campmanagement.camp.repository;

import cz.vsb.and0153.campmanagement.camp.entity.Camp;
import cz.vsb.and0153.campmanagement.camp.gateway.CampGateway;
import cz.vsb.and0153.campmanagement.camp.mapper.CampMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CampRepository {

	private final CampGateway gateway;
	private final CampMapper mapper;

	public List<Camp> findAll() {
		return gateway.findAll().stream().map(mapper::toEntity).toList();
	}

	public Camp findById(int id) {
		return mapper.toEntity(gateway.findById(id));
	}

	public Camp save(Camp camp) {
		gateway.save(mapper.toRecord(camp));
		return camp;
	}

	public boolean delete(long id) {
		return gateway.delete(id) > 0;
	}
}
