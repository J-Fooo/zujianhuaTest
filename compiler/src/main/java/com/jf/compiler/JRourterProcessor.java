package com.jf.compiler;

import com.google.auto.service.AutoService;
import com.jf.annotation.JRouter;
import com.jf.annotation.bean.RouterBean;
import com.jf.compiler.config.ProcessorConfig;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class) // 编译期 绑定 干活
@SupportedAnnotationTypes({"com.jf.annotation.JRouter"}) // 监控这个注解
@SupportedSourceVersion(SourceVersion.RELEASE_7) // 必须写
// 注解处理器接收的参数
@SupportedOptions({ProcessorConfig.OPTIONS, "packageNameForAPT"})
public class JRourterProcessor extends AbstractProcessor {
    // 操作Element的工具类（类，函数，属性，其实都是Element）
    private Elements elementTool;
    // type(类信息)的工具类，包含用于操作TypeMirror的工具方法
    private Types typeTool;
    // Message用来打印 日志相关信息  == Log.i
    private Messager messager;  // Gradle 日志中输出
    // 文件生成器， 类 资源 等，就是最终要生成的文件 是需要Filer来完成的
    private Filer filer;


    //    private Map<String, RouterBean> pathMap = new HashMap<>();
    private List<RouterBean> mBeanList = new ArrayList<>();
    private String mOptions;
    private String mPackageName;
    private String mSimpleName;
    private String mGroup;
    private String packageNameForAPT;
    private boolean firstCreate = true;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementTool = processingEnvironment.getElementUtils();
        typeTool = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

