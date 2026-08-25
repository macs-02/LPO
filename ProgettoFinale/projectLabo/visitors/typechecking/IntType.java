package projectLabo.visitors.typechecking;

public class IntType extends SimpleType {
	public static final IntType INSTANCE = new IntType();

	private IntType() {
	}

	@Override
	public String toString() {
		return "Int";
	}
}
