package model;

public abstract class Compiler {
    protected static Compiler instance;

    public abstract void compile(String path,int threshold);
}
