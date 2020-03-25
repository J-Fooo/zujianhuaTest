package com.jf.compiler;

import com.google.auto.service.AutoService;
import com.jf.annotation.Parameter;
import com.jf.compiler.config.ProcessorConfig;
import com.jf.compiler.factory.ParameterFactory;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

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
@SupportedAnnotationTypes({"com.jf.annotation.Parameter"}) // 监控这个注解
@SupportedSourceVersion(SourceVersion.RELEASE_7) // 必须写
public class ParameterProcess extends AbstractProcessor {
    // 操作Element的工具类（类，函数，属性，其实都是Element）
    private Elements elementTool;
    // type(类信息)的工具类，包含用于操作TypeMirror的工具方法
    private Types typeTool;
    // Message用来打印 日志相关信息  == Log.i
    private Messager messager;  // Gradle 日志中输出
    // 文件生成器， 类 资源 等，就是最终要生成的文件 是需要Filer来完成的
    private Filer filer;

    private Map<TypeElement, List<Element>> parameterMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        elementTool = processingEnvironment.getElementUtils();
        typeTool = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE, "TypeElement set ------ : " + set.size());
        if (set.isEmpty()) {
            return false;
        }
        //获取被parameter注解的元素的集合
        Set<? extends Element> parameterElements = roundEnvironment.getElementsAnnotatedWith(Parameter.class);
        for (Element parElement : parameterElements) {
            // 注解在属性的上面，属性节点父节点 是 类节点
            TypeElement parentEle = (TypeElement) parElement.getEnclosingElement();
            messager.printMessage(Diagnostic.Kind.NOTE, "parElement : " + parElement);
            messager.printMessage(Diagnostic.Kind.NOTE, "parentEle为 : " + parentEle);
            // 如果Map集合中的key：类节点存在，直接添加属性
            if (parameterMap.containsKey(parentEle)) {
                parameterMap.get(parentEle).add(parElement);
            } else {
                List<Element> eleList = new ArrayList<>();
                eleList.add(parElement);
                parameterMap.put(parentEle, eleList);
            }
        }//for end
        /*public class Order_MainActivity$$Parameter implements ParameterGet {
            @Override
            public void getParameter(Object targetParameter) {
                Order_MainActivity t = (Order_MainActivity) targetParameter;
                t.name = t.getIntent().getStringExtra("name");
            }
        }*/

        TypeElement parameterType = elementTool.getTypeElement(ProcessorConfig.PARAMETER_API);


        messager.printMessage(Diagnostic.Kind.NOTE, "仓库parameterMap的长度为 : " + parameterMap.size());

        for (Map.Entry<TypeElement, List<Element>> listEntry : parameterMap.entrySet()) {
            TypeElement classEle = listEntry.getKey();
            TypeMirror eleMirror = classEle.asType();
            TypeMirror activityMirror = parameterType.asType();
            // 如果类名的类型和Activity类型不匹配
            if (typeTool.isSubtype(eleMirror, activityMirror)) {
                throw new RuntimeException("出错，@Parameter注解目前仅限用于Activity类之上");
            }

            List<Element> parameterList = listEntry.getValue();
            //创建方法参数
            ParameterSpec parameterSpec = ParameterSpec.builder(Object.class, "targetParameter").build();
            //创建方法
            ParameterFactory factory = new ParameterFactory.Builder(parameterSpec)
                    .setClassName(ClassName.get(classEle))
                    .setMessager(messager)
                    .build();


            ClassName className = ClassName.get(classEle);

            factory.addFirstStatement();
            for (Element element : parameterList) {
                factory.addMultipleStatement(element);
            }


            // 最终生成的类文件名（类名$$Parameter）    Order_MainActivity$$Parameter
            String finalClassName = classEle.getSimpleName() + "$$$Parameter";
            messager.printMessage(Diagnostic.Kind.NOTE, "APT生成获取参数类文件：" +
                    className.packageName() + "." + finalClassName);

            // 开始生成文件，例如：PersonalMainActivity$$Parameter
            try {
                messager.printMessage(Diagnostic.Kind.NOTE, "创建类-----------");
                JavaFile.builder(className.packageName(), // 包名
                        TypeSpec.classBuilder(finalClassName) // 类名
                                .addSuperinterface(ClassName.get(parameterType)) // 实现ParameterLoad接口
                                .addModifiers(Modifier.PUBLIC) // public修饰符
                                .addMethod(factory.build()) // 方法的构建（方法参数 + 方法体）
                                .build()) // 类构建完成
                        .build()// JavaFile构建完成
                        .writeTo(filer); // 文件生成器开始生成类文件
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return true;
    }
}
