package mingle.processor.model;

import java.util.ArrayList;
import java.util.List;

import mingle.Mingle;
import mingle.processor.MingleAnnotationProcessor;

public class MingleFragmentModel extends AbstractMingleComponentModel{
    public MingleFragmentModel(ClassModel baseComponent, ClassModel principalClass, ClassModel generatedComponent, List<ClassModel> mixinClasses) {
        super(baseComponent, principalClass, generatedComponent, mixinClasses);
        for(String lifecycleMethodName: MingleAnnotationProcessor.FRAGMENT_LIFECYCLE_METHOD_NAMES){
            MixinStatement superStatement = new MixinStatement(null, "super", Mingle.ORDER_DEFAULT);
            List<MixinStatement> statementList = new ArrayList<MixinStatement>();
            statementList.add(superStatement);
            mLifecycleStatementMap.put(lifecycleMethodName, statementList);
        }
    }
}
