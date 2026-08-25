package projectLabo.visitors.type;

public record IntType() implements StaticType {
	public static final IntType INSTANCE = new IntType();

	@Override
	public String nameOfType() {
		return "INT";
	}
}
