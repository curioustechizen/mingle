package mingle.processor;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import mingle.annotations.Mingle;
import mingle.annotations.MingleActivity;
import mingle.annotations.MingleFragment;
import mingle.annotations.OnCreate;
import mingle.annotations.OnDestroy;
import mingle.annotations.OnPause;
import mingle.annotations.OnResume;
import mingle.annotations.OnSaveInstanceState;
import mingle.annotations.OnStart;
import mingle.annotations.OnStop;
import mingle.processor.model.ClassModel;
import mingle.processor.model.MingleActivityModel;
import mingle.processor.model.MingleFragmentModel;
import mingle.processor.model.MixinStatement;
import mingle.processor.util.NameUtils;


public class MingleAnnotationProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;
    private Messager messager;
    private Configuration mFreemarkerConfiguration;
    private Map<Class, Set<? extends Element>> mAnnotationsToElementsMap;
    private Set<? extends Element> mElementsAnnotatedWithMingleActivity;
    private Set<? extends Element> mElementsAnnotatedWithMingleFragment;
    private Set<Class<? extends Annotation>> mSupportedAnnotationClasses;
    private Set<Class<? extends Annotation>> mSupportedTopLevelAnnotationClasses;
    private Set<Class<? extends Annotation>> mSupportedMethodLevelAnnotationClasses;
    public static final Set<String> ACTIVITY_LIFECYCLE_METHOD_NAMES = new HashSet<String>();
    public static final Set<String> FRAGMENT_LIFECYCLE_METHOD_NAMES = new HashSet<String>();
    static {
        final Set<String> COMMON_LIFECYCLE_METHODS = new HashSet<String>();
        Collections.addAll(COMMON_LIFECYCLE_METHODS, "onCreate", "onStart", "onResume", "onPause", "onStop", "onDestroy", "onSaveInstanceState", "onCreateOptionsMenu", "onPrepareOptionsMenu", "onOptionsItemSelected");
        ACTIVITY_LIFECYCLE_METHOD_NAMES.addAll(COMMON_LIFECYCLE_METHODS);
        FRAGMENT_LIFECYCLE_METHOD_NAMES.addAll(COMMON_LIFECYCLE_METHODS);
        Collections.addAll(FRAGMENT_LIFECYCLE_METHOD_NAMES, "onAttach", "onActivityCreated", "onCreateView", "onViewCreated", "onDetach");
    }



    private static final String ANNOTATION_METHOD_NAME_BASE = "base";
    private static final String ANNOTATION_METHOD_NAME_MIXINS = "mixins";


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();

        initializeSupportedAnnotationClasses();

        if (mFreemarkerConfiguration == null) {
            mFreemarkerConfiguration = initializeFreemarkerConfiguration();
        }
    }

    private void initializeSupportedAnnotationClasses() {
        mSupportedTopLevelAnnotationClasses = new HashSet<Class<? extends Annotation>>(2);
        mSupportedTopLevelAnnotationClasses.add(MingleActivity.class);
        mSupportedTopLevelAnnotationClasses.add(MingleFragment.class);

        mSupportedMethodLevelAnnotationClasses = new HashSet<Class<? extends Annotation>>(7);
        mSupportedMethodLevelAnnotationClasses.add(OnCreate.class);
        mSupportedMethodLevelAnnotationClasses.add(OnStart.class);
        mSupportedMethodLevelAnnotationClasses.add(OnResume.class);
        mSupportedMethodLevelAnnotationClasses.add(OnPause.class);
        mSupportedMethodLevelAnnotationClasses.add(OnStop.class);
        mSupportedMethodLevelAnnotationClasses.add(OnSaveInstanceState.class);
        mSupportedMethodLevelAnnotationClasses.add(OnDestroy.class);

        mSupportedAnnotationClasses = new HashSet<Class<? extends Annotation>>();
        mSupportedAnnotationClasses.addAll(mSupportedTopLevelAnnotationClasses);
        mSupportedAnnotationClasses.addAll(mSupportedMethodLevelAnnotationClasses);

        mAnnotationsToElementsMap = new HashMap<Class, Set<? extends Element>>(getSupportedAnnotationTypes().size());
    }

    private Configuration initializeFreemarkerConfiguration() {
        mFreemarkerConfiguration = new Configuration();
        mFreemarkerConfiguration.setClassForTemplateLoading(getClass(), "/META-INF/templates");
        mFreemarkerConfiguration.setObjectWrapper(new DefaultObjectWrapper());
        mFreemarkerConfiguration.setDefaultEncoding("UTF-8");
        mFreemarkerConfiguration.setTemplateUpdateDelay(1);
        mFreemarkerConfiguration.setCacheStorage(new NullCacheStorage());

        mFreemarkerConfiguration.setIncompatibleImprovements(new Version(2, 3, 20));
        return mFreemarkerConfiguration;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new LinkedHashSet<String>();
        supportedAnnotationTypes.add(MingleActivity.class.getCanonicalName());
        supportedAnnotationTypes.add(MingleFragment.class.getCanonicalName());
        /*supportedAnnotationTypes.add(OnCreate.class.getCanonicalName());
        supportedAnnotationTypes.add(OnStart.class.getCanonicalName());
        supportedAnnotationTypes.add(OnResume.class.getCanonicalName());
        supportedAnnotationTypes.add(OnPause.class.getCanonicalName());
        supportedAnnotationTypes.add(OnStop.class.getCanonicalName());
        supportedAnnotationTypes.add(OnDestroy.class.getCanonicalName());
        supportedAnnotationTypes.add(OnSaveInstanceState.class.getCanonicalName());*/
        return supportedAnnotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_6;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if(!roundEnv.processingOver()) {
            for (Class<? extends Annotation> type : mSupportedAnnotationClasses) {
                mAnnotationsToElementsMap.put(type, roundEnv.getElementsAnnotatedWith(type));
                processMingleActivities();
                //processMingleFragments();
            }
        }
        return false;
    }

    private void processMingleActivities() {
        List<MingleActivityModel> activityModelList = parseMingleActivityAnnotations();
        if (activityModelList != null) {
            info(String.format("Found %d activities annotated with MingleActivity", activityModelList.size()));
            for (MingleActivityModel model : activityModelList) {
                try {
                    emitActivity(model);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TemplateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<MingleActivityModel> parseMingleActivityAnnotations() {
        mElementsAnnotatedWithMingleActivity = mAnnotationsToElementsMap.get(MingleActivity.class);
        List<MingleActivityModel> models = new ArrayList<MingleActivityModel>(mElementsAnnotatedWithMingleActivity.size());
        for (Element elem : mElementsAnnotatedWithMingleActivity) {
            if (elem.getKind() != ElementKind.CLASS) {
                error("This annotation can only be applied to a class");
            } else {
                models.add(extractActivityModel(elem));
            }
        }

        return models;
    }

    private void emitActivity(MingleActivityModel model) throws IOException, TemplateException {
        final Template activityTemplate = mFreemarkerConfiguration.getTemplate("ActivityTemplateAdvanced.ftl");
        final JavaFileObject generatedActivity = filer.createSourceFile(model.getGeneratedComponent().getFullyQualifiedName(), model.getOriginatingElements());
        Writer writer = generatedActivity.openWriter();
        activityTemplate.process(model, writer);
        writer.close();
    }


    private MingleActivityModel extractActivityModel(Element elem) {
        String packageName = elementUtils.getPackageOf(elem).getQualifiedName().toString();

        String baseComponentName = extractBaseComponentName(elem.getAnnotationMirrors());
        List<ClassModel> mixinClasses = extractMixinClassNames(elem.getAnnotationMirrors());
        final ClassModel principalClass = new ClassModel(packageName, elem.getSimpleName().toString());
        final ClassModel baseComponent = new ClassModel(baseComponentName);
        final ClassModel generatedComponent = new ClassModel(packageName, elem.getSimpleName() + "Activity_");
        final MingleActivityModel activityModel = new MingleActivityModel(principalClass, generatedComponent, baseComponent, mixinClasses);

        addStatementsForPrincipalClass(activityModel, principalClass, elem);
        addStatementsForMixinClasses(activityModel, mixinClasses);
        return activityModel;
    }

    private void addStatementsForPrincipalClass(MingleActivityModel activityModel, ClassModel principalClass, Element elem) {
        List<ExecutableElement> methods = ElementFilter.methodsIn(elem.getEnclosedElements());
        addMixinStatements(activityModel, principalClass, methods);
    }

    private void addStatementsForMixinClasses(MingleActivityModel activityModel, List<ClassModel> mixinClasses) {
        for(ClassModel mixin: mixinClasses){
            TypeElement mixinElement = elementUtils.getTypeElement(mixin.getFullyQualifiedName());
            if(mixinElement != null){
                List<ExecutableElement> mixinMethods = ElementFilter.methodsIn(mixinElement.getEnclosedElements());
                addMixinStatements(activityModel, mixin, mixinMethods);
            }
        }
    }

    private void addMixinStatements(MingleActivityModel activityModel, ClassModel classModel, List<ExecutableElement> methods) {
        for(ExecutableElement aMethod: methods){
            if(ACTIVITY_LIFECYCLE_METHOD_NAMES.contains(aMethod.getSimpleName())){
                activityModel.addMixinStatement(
                        aMethod.getSimpleName().toString(),
                        new MixinStatement(
                                classModel.getFullyQualifiedName(),
                                NameUtils.variableNameForClass(classModel.getFullyQualifiedName()),
                                Mingle.ORDER_DEFAULT));
            }
        }
    }

    private void processMingleFragments() {
        List<MingleFragmentModel> fragmentModelList = parseMingleFragmentAnnotations();
        if (fragmentModelList != null) {
            info(String.format("Found %d fragments annotated with MingleFragment", fragmentModelList.size()));
            for (MingleFragmentModel model : fragmentModelList) {
                try {
                    emitFragment(model);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TemplateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<MingleFragmentModel> parseMingleFragmentAnnotations() {
        mElementsAnnotatedWithMingleFragment = mAnnotationsToElementsMap.get(MingleFragment.class);
        List<MingleFragmentModel> models = new ArrayList<MingleFragmentModel>(mElementsAnnotatedWithMingleFragment.size());
        for (Element elem : mElementsAnnotatedWithMingleFragment) {
            if (elem.getKind() != ElementKind.CLASS) {
                error("This annotation can only be applied to a class");
            } else {
                models.add(extractFragmentModel(elem));
            }
        }

        return models;
    }

    private MingleFragmentModel extractFragmentModel(Element elem) {
        String packageName = elementUtils.getPackageOf(elem).getQualifiedName().toString();

        String baseComponentName = extractBaseComponentName(elem.getAnnotationMirrors());
        List<ClassModel> mixinClasses = extractMixinClassNames(elem.getAnnotationMirrors());
        List<ExecutableElement> methods = ElementFilter.methodsIn(elem.getEnclosedElements());
        for(ExecutableElement aMethod: methods){
            List<? extends AnnotationMirror> annotationMirrors = aMethod.getAnnotationMirrors();
            for(AnnotationMirror mirror: annotationMirrors){

                if(getSupportedAnnotationTypes().contains(mirror.getAnnotationType().asElement().getSimpleName().toString())){
                    //mirror.
                }
            }
        }

        final ClassModel principalClass = new ClassModel(packageName, elem.getSimpleName().toString());
        final ClassModel baseComponent = new ClassModel(baseComponentName);
        final ClassModel generatedComponent = new ClassModel(packageName, elem.getSimpleName() + "Fragment_");
        return new MingleFragmentModel(principalClass, generatedComponent, baseComponent, mixinClasses);
    }

    private void emitFragment(MingleFragmentModel model) throws IOException, TemplateException {
        final Template fragmentTemplate = mFreemarkerConfiguration.getTemplate("FragmentTemplate.ftl");
        final JavaFileObject generatedFragment = filer.createSourceFile(model.getGeneratedComponent().getFullyQualifiedName(), model.getOriginatingElements());
        Writer writer = generatedFragment.openWriter();
        fragmentTemplate.process(model, writer);
        writer.close();
    }

    private String extractBaseComponentName(List<? extends AnnotationMirror> annotationMirrors) {

        for (AnnotationMirror mirror : annotationMirrors) {
            Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
                String key = entry.getKey().getSimpleName().toString();
                String value = entry.getValue().getValue().toString();
                //info(String.format("Key = %s; Value = %s", key, value));
                if (ANNOTATION_METHOD_NAME_BASE.equals(key)) {

                    return value;
                }
            }
        }

        return "";
    }

    private List<ClassModel> extractMixinClassNames(List<? extends AnnotationMirror> annotationMirrors) {

        for (AnnotationMirror mirror : annotationMirrors) {
            Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
            //info("In extractMixinClassNames, elementValues="+elementValues.toString());
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
                if (ANNOTATION_METHOD_NAME_MIXINS.equals(entry.getKey().getSimpleName().toString())) {
                    List<AnnotationValue> vals = (List<AnnotationValue>) entry.getValue().getValue();
                    List<ClassModel> mixins = new ArrayList<ClassModel>(vals.size());
                    for (AnnotationValue val : vals) {
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
