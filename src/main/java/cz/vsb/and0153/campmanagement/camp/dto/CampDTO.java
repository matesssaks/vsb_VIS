package cz.vsb.and0153.campmanagement.camp.dto;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class CampDTO {
	private Integer id;
	private String name;
	private String description;
	private String imgUrl;
	private OffsetDateTime startDate;
	private OffsetDateTime endDate;
	private Integer price;
	private Integer capacity;
}
