package cz.vsb.and0153.campmanagement.server.repository;

import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;

import java.util.List;

public interface RepositoryInterface<T> {
	List<T> findAll();
	T findById(int id);
	T save(T entity);
	void delete(int id);
	void commit() throws UnitOfWorkException;
	void rollback();
}
