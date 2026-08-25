package projectLabo.parser.ast;

public class Zip extends BinaryOp {
	public Zip(Exp left, Exp right) {
		super(left, right);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
