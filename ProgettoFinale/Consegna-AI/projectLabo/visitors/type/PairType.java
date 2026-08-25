package projectLabo.visitors.type;

public record PairType(StaticType first, StaticType second) implements StaticType {

	@Override
	public String nameOfType() {
		return "(" + first.nameOfType() + "*" + second.nameOfType() + ")";
	}
}
