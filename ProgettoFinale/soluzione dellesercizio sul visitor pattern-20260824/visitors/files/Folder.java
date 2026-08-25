package visitors.files;

import java.util.LinkedList;
import java.util.List;
import static java.util.Objects.requireNonNull;

public class Folder implements FileSysTree {

	private final List<FileSysTree> children = new LinkedList<>();

	public Folder(FileSysTree... children) {
		for (var node : children)
			this.children.add(requireNonNull(node));
	}

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitFolder(this.children);
	}

}
