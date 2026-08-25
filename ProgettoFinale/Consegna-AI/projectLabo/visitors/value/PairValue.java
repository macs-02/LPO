package projectLabo.visitors.value;

public record PairValue(Value first, Value second) implements Value {
	@Override
	public String toStringValue() {
		return "(" + first.toStringValue() + "," + second.toStringValue() + ")";
	}
}
