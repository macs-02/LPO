package visitors.files;

import java.util.List;

public class Size implements Visitor<Integer> {

    @Override
    public Integer visitFile(String name, int size) {return size;}

    @Override
    public Integer visitFolder(List<FileSysTree> children) {
        var res = 0;
        for (FileSysTree node : children)
            res += node.accept(this);
        return res;
    }

}