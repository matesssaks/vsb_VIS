package cz.vsb.and0153.campmanagement.user.record;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRecord {
	private Integer id;
	private String name;
	private String email;
	private String password;
}
