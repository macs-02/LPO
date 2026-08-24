package visitors.files;

public interface FileSysTree {

	<T> T accept(Visitor<T> v);
}
