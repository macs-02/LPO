package projectLabo.parser.ast;

import static java.util.Objects.requireNonNull;

import projectLabo.visitors.Visitor;

public class Vector implements Exp {
	private final Exp exp;

	public Vector(Exp exp) {
		this.exp = requireNonNull(exp);
	}

	public Exp getExp() { return exp; }

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitVector(this);
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getClass().getSimpleName(), exp);
	}
}
