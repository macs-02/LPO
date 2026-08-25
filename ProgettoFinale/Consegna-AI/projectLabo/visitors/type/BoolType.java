package projectLabo.visitors.type;

public record BoolType() implements StaticType {
	public static final BoolType INSTANCE = new BoolType();

	@Override
	public String nameOfType() {
		return "BOOL";
	}
}
