package cz.vsb.and0153.campmanagement.person.gateway;

import cz.vsb.and0153.campmanagement.person.record.PersonRecord;
import cz.vsb.and0153.campmanagement.server.gateway.Gateway;
import cz.vsb.and0153.campmanagement.server.gateway.GatewayInterface;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

@Gateway
public record PersonGateway(JdbcTemplate jdbcTemplate) implements RowMapper<PersonRecord>, GatewayInterface<PersonRecord> {

	private static final String FIND_ALL = """
		SELECT person_id, user_id, name, town, street, postal_code, born_date FROM person
		""";
	private static final String FIND_ALL_BY_USER_ID = FIND_ALL + " WHERE user_id = ?";
	private static final String FIND_BY_ID = FIND_ALL + " WHERE person_id = ?";

	private static final String INSERT = """
		INSERT INTO person (user_id, name, town, street, postal_code, born_date)
		VALUES (?, ?, ?, ?, ?, ?)
		""";
	private static final String UPDATE = """
		UPDATE person SET user_id = ?, name = ?, town = ?, street = ?, postal_code = ?, born_date = ?
		WHERE person_id = ?
		""";

	private static final String DELETE = "DELETE FROM person WHERE person_id = ?";

	@Override
	public List<PersonRecord> findAll() {
		return jdbcTemplate.query(FIND_ALL, this);
	}

	public List<PersonRecord> findAllByUserId(int userId) {
		return jdbcTemplate.query(FIND_ALL_BY_USER_ID, this, userId);
	}

	@Override
	public PersonRecord findById(int id) {
		return jdbcTemplate.queryForStream(FIND_BY_ID, this, id).findFirst().orElse(null);
	}

	@Override
	public int save(PersonRecord rec) {
		if (rec.getId() != null) {
			return jdbcTemplate.update(UPDATE,
				rec.getUserId(), rec.getName(), rec.getTown(), rec.getStreet(), rec.getPostalCode(), rec.getBornDate(),
				rec.getId()
			);
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();

		// Update the record and get the generated ID
		int rows = jdbcTemplate.update(conn -> {
			PreparedStatement ps = conn.prepareStatement(INSERT, new String[] { "person_id" });
			ps.setInt(1, rec.getUserId());
			ps.setString(2, rec.getName());
			ps.setString(3, rec.getTown());
			ps.setString(4, rec.getStreet());
			ps.setString(5, rec.getPostalCode());
			ps.setObject(6, rec.getBornDate(), java.sql.Types.TIMESTAMP_WITH_TIMEZONE);
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
	public PersonRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
		PersonRecord rec = new PersonRecord();
		rec.setId(rs.getInt("person_id"));
		rec.setUserId(rs.getInt("user_id"));
		rec.setName(rs.getString("name"));
		rec.setTown(rs.getString("town"));
		rec.setStreet(rs.getString("street"));
		rec.setPostalCode(rs.getString("postal_code"));
		rec.setBornDate(rs.getObject("born_date", OffsetDateTime.class));
		return rec;
	}
}
