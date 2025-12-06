package cz.vsb.and0153.campmanagement.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
	Integer id;
	String name;
	String email;
	String password;
}
