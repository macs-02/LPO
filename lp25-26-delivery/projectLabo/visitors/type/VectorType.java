package projectLabo.visitors.type;

public record VectorType(StaticType elemType, int size) implements StaticType {

	@Override
	public String nameOfType() {
		return elemType.nameOfType() + "[" + size + "]";
	}
}
