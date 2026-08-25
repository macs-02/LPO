package projectLabo.parser.ast;

public class BoolLiteral extends AtomicLiteral<Boolean> {

	public BoolLiteral(boolean b) {
		super(b);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
