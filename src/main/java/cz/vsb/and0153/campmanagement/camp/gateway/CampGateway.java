package cz.vsb.and0153.campmanagement.camp.gateway;

import cz.vsb.and0153.campmanagement.camp.record.CampRecord;
import cz.vsb.and0153.campmanagement.server.gateway.Gateway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

@Gateway
public record CampGateway(JdbcTemplate jdbcTemplate) implements RowMapper<CampRecord> {

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

	public List<CampRecord> findAll() {
		return jdbcTemplate.query(FIND_ALL, this);
	}

	public CampRecord findById(int id) {
		return jdbcTemplate.queryForStream(FIND_BY_ID, this, id).findFirst().orElse(null);
	}

	public int save(CampRecord campRecord) {
		if (campRecord.getId() != null) {
			return jdbcTemplate.update(UPDATE,
				campRecord.getName(), campRecord.getDescription(), campRecord.getImgUrl(), campRecord.getStartDate(),
				campRecord.getEndDate(), campRecord.getPrice(), campRecord.getCapacity(), campRecord.getRegistered(),
				campRecord.getId()
			);
		}

		return jdbcTemplate.update(INSERT,
			campRecord.getName(), campRecord.getDescription(), campRecord.getImgUrl(), campRecord.getStartDate(),
			campRecord.getEndDate(), campRecord.getPrice(), campRecord.getCapacity(), campRecord.getRegistered()
		);
	}

	public int delete(long id) {
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
