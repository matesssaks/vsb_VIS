package cz.vsb.and0153.campmanagement.server.identitymap;

import cz.vsb.and0153.campmanagement.server.entity.Identifiable;
import cz.vsb.and0153.campmanagement.server.mapper.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class IdentityMap {

	private final Map<Class<?>, Map<Integer, Identifiable>> map = new HashMap<>();

	public <T extends Identifiable> T get(Class<T> clazz, int id) {
		return clazz.cast(map.getOrDefault(clazz, new HashMap<>()).get(id));
	}

	public <T extends Identifiable> T merge(T entity, EntityMapper<?, T> mapper) {
		if (entity == null || entity.getId() == null) {
			return entity; // Entity can't be merged without ID and is still in detached state
		}

		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) entity.getClass();

		map.putIfAbsent(clazz, new HashMap<>());
		T global = clazz.cast(map.get(clazz).get(entity.getId()));
		if (global == null) {
			map.get(entity.getClass()).put(entity.getId(), entity);
			return entity;
		} else {
			return mapper.merge(global, entity);
		}
	}

	public <T extends Identifiable> void delete(Class<T> clazz, int id) {
		map.getOrDefault(clazz, new HashMap<>()).remove(id);
	}
}
