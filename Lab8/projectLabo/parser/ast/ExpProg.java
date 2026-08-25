package projectLabo.parser.ast;

import static java.util.Objects.requireNonNull;

public class ExpProg implements Prog {
	private final StmtSeq stmtSeq;

	public ExpProg(StmtSeq stmtSeq) {
		this.stmtSeq = requireNonNull(stmtSeq);
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getClass().getSimpleName(), stmtSeq);
	}

}
