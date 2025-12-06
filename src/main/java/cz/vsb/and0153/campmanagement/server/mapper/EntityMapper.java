package cz.vsb.and0153.campmanagement.server.mapper;

public interface EntityMapper<R, E> {
	E merge(E into, E from);

	R toRecord(E entity);
	E toEntity(R rec);
}
