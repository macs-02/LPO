package projectLabo.visitors.evaluation;

public class IntValue implements Value {
	private final int value;

	public IntValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public Value asValue() {
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntValue) {
			return value == ((IntValue) obj).value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(value);
	}

	@Override
	public String toString() {
		return Integer.toString(value);
	}
}
