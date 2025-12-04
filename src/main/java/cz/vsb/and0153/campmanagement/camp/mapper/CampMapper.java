package cz.vsb.and0153.campmanagement.camp.mapper;

import cz.vsb.and0153.campmanagement.camp.dto.CampDTO;
import cz.vsb.and0153.campmanagement.camp.entity.Camp;
import cz.vsb.and0153.campmanagement.camp.record.CampRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CampMapper {
	CampRecord toRecord(Camp camp);
	Camp toEntity(CampRecord campRecord);

	CampDTO toDTO(Camp camp);
	Camp toEntity(CampDTO campDTO);
}
