package cz.vsb.and0153.campmanagement.user.record;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRecord {
	Integer id;
	String name;
	String email;
	String password;
}
