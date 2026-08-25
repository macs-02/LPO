package projectLabo.parser.ast;

import static java.util.Objects.requireNonNull;

public class AssertStmt implements Stmt {
	private final Exp exp;

	public AssertStmt(Exp exp) {
		this.exp = requireNonNull(exp);
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getClass().getSimpleName(), exp);
	}
}
