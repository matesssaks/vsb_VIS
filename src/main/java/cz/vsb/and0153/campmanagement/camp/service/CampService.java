package cz.vsb.and0153.campmanagement.camp.service;

import cz.vsb.and0153.campmanagement.camp.dto.CampDTO;
import cz.vsb.and0153.campmanagement.camp.mapper.CampMapper;
import cz.vsb.and0153.campmanagement.camp.repository.CampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampService {

	private final CampRepository repository;
	private final CampMapper mapper;

	public List<CampDTO> findAll() {
		return repository.findAll().stream().map(mapper::toDTO).toList();
	}

	public CampDTO findById(int id) {
		return mapper.toDTO(repository.findById(id));
	}

	public CampDTO save(CampDTO camp) {
		return mapper.toDTO(repository.save(mapper.toEntity(camp)));
	}

	public boolean delete(long id) {
		return repository.delete(id);
	}
}
