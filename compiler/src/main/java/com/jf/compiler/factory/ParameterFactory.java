package com.jf.compiler.factory;

import com.jf.annotation.Parameter;
import com.jf.compiler.ProcessorUtils;
import com.jf.compiler.config.ProcessorConfig;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

public class ParameterFactory {
/*
    @Override
    public void getParameter(Object targetParameter) {
        Order_MainActivity t = (Order_MainActivity) targetParameter;
        t.name = t.getIntent().getStringExtra("name");
    }*/
    private Messager messager;
    // 类名，如：MainActivity
    private ClassName className;
    private MethodSpec.Builder mMethodBuilder;

    private ParameterFactory() {
    }

    private ParameterFactory(Builder builder) {
        this.messager = builder.messager;
        this.className = builder.className;
        mMethodBuilder = MethodSpec.methodBuilder(ProcessorConfig.PARAMMETER_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(builder.parameterSpec);
    }


    public void addFirstStatement(){
        mMethodBuilder.addStatement("$T $N = ($T) $N", className, "t", className, "targetParameter");
    }


    public MethodSpec build(){
        return mMethodBuilder.build();
    }
    public void addMultipleStatement(Element element){
        // 遍历注解的属性节点 生成函数体
        TypeMirror typeMirror = element.asType();
        // 获取 TypeKind 枚举类型的序列号
        int type = typeMirror.getKind().ordinal();
        // 获取属性名
        String fieldName = element.getSimpleName().toString();
        // 获取注解的值
        String annotationValue = element.getAnnotation(Parameter.class).name();
        // 判断注解的值为空的情况下的处理（注解中有name值就用注解值）
        annotationValue = ProcessorUtils.isEmpty(annotationValue) ? fieldName : annotationValue;
        // TODO 最终拼接的前缀：
        String finalValue = "t." + fieldName;
        // t.s = t.getIntent().
        String methodContent = finalValue + " = t.getIntent().";

        // TypeKind 枚举类型不包含String
        if (type == TypeKind.INT.ordinal()) {
            // t.s = t.getIntent().getIntExtra("age", t.age);
            methodContent += "getIntExtra($S, " + finalValue + ")";
        } else if (type == TypeKind.BOOLEAN.ordinal()) {
            // t.s = t.getIntent().getBooleanExtra("isSuccess", t.age);
            methodContent += "getBooleanExtra($S, " + finalValue + ")";
        } else { // String
            // t.s = t.getIntent.getStringExtra("s");
            if (typeMirror.toString().equalsIgnoreCase("java.lang.String")) {
                methodContent += "getStringExtra($S)";
            }
        }

        // 健壮代码
        if (methodContent.endsWith(")")) {
            // 添加最终拼接方法内容语句
            mMethodBuilder.addStatement(methodContent, annotationValue);
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "目前暂支持String、int、boolean传参");
        }
    }

    public static class Builder {
        // Messager用来报告错误，警告和其他提示信息
        private Messager messager;
        // 类名，如：MainActivity
        private ClassName className;
        // 方法参数体
        private ParameterSpec parameterSpec;

        public Builder(ParameterSpec parameterSpec) {
            this.parameterSpec = parameterSpec;
        }

        public Builder setMessager(Messager messager) {
            this.messager = messager;
            return this;
        }

        public Builder setClassName(ClassName className) {
            this.className = className;
            return this;
        }

        public Builder setParameterSpec(ParameterSpec spec) {
            this.parameterSpec = spec;
            return this;
        }

        public ParameterFactory build() {
            if (parameterSpec == null) {
                throw new IllegalArgumentException("parameterSpec方法参数体为空");
            }

            if (className == null) {
                throw new IllegalArgumentException("方法内容中的className为空");
            }

            if (messager == null) {
                throw new IllegalArgumentException("messager为空，Messager用来报告错误、警告和其他提示信息");
            }
            return new ParameterFactory(this);
        }
    }
}
