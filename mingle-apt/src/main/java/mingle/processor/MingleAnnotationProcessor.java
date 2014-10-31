package mingle.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import mingle.annotations.MingleActivity;
import mingle.processor.model.ClassModel;
import mingle.processor.model.MingleActivityModel;


public class MingleAnnotationProcessor extends AbstractProcessor{

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;
    private Messager messager;
    private Configuration mFreemarkerConfiguration;

    private static final String ANNOTATION_METHOD_NAME_BASE = "base";
    private static final String ANNOTATION_METHOD_NAME_MIXINS = "mixins";


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        if (mFreemarkerConfiguration == null) {
            mFreemarkerConfiguration = initializeFreemarkerConfiguration();
        }
    }

    private Configuration initializeFreemarkerConfiguration() {
        mFreemarkerConfiguration = new Configuration();
        //try {
            //mFreemarkerConfiguration.setDirectoryForTemplateLoading(new File("main/resources"));
            mFreemarkerConfiguration.setClassForTemplateLoading(getClass(), "/META-INF/templates");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        mFreemarkerConfiguration.setObjectWrapper(new DefaultObjectWrapper());
        mFreemarkerConfiguration.setDefaultEncoding("UTF-8");
        mFreemarkerConfiguration.setTemplateUpdateDelay(0);
        mFreemarkerConfiguration.setCacheStorage(new NullCacheStorage());

// Sets how errors will appear. Here we assume we are developing HTML pages.
// For production systems TemplateExceptionHandler.RETHROW_HANDLER is better.
        //cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        //mFreemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);


        mFreemarkerConfiguration.setIncompatibleImprovements(new Version(2, 3, 20));
        return mFreemarkerConfiguration;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new LinkedHashSet<String>(1);
        supportedAnnotationTypes.add(MingleActivity.class.getCanonicalName());
        return supportedAnnotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<MingleActivityModel> activityModelList = parseMingleActivityAnnotations(roundEnv);
        if(activityModelList != null){
            info(String.format("Found %d activities annotated with MingleActivity", activityModelList.size()));
            for(MingleActivityModel model: activityModelList){
                try {
                    emitActivity(model);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TemplateException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    private void emitActivity(MingleActivityModel model) throws IOException, TemplateException {
        final Template activityTemplate = mFreemarkerConfiguration.getTemplate("ActivityTemplate.ftl");
        final JavaFileObject generatedActivity = filer.createSourceFile(model.getGeneratedComponent().getFullyQualifiedName(), model.getOriginatingElements());
        Writer writer = generatedActivity.openWriter();
        activityTemplate.process(model, writer);
        writer.close();
    }

    private List<MingleActivityModel> parseMingleActivityAnnotations(RoundEnvironment roundEnv) {

        Set<? extends Element> elementsAnnotatedWithElem = roundEnv.getElementsAnnotatedWith(MingleActivity.class);
        //info(String.format("elementsAnnotatedWithElem.size = %d", elementsAnnotatedWithElem.size()));
        List<MingleActivityModel> models = new ArrayList<MingleActivityModel>(elementsAnnotatedWithElem.size());
        for(Element elem: elementsAnnotatedWithElem){
            if(elem.getKind() != ElementKind.CLASS) {
                error("This annotation can only be applied to a class");
            } else {
                models.add(extractActivityModel(elem));
            }
        }

        return models;
    }

    private MingleActivityModel extractActivityModel(Element elem) {
        String packageName = elementUtils.getPackageOf(elem).getQualifiedName().toString();

        String baseComponentName = extractBaseComponentName(elem.getAnnotationMirrors());
        List<ClassModel> mixinClasses = extractMixinClassNames(elem.getAnnotationMirrors());

        final ClassModel principalClass = new ClassModel(packageName, elem.getSimpleName().toString());
        final ClassModel baseComponent = new ClassModel(baseComponentName);
        final ClassModel generatedComponent = new ClassModel(packageName, elem.getSimpleName()+"Activity_");
        return new MingleActivityModel(principalClass, generatedComponent, baseComponent, mixinClasses);
    }

    private String extractBaseComponentName(List<? extends AnnotationMirror> annotationMirrors) {

        for(AnnotationMirror mirror: annotationMirrors){
            Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
            for(Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry: elementValues.entrySet()){
                String key = entry.getKey().getSimpleName().toString();
                String value = entry.getValue().getValue().toString();
                //info(String.format("Key = %s; Value = %s", key, value));
                if(ANNOTATION_METHOD_NAME_BASE.equals(key)){
                    return value;
                }
            }
        }

        return "";
    }

    private List<ClassModel> extractMixinClassNames(List<? extends AnnotationMirror> annotationMirrors) {

        for(AnnotationMirror mirror: annotationMirrors){
            Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
            //info("In extractMixinClassNames, elementValues="+elementValues.toString());
            for(Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry: elementValues.entrySet()){
                if(ANNOTATION_METHOD_NAME_MIXINS.equals(entry.getKey().getSimpleName().toString())){
                    List<AnnotationValue> vals = (List<AnnotationValue>)entry.getValue().getValue();
                    List<ClassModel> mixins = new ArrayList<ClassModel>(vals.size());
                    for(AnnotationValue val: vals){
                        mixins.add(new ClassModel(val.getValue().toString()));
                    }
                    return mixins;
                }
            }
        }

        return new ArrayList<ClassModel>();
    }


    private void error(String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }

    private void info(String message) {
        messager.printMessage(Diagnostic.Kind.NOTE, message);
    }
}
