package cz.vsb.and0153.campmanagement.person.dto;

import cz.vsb.and0153.campmanagement.server.entity.Identifiable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DrugDTO implements Identifiable {
	private Integer id;
	private String name;
	private Integer dosage;
	private String description;
}
