package cz.vsb.and0153.campmanagement.person.record;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class PersonRecord {
	Integer id;
	Integer userId;
	String name;
	String email;
	String phone;
	String town;
	String street;
	Integer postalCode;
	String gender;
	OffsetDateTime birthDate;
}
