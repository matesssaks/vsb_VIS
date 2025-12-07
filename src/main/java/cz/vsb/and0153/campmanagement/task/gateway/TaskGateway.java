package cz.vsb.and0153.campmanagement.task.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.vsb.and0153.campmanagement.server.gateway.Gateway;
import cz.vsb.and0153.campmanagement.server.gateway.GatewayInterface;
import cz.vsb.and0153.campmanagement.task.exception.XMLException;
import cz.vsb.and0153.campmanagement.task.record.TaskRecord;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Gateway
public class TaskGateway implements GatewayInterface<TaskRecord> {

	/**
	 * Wrapper class for the XML file.
	 */
	@JacksonXmlRootElement(localName = "tasks")
	private static class TaskListWrapper {

		@JacksonXmlElementWrapper(useWrapping = false)
		@JsonProperty("task")
		@Getter
		@Setter
		private List<TaskRecord> tasks = new ArrayList<>();
	}

	private final XmlMapper xmlMapper = new XmlMapper();
	private final Path path;

	public TaskGateway(@Value("${xml.path}") String path) {
		this.path = Path.of(path).resolve("tasks.xml");

		// Enable support for OffsetDateTime
		this.xmlMapper.registerModule(new JavaTimeModule());
		this.xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Override
	public List<TaskRecord> findAll() {
		TaskListWrapper wrapper = readWrapper();
		return wrapper.getTasks();
	}

	@Override
	public TaskRecord findById(int id) {
		TaskListWrapper wrapper = readWrapper();
		return wrapper.getTasks().stream()
			.filter(rec -> Objects.equals(rec.getId(), id))
			.findFirst().orElse(null);
	}

	@Override
	public int save(TaskRecord rec) {
		TaskListWrapper wrapper = readWrapper();
		List<TaskRecord> tasks = wrapper.getTasks();

		if (rec.getId() == null) {
			int nextId = getNextId(tasks);
			rec.setId(nextId);
			tasks.add(rec);
		} else {
			int existingIndex = findIndexById(tasks, rec.getId());
			if (existingIndex >= 0) {
				tasks.set(existingIndex, rec);
			} else {
				tasks.add(rec);
			}
		}

		writeWrapper(wrapper);

		return 1; // Save always succeeds
	}

	@Override
	public int delete(int id) {
		TaskListWrapper wrapper = readWrapper();

		boolean changed = wrapper.getTasks().removeIf(p -> Objects.equals(p.getId(), id));

		// If data was changed, write it back to the file
		if (changed) {
			writeWrapper(wrapper);
			return 1; // At least one row was deleted
		}

		return 0; // No rows affected
	}

	private TaskListWrapper readWrapper() {
		if (!Files.exists(path)) {
			return new TaskListWrapper();
		}
		try {
			if (Files.size(path) == 0) {
				return new TaskListWrapper();
			}

			// Read the XML file
			return xmlMapper.readValue(Files.newInputStream(path), TaskListWrapper.class);
		} catch (IOException e) {
			throw new XMLException("Failed to read XML file: " + path, e);
		}
	}

	private void writeWrapper(TaskListWrapper wrapper) {
		try {
			// Create parent directories if necessary
			Files.createDirectories(path.getParent());

			// Write the XML file
			xmlMapper.writerWithDefaultPrettyPrinter().writeValue(Files.newOutputStream(path), wrapper);
		} catch (IOException e) {
			throw new XMLException("Failed to write XML file: " + path, e);
		}
	}

	private int getNextId(List<TaskRecord> records) {
		return records.stream()
			.map(TaskRecord::getId)
			.filter(Objects::nonNull)
			.mapToInt(Integer::intValue)
			.max()
			.orElse(0) + 1;
	}

	private int findIndexById(List<TaskRecord> tasks, Integer id) {
		return IntStream.range(0, tasks.size())
			.filter(i -> Objects.equals(tasks.get(i).getId(), id))
			.findFirst().orElse(-1);
	}
}
