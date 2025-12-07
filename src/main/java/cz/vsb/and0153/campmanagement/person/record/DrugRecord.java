package cz.vsb.and0153.campmanagement.person.record;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DrugRecord {
	private Integer id;
	private Integer personId;
	private String name;
	private Integer dosage;
	private String description;
}
