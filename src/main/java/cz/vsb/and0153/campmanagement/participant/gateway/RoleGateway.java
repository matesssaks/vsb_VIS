package cz.vsb.and0153.campmanagement.participant.gateway;

import cz.vsb.and0153.campmanagement.participant.record.RoleRecord;
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
public record RoleGateway(JdbcTemplate jdbcTemplate) implements RowMapper<RoleRecord>, GatewayInterface<RoleRecord> {

	private static final String FIND_ALL = """
		SELECT role_id, name, can_edit_camp, can_create_task, can_edit_task, can_view_drugs, can_receive_info  FROM role
		""";
	private static final String FIND_BY_ID = FIND_ALL + " WHERE role_id = ?";

	private static final String INSERT = """
		INSERT INTO role (name, can_edit_camp, can_create_task, can_edit_task, can_view_drugs, can_receive_info)
		VALUES (?, ?, ?, ?, ?, ?)
		""";
	private static final String UPDATE = """
		UPDATE role SET name = ?, can_edit_camp = ?, can_create_task = ?, can_edit_task = ?, can_view_drugs = ?, can_receive_info= ?
		WHERE role_id = ?
		""";

	private static final String DELETE = "DELETE FROM role WHERE role_id = ?";

	@Override
	public List<RoleRecord> findAll() {
		return jdbcTemplate.query(FIND_ALL, this);
	}

	@Override
	public RoleRecord findById(int id) {
		return jdbcTemplate.query(FIND_BY_ID, this, id).stream().findFirst().orElse(null);
	}

	@Override
	public int save(RoleRecord rec) {
		if (rec.getId() != null) {
			return jdbcTemplate.update(UPDATE,
				rec.getName(), rec.getCanEditCamp(), rec.getCanCreateTask(), rec.getCanEditTask(),
				rec.getCanViewDrugs(), rec.getCanReceiveInfo(),
				rec.getId()
			);
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();

		// Update the record and get the generated ID
		int rows = jdbcTemplate.update(conn -> {
			PreparedStatement ps = conn.prepareStatement(INSERT, new String[] { "role_id" });
			ps.setString(1, rec.getName());
			ps.setBoolean(2, rec.getCanEditCamp());
			ps.setBoolean(3, rec.getCanCreateTask());
			ps.setBoolean(4, rec.getCanEditTask());
			ps.setBoolean(5, rec.getCanViewDrugs());
			ps.setBoolean(6, rec.getCanReceiveInfo());
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
	public RoleRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
		RoleRecord rec = new RoleRecord();
		rec.setId(rs.getInt("role_id"));
		rec.setName(rs.getString("name"));
		rec.setCanEditCamp(rs.getBoolean("can_edit_camp"));
		rec.setCanCreateTask(rs.getBoolean("can_create_task"));
		rec.setCanEditTask(rs.getBoolean("can_edit_task"));
		rec.setCanViewDrugs(rs.getBoolean("can_view_drugs"));
		rec.setCanReceiveInfo(rs.getBoolean("can_receive_info"));
		return rec;
	}
}
