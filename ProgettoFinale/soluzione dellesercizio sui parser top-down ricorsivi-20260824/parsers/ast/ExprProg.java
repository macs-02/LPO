package parsers.ast;

import static java.util.Objects.requireNonNull;

/**
 * AST for programs
 */
public class ExprProg implements Prog {
	private final Exp exp;

	public ExprProg(Exp exp) {
		this.exp = requireNonNull(exp);
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getClass().getSimpleName(), exp);
	}

}
