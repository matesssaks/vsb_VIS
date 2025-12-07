package cz.vsb.and0153.campmanagement.task.record;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class TaskRecord {
	private Integer id;
	private String shortDescription;
	private String description;
	private OffsetDateTime createdAt;
	private OffsetDateTime finishAt;
	private OffsetDateTime startDate;
	private OffsetDateTime dueDate;
}
