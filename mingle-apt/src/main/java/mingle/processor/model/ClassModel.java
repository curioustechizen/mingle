package mingle.processor.model;


import mingle.processor.util.NameUtils;

public class ClassModel {
    private final String name;
    private final String packageName;
    private final String fqcn;
    private final String variableName;
    //private boolean onCreate = true, onStart = true, onResume = false, onPause = false, onDestroy = true, onStop = true, onSaveInstanceState = true;

    public ClassModel(String fqName) {
        this.fqcn = fqName;
        int lastDot = fqName.lastIndexOf(".");
        this.packageName = fqName.substring(0, lastDot);
        this.name = fqName.substring(lastDot + 1);
        this.variableName = NameUtils.variableNameForClass(this.fqcn);
    }

    public ClassModel(String packageName, String name) {
        this.packageName = packageName;
        this.name = name;
        this.fqcn = this.packageName + "." + this.name;
        this.variableName = NameUtils.variableNameForClass(this.fqcn);
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

    public String getFqcn() {
        return fqcn;
    }

    public String getVariableName(){
        return this.variableName;
    }
}
