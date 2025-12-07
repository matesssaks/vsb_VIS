package cz.vsb.and0153.campmanagement.user.entity;

import cz.vsb.and0153.campmanagement.server.entity.Identifiable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User implements Identifiable {
	private Integer id;
	private String name;
	private String email;
	private String password;
}
