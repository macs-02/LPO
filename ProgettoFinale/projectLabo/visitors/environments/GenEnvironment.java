package projectLabo.visitors.environments;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class GenEnvironment<T> implements Environment<T> {
	private LinkedList<Map<String, T>> scopes = new LinkedList<>();

	public GenEnvironment() {
		enterScope();
	}

	@Override
	public void enterScope() {
		scopes.addFirst(new HashMap<>());
	}

	@Override
	public void exitScope() {
		scopes.removeFirst();
	}

	@Override
	public T lookup(String name) {
		for (Map<String, T> scope : scopes) {
			if (scope.containsKey(name)) {
				return scope.get(name);
			}
		}
		throw new RuntimeException("Undeclared variable: " + name);
	}

	@Override
	public T dec(String name, T payload) {
		Map<String, T> currentScope = scopes.getFirst();
		if (currentScope.containsKey(name)) {
			throw new RuntimeException("Variable already declared: " + name);
		}
		currentScope.put(name, payload);
		return payload;
	}

	@Override
	public T update(String name, T payload) {
		for (Map<String, T> scope : scopes) {
			if (scope.containsKey(name)) {
				scope.put(name, payload);
				return payload;
			}
		}
		throw new RuntimeException("Undeclared variable: " + name);
	}
}
