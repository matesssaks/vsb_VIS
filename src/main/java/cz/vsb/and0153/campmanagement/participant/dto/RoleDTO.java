package cz.vsb.and0153.campmanagement.participant.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleDTO {
	private Integer id;
	private String name;
	private Boolean canEditCamp;
	private Boolean canCreateTask;
	private Boolean canEditTask;
	private Boolean canViewDrugs;
	private Boolean canReceiveInfo;
}
