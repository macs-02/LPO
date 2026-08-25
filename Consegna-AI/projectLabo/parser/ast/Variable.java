package projectLabo.parser.ast;

import projectLabo.visitors.Visitor;

import static java.util.Objects.requireNonNull;

public record Variable(String name) implements NamedElement, Exp {

	public Variable {
		requireNonNull(name);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitVariable(this);
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getClass().getSimpleName(), name);
	}

}
