package cz.vsb.and0153.campmanagement.participant.record;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParticipantRecord {
	Integer id;
	Integer campId;
	Integer personId;
	Integer roleId;
}
