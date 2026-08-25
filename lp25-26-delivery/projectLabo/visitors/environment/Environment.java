package projectLabo.visitors.environment;

import java.util.*;

public class Environment<T> {
	private final List<Map<String, T>> scopeLevels;

	public Environment() {
		this.scopeLevels = new ArrayList<>();
		this.scopeLevels.add(new HashMap<>()); // initial empty level
	}

	public void enterLevel() {
		scopeLevels.add(0, new HashMap<>());
	}

	public void exitLevel() {
		if (scopeLevels.size() <= 1) 
			throw new IllegalStateException("Cannot exit the global scope");
		scopeLevels.remove(0);
	}

	public void declare(String name, T info) {
		var currentLevel = scopeLevels.get(0);
		if (currentLevel.containsKey(name))
			throw new EnvironmentException("redeclared variable " + name);
		currentLevel.put(name, info);
	}

	public T lookup(String name) {
		for (var level : scopeLevels) {
			if (level.containsKey(name)) return level.get(name);
		}
		throw new EnvironmentException("undeclared variable " + name);
	}

	public void update(String name, T info) {
		for (var level : scopeLevels) {
			if (level.containsKey(name)) {
				level.put(name, info);
				return;
			}
		}
		throw new EnvironmentException("undeclared variable " + name);
	}
}
