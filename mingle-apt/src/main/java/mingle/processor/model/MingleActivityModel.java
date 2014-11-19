package mingle.processor.model;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import mingle.Mingle;
import mingle.processor.MingleAnnotationProcessor;

public class MingleActivityModel extends AbstractMingleComponentModel {

    private static final Comparator<MixinStatement> MIXIN_ORDER_COMPARATOR = new Comparator<MixinStatement>() {
        @Override
        public int compare(MixinStatement o1, MixinStatement o2) {
            return o1.getOrder() - o2.getOrder();
        }
    };

    public MingleActivityModel(ClassModel principalClass, ClassModel generatedComponent, ClassModel baseComponent, List<ClassModel> mixinClasses) {
        super(baseComponent, principalClass, generatedComponent, mixinClasses);
        for(String lifecycleMethodName: MingleAnnotationProcessor.ACTIVITY_LIFECYCLE_METHOD_NAMES){
            MixinStatement superStatement = new MixinStatement(null, "super", Mingle.ORDER_DEFAULT);
            List<MixinStatement> statementList = new ArrayList<MixinStatement>();
            statementList.add(superStatement);
            mLifecycleStatementMap.put(lifecycleMethodName, statementList);
        }

    }

    public List<MixinStatement> getOnCreateStatementList(){
        List<MixinStatement> list = mLifecycleStatementMap.get("onCreate");
        Collections.sort(list, MIXIN_ORDER_COMPARATOR);
        return list;
    }

    public List<MixinStatement> getOnStartStatementList(){
        List<MixinStatement> list = mLifecycleStatementMap.get("onStart");
        Collections.sort(list, MIXIN_ORDER_COMPARATOR);
        return list;
    }

    public List<MixinStatement> getOnResumeStatementList(){
        List<MixinStatement> list = mLifecycleStatementMap.get("onResume");
        Collections.sort(list, MIXIN_ORDER_COMPARATOR);
        return list;
    }

    public List<MixinStatement> getOnPauseStatementList(){
        List<MixinStatement> list = mLifecycleStatementMap.get("onPause");
        Collections.sort(list, MIXIN_ORDER_COMPARATOR);
        return list;
    }

    public List<MixinStatement> getOnStopStatementList(){
        List<MixinStatement> list = mLifecycleStatementMap.get("onStop");
        Collections.sort(list, MIXIN_ORDER_COMPARATOR);
        return list;
    }

    public List<MixinStatement> getOnDestroyStatementList(){
        List<MixinStatement> list = mLifecycleStatementMap.get("onDestroy");
        Collections.sort(list, MIXIN_ORDER_COMPARATOR);
        return list;
    }

    public List<MixinStatement> getOnSaveInstanceStateStatementList(){
        List<MixinStatement> list = mLifecycleStatementMap.get("onSaveInstanceState");
        Collections.sort(list, MIXIN_ORDER_COMPARATOR);
        return list;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
