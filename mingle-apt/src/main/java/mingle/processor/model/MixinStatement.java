package mingle.processor.model;

public class MixinStatement {

    private final String fqcn;
    private final String variableName;
    private final int order;

    public MixinStatement(String fqcn, String variableName, int order) {
        this.fqcn = fqcn;
        this.variableName = variableName;
        this.order = order;
    }

    public String getFqcn(){
        return this.fqcn;
    }

    public String getVariableName(){
        return this.variableName;
    }

    public int getOrder(){
        return this.order;
    }
}
