package projectLabo.visitors.typechecking;

public class VectorType implements Type {
	private final Type elemType;
	private final int size; // La semantica statica richiede che il VectorType tracci la dimensione

	public VectorType(Type elemType, int size) {
		this.elemType = elemType;
		this.size = size;
	}

	public Type getElemType() {
		return elemType;
	}

	public int getSize() {
		return size;
	}

	@Override
	public Type checkEqual(Type other) {
		if (other instanceof VectorType) {
			VectorType vt = (VectorType) other;
			if (this.size == vt.size) {
				this.elemType.checkEqual(vt.elemType);
				return this;
			}
			throw new RuntimeException("Type error: expected Vector of size " + size + " but found size " + vt.size);
		}
		throw new RuntimeException("Type error: expected VectorType but found " + other);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VectorType) {
			VectorType vt = (VectorType) obj;
			return size == vt.size && elemType.equals(vt.elemType);
		}
		return false;
	}

	@Override
	public String toString() {
		return "Vector[" + size + "] of " + elemType;
	}
}
