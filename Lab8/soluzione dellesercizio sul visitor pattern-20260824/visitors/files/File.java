package visitors.files;

import static java.util.Objects.requireNonNull;

public class File implements FileSysTree {

	private String name;
	private int size; // invariant size >= 0

	public File(String name, int size) {
		this.name = requireNonNull(name);
		if (size < 0)
			throw new IllegalArgumentException("File size cannot be negative");
		this.size = size;
	}

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitFile(this.name, this.size);
	}

}
