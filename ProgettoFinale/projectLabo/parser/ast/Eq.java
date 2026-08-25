package projectLabo.parser.ast;

public class Eq extends BinaryOp {
	public Eq(Exp left, Exp right) {
		super(left, right);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
