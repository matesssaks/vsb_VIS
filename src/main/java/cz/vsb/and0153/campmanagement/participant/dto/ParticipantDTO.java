package cz.vsb.and0153.campmanagement.participant.dto;

import cz.vsb.and0153.campmanagement.camp.dto.CampDTO;
import cz.vsb.and0153.campmanagement.person.dto.PersonDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParticipantDTO {
	private Integer id;
	private CampDTO camp;
	private PersonDTO person;
	private RoleDTO role;
}
