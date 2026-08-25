package projectLabo.parser.ast;

public class Not extends UnaryOp {
	public Not(Exp exp) {
		super(exp);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
