package cz.vsb.and0153.campmanagement.task.record;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class TaskRecord {
	Integer id;
	String shortDescription;
	String description;
	OffsetDateTime createdAt;
	OffsetDateTime finishAt;
	OffsetDateTime startDate;
	OffsetDateTime dueDate;
}
