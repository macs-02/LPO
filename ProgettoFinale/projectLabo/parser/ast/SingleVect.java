package projectLabo.parser.ast;

public class SingleVect extends UnaryOp {
	public SingleVect(Exp exp) {
		super(exp);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
