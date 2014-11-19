package mingle.processor.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;

import mingle.annotations.Mingle;
import mingle.processor.MingleAnnotationProcessor;

public class MingleActivityModel extends AbstractMingleComponentModel {

    public MingleActivityModel(ClassModel principalClass, ClassModel generatedComponent, ClassModel baseComponent, List<ClassModel> mixinClasses) {
        super(baseComponent, principalClass, generatedComponent, mixinClasses);
        for(String lifecycleMethodName: MingleAnnotationProcessor.ACTIVITY_LIFECYCLE_METHOD_NAMES){
            MixinStatement superStatement = new MixinStatement(null, "super", Mingle.ORDER_DEFAULT);
            List<MixinStatement> statementList = new ArrayList<MixinStatement>();
            statementList.add(superStatement);
            mLifecycleStatementMap.put(lifecycleMethodName, statementList);
        }

    }

}
