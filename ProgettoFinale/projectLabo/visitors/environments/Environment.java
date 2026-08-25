package projectLabo.visitors.environments;

public interface Environment<T> {
	void enterScope();
	void exitScope();
	T lookup(String name);
	T dec(String name, T payload);
	T update(String name, T payload);
}
