package cz.vsb.and0153.campmanagement.task.dto;

import cz.vsb.and0153.campmanagement.participant.dto.ParticipantDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class TaskDTO {
	private Integer id;
	private String shortDescription;
	private String description;
	private OffsetDateTime createdAt;
	private OffsetDateTime finishAt;
	private OffsetDateTime startDate;
	private OffsetDateTime dueDate;
	private List<ParticipantDTO> participants;
}
