package projectLabo.visitors.typechecking;

public class BoolType extends SimpleType {
	public static final BoolType INSTANCE = new BoolType();

	private BoolType() {
	}

	@Override
	public String toString() {
		return "Bool";
	}
}
