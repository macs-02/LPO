package projectLabo.parser.ast;

public class NonEmptyStmtSeq extends NonEmptySeq<Stmt, StmtSeq> implements StmtSeq {

	public NonEmptyStmtSeq(Stmt first, StmtSeq rest) {
		super(first, rest);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}

	public Stmt getFirst() { return first; }
	public StmtSeq getRest() { return rest; }
}
