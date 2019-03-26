package model.compiler;

import java.util.List;

public abstract class Compiler {
    protected static Compiler instance;

    public abstract void compile(String path,int threshold,List<String> flags);
}
