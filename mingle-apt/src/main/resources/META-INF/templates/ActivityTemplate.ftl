<#--
<#macro instanceVariable theClass>m${theClass.name?cap_first}$$0</#macro>
-->
package ${generatedComponent.packageName};

import android.os.Bundle;

public class ${generatedComponent.name} extends ${baseComponent.fqcn} {

    private ${principalClass.fqcn} ${principalClass.variableName};
    <#list mixinClasses as mixinClass>
    private ${mixinClass.fqcn} ${mixinClass.variableName};
    </#list>

    @Override
    protected void onCreate(Bundle savedInstanceState$$0) {
        <#list mixinClasses as mixinClass>
        if(${mixinClass.variableName} == null){
            ${mixinClass.variableName} = new ${mixinClass.fqcn}(this);
        }
        </#list>
        <#list onCreateStatementList as onCreate>
        ${onCreate.variableName}.onCreate(savedInstanceState$$0);
        </#list>
    }

    <#assign lifeCycleMethodsWithoutParams = ["onStart", "onResume", "onPause", "onStop", "onDestroy"]>
    <#list lifeCycleMethodsWithoutParams as lcMethod>
    @Override
    protected void ${lcMethod}() {
        <#list .vars[lcMethod+"StatementList"] as lcMethodStatement>
        ${lcMethodStatement.variableName}.${lcMethod}();
        </#list>
    }
    </#list>

    @Override
    protected void onSaveInstanceState(Bundle outState$$0){
        <#list onSaveInstanceStateStatementList as onSaveStmt>
        ${onSaveStmt.variableName}.onSaveInstanceState(outState$$0);
        </#list>
    }
}