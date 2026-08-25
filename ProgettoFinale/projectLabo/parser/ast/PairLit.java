package projectLabo.parser.ast;

public class PairLit extends BinaryOp {
	public PairLit(Exp left, Exp right) {
		super(left, right);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
