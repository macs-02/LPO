package projectLabo.visitors.type;

public sealed interface StaticType permits IntType, BoolType, PairType, VectorType {
	String nameOfType();
}
