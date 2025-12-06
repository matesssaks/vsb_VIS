package cz.vsb.and0153.campmanagement.server.gateway;

import java.util.List;

public interface GatewayInterface<T> {
	List<T> findAll();
	T findById(int id);
	int save(T rec);
	int delete(int id);
}
