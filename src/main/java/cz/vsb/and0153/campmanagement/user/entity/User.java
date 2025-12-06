package cz.vsb.and0153.campmanagement.user.entity;

import cz.vsb.and0153.campmanagement.server.entity.Identifiable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User implements Identifiable {
	Integer id;
	String name;
	String email;
	String password;
}
