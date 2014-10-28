<#macro instanceVariable theClass>m${theClass.name?cap_first}$$0</#macro>

package ${generatedComponent.packageName};

import android.os.Bundle;

public class ${generatedComponent.name} extends ${baseComponent.fullyQualifiedName} {

    private ${principalClass.fullyQualifiedName} <@instanceVariable theClass=principalClass/>;
    <#list mixinClasses as mixinClass>
    private ${mixinClass.fullyQualifiedName} <@instanceVariable theClass=mixinClass/>;
    </#list>

    @Override
    protected void onCreate(Bundle savedInstanceState$$0) {
        super.onCreate(savedInstanceState$$0);
        if(<@instanceVariable theClass=principalClass/> == null){
            <@instanceVariable theClass=principalClass/> = new ${principalClass.fullyQualifiedName}();
        } else {
            <@instanceVariable theClass=principalClass/>.onCreate(savedInstanceState$$0);
        }
         <#list mixinClasses as mixinClass>
         if(<@instanceVariable theClass=mixinClass/> == null) {
             <@instanceVariable theClass=mixinClass/> = new ${mixinClass.fullyQualifiedName}(this);
         } else {
             <@instanceVariable theClass=mixinClass/>.onCreate(savedInstanceState$$0);
         }
         </#list>
    }

    <#assign lifeCycleMethodsWithoutParams = ["onStart()", "onResume()", "onPause()", "onStop()", "onDestroy()"]>
    <#list lifeCycleMethodsWithoutParams as lcMethod>
    @Override
    protected void ${lcMethod} {
        super.${lcMethod};
        <@instanceVariable theClass=principalClass/>.${lcMethod};
        <#list mixinClasses as mixinClass>
        <@instanceVariable theClass=mixinClass/>.${lcMethod};
        </#list>
    }
    </#list>

    @Override
    protected void onSaveInstanceState(Bundle outState$$0){
        super.onSaveInstanceState(outState$$0);
        <@instanceVariable theClass=principalClass/>.onSaveInstanceState(outState$$0);
        <#list mixinClasses as mixinClass>
        <@instanceVariable theClass=mixinClass/>.onSaveInstanceState(outState$$0);
        </#list>
    }
}