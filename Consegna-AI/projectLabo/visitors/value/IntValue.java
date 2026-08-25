package projectLabo.visitors.value;

public record IntValue(int value) implements Value {
	@Override
	public String toStringValue() {
		return String.valueOf(value);
	}
}
