package projectLabo.visitors.typechecking;

public abstract class SimpleType implements Type {
	@Override
	public Type checkEqual(Type other) {
		if (this.getClass().equals(other.getClass())) {
			return this;
		}
		throw new RuntimeException("Type error: expected " + this + " but found " + other);
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && getClass().equals(obj.getClass());
	}
}
