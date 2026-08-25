package projectLabo.parser.ast;

import static java.util.Objects.requireNonNull;

import projectLabo.visitors.Visitor;

public class PrintStmt implements Stmt {
	private final Exp exp;

	public PrintStmt(Exp exp) {
		this.exp = requireNonNull(exp);
	}

	public Exp getExp() {
		return exp;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitPrintStmt(this);
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getClass().getSimpleName(), exp);
	}

}
