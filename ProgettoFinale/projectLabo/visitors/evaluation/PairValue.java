package projectLabo.visitors.evaluation;

public class PairValue implements Value {
	private final Value fstValue;
	private final Value sndValue;

	public PairValue(Value fstValue, Value sndValue) {
		this.fstValue = fstValue;
		this.sndValue = sndValue;
	}

	public Value getFstValue() {
		return fstValue;
	}

	public Value getSndValue() {
		return sndValue;
	}

	@Override
	public Value asValue() {
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PairValue) {
			PairValue pv = (PairValue) obj;
			return fstValue.equals(pv.fstValue) && sndValue.equals(pv.sndValue);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return fstValue.hashCode() + 31 * sndValue.hashCode();
	}

	@Override
	public String toString() {
		return "(" + fstValue + "," + sndValue + ")";
	}
}
