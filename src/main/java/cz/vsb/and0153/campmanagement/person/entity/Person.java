package cz.vsb.and0153.campmanagement.person.entity;

import cz.vsb.and0153.campmanagement.server.entity.Identifiable;
import cz.vsb.and0153.campmanagement.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class Person implements Identifiable {
	private Integer id;
	private User user;
	private String personalNumber;
	private String name;
	private String town;
	private String street;
	private String postalCode;
	private OffsetDateTime bornDate;
	private List<Drug> drugs;
}
