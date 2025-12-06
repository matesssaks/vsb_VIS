package cz.vsb.and0153.campmanagement.camp.gateway;

import cz.vsb.and0153.campmanagement.camp.record.CampRecord;
import cz.vsb.and0153.campmanagement.server.gateway.Gateway;
import cz.vsb.and0153.campmanagement.server.gateway.GatewayInterface;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.List;

@Gateway
public record CampGateway(JdbcTemplate jdbcTemplate) implements RowMapper<CampRecord>, GatewayInterface<CampRecord> {

	private static final String FIND_ALL = """
		SELECT camp_id, name, description, img_url, start_date, end_date, price, capacity, registered FROM camp
		""";
	private static final String FIND_BY_ID = FIND_ALL + " WHERE camp_id = ?";

	private static final String INSERT = """
		INSERT INTO camp (name, description, img_url, start_date, end_date, price, capacity, registered)
		VALUES (?, ?, ?, ?, ?, ?, ?, ?)
		""";
	private static final String UPDATE = """
		UPDATE camp SET name = ?, description = ?, img_url = ?, start_date = ?, end_date = ?, price = ?, capacity = ?, registered = ?
		WHERE camp_id = ?
		""";

	private static final String DELETE = "DELETE FROM camp WHERE camp_id = ?";

	@Override
	public List<CampRecord> findAll() {
		return jdbcTemplate.query(FIND_ALL, this);
	}

	@Override
	public CampRecord findById(int id) {
		return jdbcTemplate.queryForStream(FIND_BY_ID, this, id).findFirst().orElse(null);
	}

	@Override
	public int save(CampRecord campRecord) {
		if (campRecord.getId() != null) {
			return jdbcTemplate.update(UPDATE,
				campRecord.getName(), campRecord.getDescription(), campRecord.getImgUrl(), campRecord.getStartDate(),
				campRecord.getEndDate(), campRecord.getPrice(), campRecord.getCapacity(), campRecord.getRegistered(),
				campRecord.getId()
			);
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();

		// Update the record and get the generated ID
		int rows = jdbcTemplate.update(conn -> {
			PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, campRecord.getName());
			ps.setString(2, campRecord.getDescription());
			ps.setString(3, campRecord.getImgUrl());
			ps.setObject(4, campRecord.getStartDate(), Types.TIMESTAMP_WITH_TIMEZONE);
			ps.setObject(5, campRecord.getEndDate(), Types.TIMESTAMP_WITH_TIMEZONE);
			ps.setInt(6, campRecord.getPrice());
			ps.setInt(7, campRecord.getCapacity());
			ps.setInt(8, campRecord.getRegistered());
			return ps;
		}, keyHolder);

		// Store the generated ID in the record
		Number key = keyHolder.getKey();
		if (key != null) {
			campRecord.setId(key.intValue());
		}

		return rows;
	}

	@Override
	public int delete(int id) {
		return jdbcTemplate.update(DELETE, id);
	}

	@Override
	public CampRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
		CampRecord campRecord = new CampRecord();
		campRecord.setId(rs.getInt("camp_id"));
		campRecord.setName(rs.getString("name"));
		campRecord.setDescription(rs.getString("description"));
		campRecord.setImgUrl(rs.getString("img_url"));
		campRecord.setStartDate(rs.getObject("start_date", OffsetDateTime.class));
		campRecord.setStartDate(rs.getObject("end_date", OffsetDateTime.class));
		campRecord.setPrice(rs.getInt("price"));
		campRecord.setCapacity(rs.getInt("capacity"));
		campRecord.setRegistered(rs.getInt("registered"));
		return campRecord;
	}
}
