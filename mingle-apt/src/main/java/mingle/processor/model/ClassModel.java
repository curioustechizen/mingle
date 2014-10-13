package mingle.processor.model;


public class ClassModel {
    private final String name;
    private final String packageName;
    private final String fullyQualifiedName;

    public ClassModel(String fqName){
        this.fullyQualifiedName = fqName;
        int lastDot = fqName.lastIndexOf(".");
        this.packageName = fqName.substring(0, lastDot-1);
        this.name = fqName.substring(lastDot+1);
    }

    public ClassModel(String packageName, String name) {
        this.packageName = packageName;
        this.name = name;
        this.fullyQualifiedName = this.packageName+"."+this.name;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }
}
