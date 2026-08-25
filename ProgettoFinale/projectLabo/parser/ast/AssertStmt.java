package projectLabo.parser.ast;

import static java.util.Objects.requireNonNull;

public class AssertStmt implements Stmt {
	private final Exp exp;

	public AssertStmt(Exp exp) {
		this.exp = requireNonNull(exp);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getClass().getSimpleName(), exp);
	}

	public Exp getExp() { return exp; }
}
