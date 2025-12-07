package cz.vsb.and0153.campmanagement.person.gateway;

import cz.vsb.and0153.campmanagement.person.record.DrugRecord;
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
public record DrugGateway(JdbcTemplate jdbcTemplate) implements RowMapper<DrugRecord>, GatewayInterface<DrugRecord> {

	private static final String FIND_ALL = """
		SELECT drug_id, person_id, name, dosage, description FROM drug
		""";
	private static final String FIND_ALL_BY_PERSON_ID = FIND_ALL + " WHERE person_id = ?";
	private static final String FIND_BY_ID = FIND_ALL + " WHERE drug_id = ?";
	private static final String FIND_PERSON_BY_ID = """
		SELECT person_id FROM drug WHERE drug_id = ?
		""";

	private static final String INSERT = """
		INSERT INTO drug (person_id, name, dosage, description)
		VALUES (?, ?, ?, ?)
		""";
	private static final String UPDATE = """
		UPDATE drug SET person_id = ?, name = ?, dosage = ?, description = ?
		WHERE drug_id = ?
		""";

	private static final String DELETE = "DELETE FROM drug WHERE drug_id = ?";

	@Override
	public List<DrugRecord> findAll() {
		return jdbcTemplate.query(FIND_ALL, this);
	}

	public List<DrugRecord> findAllByPersonId(int personId) {
		return jdbcTemplate.query(FIND_ALL_BY_PERSON_ID, this, personId);
	}

	@Override
	public DrugRecord findById(int id) {
		return jdbcTemplate.query(FIND_BY_ID, this, id).stream().findFirst().orElse(null);
	}

	public Integer findPersonById(int id) {
		return jdbcTemplate.queryForObject(FIND_PERSON_BY_ID, Integer.class, id);
	}

	@Override
	public int save(DrugRecord rec) {
		if (rec.getId() != null) {
			return jdbcTemplate.update(UPDATE,
				rec.getPersonId(), rec.getName(), rec.getDosage(), rec.getDescription(),
				rec.getId()
			);
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();

		// Update the record and get the generated ID
		int rows = jdbcTemplate.update(conn -> {
			PreparedStatement ps = conn.prepareStatement(INSERT, new String[] { "drug_id" });
			ps.setInt(1, rec.getPersonId());
			ps.setString(2, rec.getName());
			ps.setInt(3, rec.getDosage());
			ps.setString(4, rec.getDescription());
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
	public DrugRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
		DrugRecord rec = new DrugRecord();
		rec.setId(rs.getInt("drug_id"));
		rec.setPersonId(rs.getInt("person_id"));
		rec.setName(rs.getString("name"));
		rec.setDosage(rs.getInt("dosage"));
		rec.setDescription(rs.getString("description"));
		return rec;
	}
}
