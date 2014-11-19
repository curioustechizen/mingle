package mingle.processor.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;

public class AbstractMingleComponentModel {

    private final ClassModel mBaseComponent;
    private final ClassModel mPrincipalClass;
    private final ClassModel mGeneratedComponent;
    private final List<ClassModel> mMixinClasses;
    protected final Map<String, List<MixinStatement>> mLifecycleStatementMap;


    public AbstractMingleComponentModel(ClassModel baseComponent, ClassModel principalClass, ClassModel generatedComponent, List<ClassModel> mixinClasses) {
        mBaseComponent = baseComponent;
        mPrincipalClass = principalClass;
        mGeneratedComponent = generatedComponent;
        this.mMixinClasses = mixinClasses;
        mLifecycleStatementMap = new HashMap<String, List<MixinStatement>>();
    }

    public void addMixinStatement(String method, MixinStatement stmt){
        mLifecycleStatementMap.get(method).add(stmt);
    }

    public ClassModel getGeneratedComponent(){
        return this.mGeneratedComponent;
    }

    public Element getOriginatingElements() {
        return null;
    }

    public ClassModel getPrincipalClass() {
        return this.mPrincipalClass;
    }

    public ClassModel getBaseComponent(){
        return this.mBaseComponent;
    }

    public List<ClassModel> getMixinClasses(){
        return this.mMixinClasses;
    }
}
