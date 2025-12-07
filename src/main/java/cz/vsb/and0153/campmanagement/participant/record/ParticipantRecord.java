package cz.vsb.and0153.campmanagement.participant.record;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParticipantRecord {
	private Integer id;
	private Integer campId;
	private Integer personId;
	private Integer roleId;
}
