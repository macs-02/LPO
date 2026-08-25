package projectLabo.parser.ast;

import static java.util.Objects.requireNonNull;

public class PrintStmt implements Stmt {
	private final Exp exp;

	public PrintStmt(Exp exp) {
		this.exp = requireNonNull(exp);
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getClass().getSimpleName(), exp);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}

	public Exp getExp() { return exp; }
}
