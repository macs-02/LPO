package projectLabo.visitors.value;

public sealed interface Value permits IntValue, BoolValue, PairValue, VectorValue {
	String toStringValue();
}
