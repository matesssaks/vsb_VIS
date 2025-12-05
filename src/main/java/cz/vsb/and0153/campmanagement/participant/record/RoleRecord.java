package cz.vsb.and0153.campmanagement.participant.record;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleRecord {
	Integer id;
	String name;
	Boolean canEditCamp;
	Boolean canCreateTask;
	Boolean canEditTask;
	Boolean canViewDrugs;
	Boolean canReceiveInfo;
}
