package projectLabo.visitors.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class VectorValue implements Value {
	private final List<Value> elements;

	public VectorValue(List<Value> elements) {
		this.elements = new ArrayList<>(elements);
	}
	
	public VectorValue() {
		this.elements = new ArrayList<>();
	}

	public List<Value> getElements() {
		return Collections.unmodifiableList(elements);
	}

	@Override
	public Value asValue() {
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VectorValue) {
			VectorValue vv = (VectorValue) obj;
			return elements.equals(vv.elements);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	@Override
	public String toString() {
		return "VectorValue[" + elements.size() + "]";
	}
}
