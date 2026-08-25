package projectLabo.parser.ast;

public class Minus extends UnaryOp {

	public Minus(Exp exp) {
		super(exp);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
