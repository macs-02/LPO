package projectLabo.visitors.value;

public record BoolValue(boolean value) implements Value {
	@Override
	public String toStringValue() {
		return value ? "true" : "false";
	}
}
