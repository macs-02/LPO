package projectLabo.parser.ast;

public class Add extends BinaryOp {
	public Add(Exp left, Exp right) {
		super(left, right);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
