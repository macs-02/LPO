package projectLabo.visitors.typechecking;

public interface Type {
	Type checkEqual(Type other);
}