        mOptions = processingEnvironment.getOptions().get(ProcessorConfig.OPTIONS);
        packageNameForAPT = processingEnvironment.getOptions().get("packageNameForAPT");
        messager.printMessage(Diagnostic.Kind.NOTE, "init >>>>>>>>>");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.isEmpty()) {
            return false;
        }

        //获取被JRouter注解的类的信息element集合
        Set<? extends Element> routerElements = roundEnvironment.getElementsAnnotatedWith(JRouter.class);

        messager.printMessage(Diagnostic.Kind.NOTE, "process >>>>>>>>>" + routerElements.size());
        //此module有多少个被注解的地方，element就有多大
        for (Element element :
                routerElements) {
            mSimpleName = element.getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, "被@ARetuer注解的类有：" + mSimpleName);
            mPackageName = elementTool.getPackageOf(element).toString();
            String qualifiedName = elementTool.getPackageOf(element).getQualifiedName().toString();

            messager.printMessage(Diagnostic.Kind.NOTE, "SimpleName>>>>>>>>>  -- " + mSimpleName);    // MainActivity   可以获取类名
            messager.printMessage(Diagnostic.Kind.NOTE, "packageName>>>>>>>>>  -- " + mPackageName);  // com.test.zujianhuatest  可以获取包名
            //            messager.printMessage(Diagnostic.Kind.NOTE, "qualifiedName>>>>>>>>>  -- " + qualifiedName); // com.test.zujianhuatest

            //获取注解，拿到注解参数
            JRouter jRouter = element.getAnnotation(JRouter.class);
            String routerPath = jRouter.path();
            mGroup = jRouter.group();


            RouterBean routerBean = new RouterBean.Builder()
                    .addGroup(mGroup)
                    .addPath(routerPath)
                    .addElement(element)
                    .build();


            //判断注解是否在activity上
            //            Activity的typeMirror
            TypeMirror activityMirror = elementTool.getTypeElement(ProcessorConfig.ACTIVITY_PACKAGE).asType();
            TypeMirror elementMirror = element.asType();
            if (typeTool.isSubtype(elementMirror, activityMirror)) {
                messager.printMessage(Diagnostic.Kind.NOTE, "SimpleName>>>>>>>>>  -- " + mSimpleName);
                routerBean.setTypeEnum(RouterBean.TypeEnum.ACTIVITY);
            } else {
                throw new RuntimeException(mPackageName + "." + mSimpleName + "出错，@JRouter注解目前仅限用于Activity类之上");
            }

            //判断path和group是否合法
            if (checkRouterPath(routerBean)) {
                messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean Check Success:" + routerBean.toString());
                mBeanList.add(routerBean);
                messager.printMessage(Diagnostic.Kind.NOTE, "routerBean------------->>>>>>>>>  -- " + routerBean.toString());
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "@JRouter注解未按规范配置，如：/app/MainActivity");
            }

            /**
             * public class MainActivity$$$$$$$$$JRouter {
             *   public static Class findTargetClass(String path) {
             *     return path.equals("app/MainActivity") ? MainActivity.class : null;
             *   }
             * }
             */
            /*MethodSpec methodSpec = MethodSpec.methodBuilder("findTargetClass")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(String.class, "path")
                    .returns(Class.class)
                    .addStatement("return path.equals($S) ? $T.class : null", routerPath, ClassName.get(packageName, simpleName))
                    .build();

            TypeSpec typeSpec = TypeSpec.classBuilder(simpleName + "$$$$$$$$$$JRouter")
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodSpec)
                    .build();
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();

            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
                messager.printMessage(Diagnostic.Kind.NOTE, "生成代码失败");
            }*/

            //            public class JRouter$$Path$$personal implements JRouterPath {
            //
            //                @Override
            //                public Map<String, RouterBean> getPathMap() {
            //                    Map<String, RouterBean> pathMap = new HashMap<>();
            //
            //                    pathMap.put("/personal/Personal_MainActivity",
            //                            RouterBean.create(RouterBean.TypeEnum.ACTIVITY,
            //                                    Order_MainActivity.class,
            //                                    "/personal/Personal_MainActivity",
            //                                    "personal"));
            //
            //                    pathMap.put("/personal/Order_Main2Activity",
            //                            RouterBean.create(RouterBean.TypeEnum.ACTIVITY,
            //                                    Personal_Main2Activity.class,
            //                                    "/personal/Personal_Main2Activity",
            //                                    "personal"));
            //                    return pathMap;
            //                }
            //            }
            /*ClassName mapType = ClassName.get(Map.class);
            ClassName strType = ClassName.get(String.class);
            ClassName routerBeanType = ClassName.get(RouterBean.class);
            ClassName hashMapType = ClassName.get(HashMap.class);
            TypeName mapOfRouterBean = ParameterizedTypeName.get(mapType, strType, routerBeanType);
            MethodSpec methodSpec = MethodSpec.methodBuilder("getPathMap")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(mapOfRouterBean)
                    .addStatement("$T pathMap = new $T()", mapOfRouterBean, hashMapType)
                    .addStatement("pathMap.put($S,$T.create($T.TypeEnum.ACTIVITY,$T.class,$S,$S))", routerPath, routerBeanType, routerBeanType, ClassName.get(mPackageName, mSimpleName), routerPath, mGroup)
                    .addStatement("return pathMap")
                    .build();


            TypeSpec typeSpec = TypeSpec.classBuilder(mSimpleName + "$$$$$$$$$$JRouter")
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(JRouterPath.class)
                    .superclass(JrouterTest.class)
                    .addMethod(methodSpec)
                    .build();
            JavaFile javaFile = JavaFile.builder(mPackageName, typeSpec).build();

            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
                messager.printMessage(Diagnostic.Kind.NOTE, "生成代码失败");
            }*/
        }

        TypeElement pathType = elementTool.getTypeElement(ProcessorConfig.JROUTER_API_PATH);
        TypeElement groupType = elementTool.getTypeElement(ProcessorConfig.JROUTER_API_GROUP);

        try {
            createPathMethod(pathType);
        } catch (IOException e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE, "在生成PATH模板时，异常了 e:" + e.getMessage());
        }

        try {
            createGroupMethod(pathType, groupType);
        } catch (IOException e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE, "在生成GROUP模板时，异常了 e:" + e.getMessage());
        }

        return true;
    }

    private void createGroupMethod(TypeElement pathType, TypeElement groupType) throws IOException {
        if (mBeanList.size() == 0)
            return;
        ClassName mapClass = ClassName.get(Map.class);
        ClassName stringClass = ClassName.get(String.class);
        ParameterizedTypeName typeName = ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(ClassName.get(pathType)));
        ParameterizedTypeName mapOfGroup = ParameterizedTypeName.get(mapClass, stringClass, typeName);
        MethodSpec methodSpec = MethodSpec.methodBuilder(ProcessorConfig.GROUP_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("$T $N = new $T()", mapOfGroup, "groupMap", ClassName.get(HashMap.class))
                .addStatement("$N.put($S,$T.class)", "groupMap", mOptions, ClassName.get(packageNameForAPT, ProcessorConfig.PATH_FILE_NAME + mOptions))
                .addStatement("return $N", "groupMap")
                .returns(mapOfGroup)
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(ProcessorConfig.GROUP_FILE_NAME + mOptions)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(groupType))
                .addMethod(methodSpec)
                .build();

        JavaFile javaFile = JavaFile.builder(packageNameForAPT, typeSpec).build();
        javaFile.writeTo(filer);
    }

    private void createPathMethod(TypeElement pathType) throws IOException {
        //所有注解的activity信息保存起来后开始生成方法
        if (mBeanList.size() == 0)
            return;

        ClassName mapClass = ClassName.get(Map.class);
        ClassName stringClass = ClassName.get(String.class);
        ClassName routerClass = ClassName.get(RouterBean.class);
        ClassName hashClass = ClassName.get(HashMap.class);
        TypeName mapOfRouter = ParameterizedTypeName.get(mapClass, stringClass, routerClass);

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(ProcessorConfig.PATH_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .returns(mapOfRouter)
                .addStatement("$T pathMap = new $T()", mapOfRouter, hashClass)
                .addAnnotation(Override.class);


        for (RouterBean routerBean :
                mBeanList) {
            //            pathMap.put("/app/MainActivity",RouterBean.create(RouterBean.TypeEnum.ACTIVITY, MainActivity.class,"/app/MainActivity","app"));
            methodBuilder.addStatement("$N.put($S,$T.create($T.$L, $T.class,$S,$S))", "pathMap", routerBean.getPath(), routerClass, RouterBean.TypeEnum.class, routerBean.getTypeEnum(), ClassName.get((TypeElement) routerBean.getElement()), routerBean.getPath(), routerBean.getGroup());

        }

        MethodSpec methodSpec = methodBuilder.addStatement("return $N", "pathMap")
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(ProcessorConfig.PATH_FILE_NAME + mOptions)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(pathType))
                .addMethod(methodSpec)
                .build();
        JavaFile javaFile = JavaFile.builder(packageNameForAPT, typeSpec).build();

        javaFile.writeTo(filer);
    }


    /**
     * 校验@JRouter注解的值，如果group未填写就从必填项path中截取数据
     *
     * @param bean 路由详细信息，最终实体封装类
     */
    private final boolean checkRouterPath(RouterBean bean) {
        String group = bean.getGroup();
        String path = bean.getPath();
        // @JRouter注解中的path值，必须要以 / 开头（模仿阿里JRouter规范）
        if (ProcessorUtils.isEmpty(path) || !path.startsWith("/")) {
            // ERROR 故意去奔溃的
            messager.printMessage(Diagnostic.Kind.ERROR, "@JRouter注解中的path值，必须要以 / 开头");
            return false;
        }

        // 比如开发者代码为：path = "/MainActivity"，最后一个 / 符号必然在字符串第1位
        if (path.lastIndexOf("/") == 0) {
            // 架构师定义规范，让开发者遵循
            messager.printMessage(Diagnostic.Kind.ERROR, "@JRouter注解未按规范配置，如：/app/MainActivity");
            return false;
        }

        // 从第一个 / 到第二个 / 中间截取，如：/app/MainActivity 截取出 app 作为group
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        // finalGroup == app, personal, order
        // @JRouter注解中的group有赋值情况   用户传递进来时 order，  我截取出来的也必须是 order
        if (!ProcessorUtils.isEmpty(group) && !group.equals(mOptions)) {
            // 架构师定义规范，让开发者遵循
            messager.printMessage(Diagnostic.Kind.ERROR, "@JRouter注解中的group值必须和子模块名一致！");
            return false;
        } else {
            bean.setGroup(finalGroup); // 赋值  order 添加进去了
        }

        return true;
    }
}
