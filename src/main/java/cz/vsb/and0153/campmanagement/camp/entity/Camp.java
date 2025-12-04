package cz.vsb.and0153.campmanagement.camp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class Camp {
	private Integer id;
	private String name;
	private String description;
	private String imgUrl;
	private OffsetDateTime startDate;
	private OffsetDateTime endDate;
	private Integer price;
	private Integer capacity;
	private Integer registered;
}
