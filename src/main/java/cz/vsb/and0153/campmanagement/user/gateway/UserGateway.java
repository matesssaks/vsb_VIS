package cz.vsb.and0153.campmanagement.user.gateway;

import cz.vsb.and0153.campmanagement.server.gateway.Gateway;
import cz.vsb.and0153.campmanagement.server.gateway.GatewayInterface;
import cz.vsb.and0153.campmanagement.user.record.UserRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Gateway
public record UserGateway(JdbcTemplate jdbcTemplate) implements RowMapper<UserRecord>, GatewayInterface<UserRecord> {

	private static final String FIND_ALL = """
		SELECT user_id, name, email, password FROM users
		""";
	private static final String FIND_BY_ID = FIND_ALL + " WHERE user_id = ?";
	private static final String FIND_BY_EMAIL = FIND_ALL + " WHERE email = ?";

	private static final String INSERT = """
		INSERT INTO users (name, email, password)
		VALUES (?, ?, ?)
		""";
	private static final String UPDATE = """
		UPDATE users SET name = ?, email = ?, password = ?
		WHERE user_id = ?
		""";

	private static final String DELETE = "DELETE FROM users WHERE user_id = ?";

	@Override
	public List<UserRecord> findAll() {
		return jdbcTemplate.query(FIND_ALL, this);
	}

	@Override
	public UserRecord findById(int id) {
		return jdbcTemplate.queryForStream(FIND_BY_ID, this, id).findFirst().orElse(null);
	}

	public UserRecord findByEmail(String email) {
		return jdbcTemplate.queryForStream(FIND_BY_EMAIL, this, email).findFirst().orElse(null);
	}

	@Override
	public int save(UserRecord rec) {
		// If the record already has an ID, update it
		if (rec.getId() != null) {
			return jdbcTemplate.update(UPDATE,
				rec.getName(), rec.getEmail(), rec.getPassword(),
				rec.getId()
			);
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();

		// Update the record and get the generated ID
		int rows = jdbcTemplate.update(conn -> {
			PreparedStatement ps = conn.prepareStatement(INSERT, new String[] { "user_id" });
			ps.setString(1, rec.getName());
			ps.setString(2, rec.getEmail());
			ps.setString(3, rec.getPassword());
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
	public UserRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserRecord userRecord = new UserRecord();
		userRecord.setId(rs.getInt("user_id"));
		userRecord.setName(rs.getString("name"));
		userRecord.setEmail(rs.getString("email"));
		userRecord.setPassword(rs.getString("password"));
		return userRecord;
	}
}
