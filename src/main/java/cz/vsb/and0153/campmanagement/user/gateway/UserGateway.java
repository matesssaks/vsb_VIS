package cz.vsb.and0153.campmanagement.user.gateway;

import cz.vsb.and0153.campmanagement.server.gateway.Gateway;
import cz.vsb.and0153.campmanagement.user.record.UserRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Gateway
public record UserGateway(JdbcTemplate jdbcTemplate) implements RowMapper<UserRecord> {

	private static final String FIND_ALL = """
		SELECT user_id, name, email, password FROM users
		""";
	private static final String FIND_BY_ID = FIND_ALL + " WHERE user_id = ?";
	private static final String INSERT = """
		INSERT INTO users (name, email, password)
		VALUES (?, ?, ?)
		""";
	private static final String UPDATE = """
		UPDATE users SET name = ?, email = ?, password = ?
		WHERE user_id = ?
		""";
	private static final String DELETE = "DELETE FROM users WHERE user_id = ?";

	public List<UserRecord> findAll() {
		return jdbcTemplate.query(FIND_ALL, this);
	}

	public UserRecord findById(int id) {
		return jdbcTemplate.queryForStream(FIND_BY_ID, this, id).findFirst().orElse(null);
	}

	public int save(UserRecord userRecord) {
		if (userRecord.getId() != null) {
			return jdbcTemplate.update(UPDATE,
				userRecord.getName(), userRecord.getName(), userRecord.getEmail(), userRecord.getPassword(),
				userRecord.getId()
			);
		}

		return jdbcTemplate.update(INSERT,
			userRecord.getName(), userRecord.getName(), userRecord.getEmail(), userRecord.getPassword()
		);
	}

	public int delete(long id) {
		return jdbcTemplate.update(DELETE, id);
	}

	@Override
	public UserRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserRecord userRecord = new UserRecord();
		userRecord.setId(rs.getInt("user_id"));
		userRecord.setName(rs.getString("name"));
		userRecord.setEmail(rs.getString("email"));
		userRecord.setPassword(rs.getString("password"));
		return userRecord;
	}
}
