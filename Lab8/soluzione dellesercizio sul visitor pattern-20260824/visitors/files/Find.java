package visitors.files;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class Find implements Visitor<Boolean> {
    private final String name; // name to find

    public Find(String name) {
        this.name = requireNonNull(name);
    }

    public Boolean visitFile(String name, int size) {
		return this.name.equals(name);
    }

    public Boolean visitFolder(List<FileSysTree> children) {
        for (FileSysTree node : children)
            if (node.accept(this))
                return true;
        return false;
    }
}