package cz.vsb.and0153.campmanagement.participant.entity;

import cz.vsb.and0153.campmanagement.camp.entity.Camp;
import cz.vsb.and0153.campmanagement.person.entity.Person;
import cz.vsb.and0153.campmanagement.server.entity.Identifiable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Participant implements Identifiable {
	private Integer id;
	private Camp camp;
	private Person person;
	private Role role;
}
