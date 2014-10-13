<#macro instanceVariable theClass>m${theClass.name?cap_first}</#macro>

package ${generatedComponent.packageName};

import android.os.Bundle;

public class ${generatedComponent.name} extends ${baseComponent.fullyQualifiedName} {

    <#list mixinClasses as mixinClass>
    private ${mixinClass.fullyQualifiedName} <@instanceVariable theClass=mixinClass/>;
    </#list>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         <#list mixinClasses as mixinClass>
         if(<@instanceVariable theClass=mixinClass/> == null) {
             <@instanceVariable theClass=mixinClass/> = new ${mixinClass.fullyQualifiedName}(this);
         } else {
             <@instanceVariable theClass=mixinClass/>.onCreate(savedInstanceState);
         }
         </#list>
    }

    <#assign lifeCycleMethodsWithoutParams = ["onStart()", "onResume()", "onPause()", "onStop()", "onDestroy()"]>
    <#list lifeCycleMethodsWithoutParams as lcMethod>
    protected void ${lcMethod} {
        super.${lcMethod};
        <#list mixinClasses as mixinClass>
        <@instanceVariable theClass=mixinClass/>.${lcMethod};
        </#list>
    }
    </#list>

    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        <#list mixinClasses as mixinClass>
        <@instanceVariable theClass=mixinClass/>.onSaveInstanceState(outState);
        </#list>
    }
}