package visitors.files;

import java.util.List;

public interface Visitor<T> {
	T visitFile(String name, int size);

	T visitFolder(List<FileSysTree> children);
}
