package mingle.processor.model;


import java.util.List;

import javax.lang.model.element.Element;

public class MingleActivityModel {
    private final ClassModel generatedComponent;
    private final ClassModel baseComponent;
    private final List<ClassModel> mixinClasses;

    public MingleActivityModel(ClassModel generatedComponent, ClassModel baseComponent, List<ClassModel> mixinClasses) {
        this.generatedComponent = generatedComponent;
        this.baseComponent = baseComponent;
        this.mixinClasses = mixinClasses;
    }

    public ClassModel getGeneratedComponent() {
        return generatedComponent;
    }

    public ClassModel getBaseComponent() {
        return baseComponent;
    }

    public List<ClassModel> getMixinClasses() {
        return mixinClasses;
    }

    public Element getOriginatingElements() {
        return null;
    }
}
