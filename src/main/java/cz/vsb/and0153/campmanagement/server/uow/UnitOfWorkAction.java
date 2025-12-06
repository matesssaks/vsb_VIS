package cz.vsb.and0153.campmanagement.server.uow;

@FunctionalInterface
public interface UnitOfWorkAction {
	void apply() throws UnitOfWorkException;
}
