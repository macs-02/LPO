package projectLabo.parser.ast;

public class Fst extends UnaryOp {

	public Fst(Exp exp) {
		super(exp);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
