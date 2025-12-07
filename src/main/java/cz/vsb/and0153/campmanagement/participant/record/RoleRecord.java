package cz.vsb.and0153.campmanagement.participant.record;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleRecord {
	private Integer id;
	private String name;
	private Boolean canEditCamp;
	private Boolean canCreateTask;
	private Boolean canEditTask;
	private Boolean canViewDrugs;
	private Boolean canReceiveInfo;
}
