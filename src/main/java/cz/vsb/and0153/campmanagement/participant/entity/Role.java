package cz.vsb.and0153.campmanagement.participant.entity;

import cz.vsb.and0153.campmanagement.server.entity.Identifiable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Role implements Identifiable {
	private Integer id;
	private String name;
	private Boolean canEditCamp;
	private Boolean canCreateTask;
	private Boolean canEditTask;
	private Boolean canViewDrugs;
	private Boolean canReceiveInfo;
}
