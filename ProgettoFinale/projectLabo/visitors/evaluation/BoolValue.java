package projectLabo.visitors.evaluation;

public class BoolValue implements Value {
	private final boolean value;

	public BoolValue(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public Value asValue() {
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BoolValue) {
			return value == ((BoolValue) obj).value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Boolean.hashCode(value);
	}

	@Override
	public String toString() {
		return Boolean.toString(value);
	}
}
