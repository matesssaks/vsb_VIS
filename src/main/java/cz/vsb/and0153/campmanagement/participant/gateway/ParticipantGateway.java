package cz.vsb.and0153.campmanagement.participant.gateway;

import cz.vsb.and0153.campmanagement.participant.record.ParticipantRecord;
import cz.vsb.and0153.campmanagement.server.gateway.Gateway;
import cz.vsb.and0153.campmanagement.server.gateway.GatewayInterface;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Gateway
public record ParticipantGateway(JdbcTemplate jdbcTemplate) implements RowMapper<ParticipantRecord>, GatewayInterface<ParticipantRecord> {

	private static final String FIND_ALL = """
		SELECT participant_id, camp_id, person_id, role_id FROM participant
		""";
	private static final String FIND_ALL_BY_USER_ID = """
		SELECT participant.participant_id as participant_id, participant.camp_id as camp_id,
		participant.person_id as person_id, participant.role_id as role_id FROM participant
		JOIN person ON person.person_id = participant.person_id WHERE person.user_id = ?
		""";
	private static final String FIND_ALL_BY_PERSON_ID = FIND_ALL + " WHERE person_id = ?";
	private static final String FIND_ALL_BY_CAMP_ID = FIND_ALL + " WHERE camp_id = ?";
	private static final String FIND_BY_ID = FIND_ALL + " WHERE participant_id = ?";

	private static final String INSERT = """
		INSERT INTO participant (camp_id, person_id, role_id)
		VALUES (?, ?, ?)
		""";
	private static final String UPDATE = """
		UPDATE participant SET camp_id = ?, person_id = ?, role_id = ?
		WHERE participant_id = ?
		""";

	private static final String DELETE = "DELETE FROM participant WHERE participant_id = ?";

	@Override
	public List<ParticipantRecord> findAll() {
		return jdbcTemplate.query(FIND_ALL, this);
	}

	public List<ParticipantRecord> findAllByUserId(int userId) {
		return jdbcTemplate.query(FIND_ALL_BY_USER_ID, this, userId);
	}

	public List<ParticipantRecord> findAllByCampId(int campId) {
		return jdbcTemplate.query(FIND_ALL_BY_CAMP_ID, this, campId);
	}

	public List<ParticipantRecord> findAllByPersonId(int userId) {
		return jdbcTemplate.query(FIND_ALL_BY_PERSON_ID, this, userId);
	}

	@Override
	public ParticipantRecord findById(int id) {
		return jdbcTemplate.query(FIND_BY_ID, this, id).stream().findFirst().orElse(null);
	}

	@Override
	public int save(ParticipantRecord rec) {
		if (rec.getId() != null) {
			return jdbcTemplate.update(UPDATE,
				rec.getCampId(), rec.getPersonId(), rec.getRoleId(),
				rec.getId()
			);
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();

		// Update the record and get the generated ID
		int rows = jdbcTemplate.update(conn -> {
			PreparedStatement ps = conn.prepareStatement(INSERT, new String[] { "participant_id" });
			ps.setInt(1, rec.getCampId());
			ps.setInt(2, rec.getPersonId());
			ps.setInt(3, rec.getRoleId());
			return ps;
		}, keyHolder);

		// Store the generated ID in the record
		Number key = keyHolder.getKey();
		if (key != null) {
			rec.setId(key.intValue());
		}

		return rows;
	}

	@Override
	public int delete(int id) {
		return jdbcTemplate.update(DELETE, id);
	}

	@Override
	public ParticipantRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
		ParticipantRecord rec = new ParticipantRecord();
		rec.setId(rs.getInt("participant_id"));
		rec.setCampId(rs.getInt("camp_id"));
		rec.setPersonId(rs.getInt("person_id"));
		rec.setRoleId(rs.getInt("role_id"));
		return rec;
	}
}
