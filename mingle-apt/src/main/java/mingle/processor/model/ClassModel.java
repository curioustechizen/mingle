package mingle.processor.model;


public class ClassModel {
    private final String name;
    private final String packageName;
    private final String fullyQualifiedName;
    //private boolean onCreate = true, onStart = true, onResume = false, onPause = false, onDestroy = true, onStop = true, onSaveInstanceState = true;

    public ClassModel(String fqName) {
        this.fullyQualifiedName = fqName;
        int lastDot = fqName.lastIndexOf(".");
        this.packageName = fqName.substring(0, lastDot - 1);
        this.name = fqName.substring(lastDot + 1);
    }

    public ClassModel(String packageName, String name) {
        this.packageName = packageName;
        this.name = name;
        this.fullyQualifiedName = this.packageName + "." + this.name;
    }

    /*public boolean getOnCreate() {
        return onCreate;
    }

    public boolean getOnStart() {
        return onStart;
    }

    public boolean getOnResume() {
        return onResume;
    }

    public boolean getOnPause() {
        return onPause;
    }

    public boolean getOnDestroy() {
        return onDestroy;
    }

    public boolean getOnStop() {
        return onStop;
    }

    public boolean getOnSaveInstanceState() {
        return onSaveInstanceState;
    }*/

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
