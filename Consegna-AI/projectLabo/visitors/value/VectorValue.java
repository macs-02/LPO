package projectLabo.visitors.value;

import java.util.List;

public record VectorValue(List<Value> elements) implements Value {
	@Override
	public String toStringValue() {
		return "VectorValue[" + elements.size() + "]";
	}
}
