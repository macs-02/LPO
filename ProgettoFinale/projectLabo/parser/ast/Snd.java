package projectLabo.parser.ast;

public class Snd extends UnaryOp {

	public Snd(Exp exp) {
		super(exp);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
