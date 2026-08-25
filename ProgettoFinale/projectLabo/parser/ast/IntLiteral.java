package projectLabo.parser.ast;

public class IntLiteral extends AtomicLiteral<Integer> {

	public IntLiteral(int n) {
		super(n);
	}

	@Override
	public <T> T accept(projectLabo.visitors.Visitor<T> visitor) {
		return visitor.visit(this);
	}
}
