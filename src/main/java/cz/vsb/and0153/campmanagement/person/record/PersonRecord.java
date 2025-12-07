package cz.vsb.and0153.campmanagement.person.record;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class PersonRecord {
	private Integer id;
	private Integer userId;
	private String personalNumber;
	private String name;
	private String town;
	private String street;
	private String postalCode;
	private OffsetDateTime bornDate;
}
