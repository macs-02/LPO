package projectLabo.parser.ast;

public class Flatten extends UnaryOp {
	public Flatten(Exp exp) {
		super(exp);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
