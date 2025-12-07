package cz.vsb.and0153.campmanagement.person.dto;

import cz.vsb.and0153.campmanagement.user.dto.UserDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class PersonDTO {
	private Integer id;
	private UserDTO user;
	private String name;
	private String town;
	private String street;
	private String postalCode;
	private OffsetDateTime bornDate;
	private List<DrugDTO> drugs;
}
