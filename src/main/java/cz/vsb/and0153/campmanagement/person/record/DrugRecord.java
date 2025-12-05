package cz.vsb.and0153.campmanagement.person.record;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DrugRecord {
	Integer id;
	Integer personId;
	String name;
	Integer dosage;
	String description;
}
