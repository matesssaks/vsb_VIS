package cz.vsb.and0153.campmanagement.task.entity;

import cz.vsb.and0153.campmanagement.participant.entity.Participant;
import cz.vsb.and0153.campmanagement.server.entity.Identifiable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class Task implements Identifiable {
	private Integer id;
	private String shortDescription;
	private String description;
	private OffsetDateTime createdAt;
	private OffsetDateTime finishAt;
	private OffsetDateTime startDate;
	private OffsetDateTime dueDate;
	private List<Participant> participants;
}
