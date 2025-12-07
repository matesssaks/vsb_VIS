package cz.vsb.and0153.campmanagement.task.gateway;

import cz.vsb.and0153.campmanagement.server.gateway.Gateway;
import cz.vsb.and0153.campmanagement.server.gateway.GatewayInterface;
import cz.vsb.and0153.campmanagement.task.record.ParticipantTaskRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Gateway
public record ParticipantTaskGateway(JdbcTemplate jdbcTemplate) implements RowMapper<ParticipantTaskRecord>, GatewayInterface<ParticipantTaskRecord> {

	private static final String FIND_ALL = """
		SELECT participant_id, task_id FROM participant_task
		""";
	private static final String FIND_ALL_BY_TASK_ID = FIND_ALL + " WHERE task_id = ?";

	private static final String INSERT = """
		INSERT INTO participant_task (participant_id, task_id)
		VALUES (?, ?)
		""";

	private static final String DELETE = "DELETE FROM participant_task WHERE task_id = ?";
	private static final String DELETE_BY_TASK_PARTICIPANT = "DELETE FROM participant_task WHERE task_id = ? AND participant_id = ?";

	@Override
	public List<ParticipantTaskRecord> findAll() {
		return jdbcTemplate.query(FIND_ALL, this);
	}

	public List<ParticipantTaskRecord> findAllByTaskId(int taskId) {
		return jdbcTemplate.query(FIND_ALL_BY_TASK_ID, this, taskId);
	}

	@Override
	public ParticipantTaskRecord findById(int id) {
		// Stub implementation
		return jdbcTemplate.query(FIND_ALL_BY_TASK_ID, this, id).stream().findFirst().orElse(null);
	}

	@Override
	public int save(ParticipantTaskRecord rec) {
		return jdbcTemplate.update(INSERT,
			rec.getParticipantId(), rec.getTaskId()
		);
	}

	@Override
	public int delete(int id) {
		return jdbcTemplate.update(DELETE, id);
	}

	public int deleteByTaskIdAndParticipantId(int taskId, int participantId) {
		return jdbcTemplate.update(DELETE_BY_TASK_PARTICIPANT, taskId, participantId);
	}

	@Override
	public ParticipantTaskRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
		ParticipantTaskRecord rec = new ParticipantTaskRecord();
		rec.setTaskId(rs.getInt("task_id"));
		rec.setParticipantId(rs.getInt("participant_id"));
		return rec;
	}
}
