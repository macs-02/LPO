package projectLabo.parser.ast;

public class EmptyStmtSeq extends EmptySeq implements StmtSeq {

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
