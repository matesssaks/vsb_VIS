package cz.vsb.and0153.campmanagement.server.uow;

import cz.vsb.and0153.campmanagement.server.entity.Identifiable;
import cz.vsb.and0153.campmanagement.server.gateway.GatewayInterface;
import cz.vsb.and0153.campmanagement.server.identitymap.IdentityMap;
import cz.vsb.and0153.campmanagement.server.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UnitOfWork {

	private final IdentityMap identityMap;
	private final List<UnitOfWorkAction> actions = new LinkedList<>();

	public void register(UnitOfWorkAction action) {
		actions.add(action);
	}

	public <E extends Identifiable, R> List<E> findAll(
		EntityMapper<R, E> mapper,
		GatewayInterface<R> gateway
	) {
		return gateway.findAll().stream().map(rec -> identityMap.merge(mapper.toEntity(rec), mapper)).toList();
	}

	public <E extends Identifiable, R> E findById(
		Class<E> clazz,
		int id,
		EntityMapper<R, E> mapper,
		GatewayInterface<R> gateway
	) {
		E identityEntity = identityMap.get(clazz, id);
		if (identityEntity == null) {
			return identityMap.merge(mapper.toEntity(gateway.findById(id)), mapper);
		}

		return identityEntity;
	}

	public <E extends Identifiable, R> E save(
		E entity,
		EntityMapper<R, E> mapper,
		GatewayInterface<R> gateway
	) {
		// Merge entity with the identity map for immediate changes
		E identityEntity = identityMap.merge(entity, mapper);

		register(() -> {
			R rec = mapper.toRecord(identityEntity);
			if (gateway.save(rec) < 1) throw new UnitOfWorkException("Failed to save entity");
			identityMap.merge(mapper.toEntity(rec), mapper);
		});

		return identityEntity;
	}

	public <E extends Identifiable, R> void delete(
		Class<E> clazz,
		int id,
		GatewayInterface<R> gateway
	) {
		identityMap.delete(clazz, id);
		register(() -> {
			if (gateway.delete(id) < 1) throw new UnitOfWorkException("Failed to delete entity");
		});
	}

	public void commit() throws UnitOfWorkException {
		for (UnitOfWorkAction action : actions) {
			action.apply();
		}
	}

	public void rollback() {
		actions.clear();
	}
}
