package projectLabo.visitors.typechecking;

public class PairType implements Type {
	private final Type fstType;
	private final Type sndType;

	public PairType(Type fstType, Type sndType) {
		this.fstType = fstType;
		this.sndType = sndType;
	}

	public Type getFstType() {
		return fstType;
	}

	public Type getSndType() {
		return sndType;
	}

	@Override
	public Type checkEqual(Type other) {
		if (other instanceof PairType) {
			PairType pt = (PairType) other;
			fstType.checkEqual(pt.fstType);
			sndType.checkEqual(pt.sndType);
			return this;
		}
		throw new RuntimeException("Type error: expected PairType but found " + other);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PairType) {
			PairType pt = (PairType) obj;
			return fstType.equals(pt.fstType) && sndType.equals(pt.sndType);
		}
		return false;
	}

	@Override
	public String toString() {
		return "Pair(" + fstType + "," + sndType + ")";
	}
}
