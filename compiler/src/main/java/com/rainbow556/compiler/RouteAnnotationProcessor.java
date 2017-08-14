package com.rainbow556.compiler;

import com.google.auto.service.AutoService;
import com.rainbow556.annotation.Route;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by lixiang on 2017/8/12.
 */
@AutoService(Processor.class)
public class RouteAnnotationProcessor extends AbstractProcessor{
    private Elements mElementUtils;
    private Filer mFiler;
    private Messager mMessager;
    private boolean isFirst = true;

    @Override
    public synchronized void init(ProcessingEnvironment env){
        super.init(env);
        mElementUtils = env.getElementUtils();
        mFiler = env.getFiler();
        mMessager = env.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes(){
        Set<String> set = new HashSet<>();
        set.add(Route.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion(){
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment env){
        if(!isFirst){
            return false;
        }
        isFirst = false;
        final String prefix = "route://com.simplerouter.app";
        Set<? extends Element> elements = env.getElementsAnnotatedWith(Route.class);
        ClassName routeEntryName = ClassName.get("com.rainbow556.router_api", "RouteEntry");
        ClassName listName = ClassName.get(List.class);
        TypeName entryTypeName = ParameterizedTypeName.get(listName, routeEntryName);
        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("getRouteEntries")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(entryTypeName)
                .addStatement("$T list = new $T(30)", entryTypeName, ArrayList.class)
                .addStatement("$T entry = null", routeEntryName);
        for(Element e : elements){
            if(e.getKind() != ElementKind.CLASS){
                return false;
            }
            TypeElement typeElement = (TypeElement) e;
            Route route = typeElement.getAnnotation(Route.class);
            methodSpecBuilder.addStatement("entry = new $T()", routeEntryName);
            String[] values = route.value();
            int len = values.length;
            for(int i = 0; i < len; i++){
                methodSpecBuilder.addStatement("entry.addUrl($S)", prefix + values[i]);
                if(i == len - 1){
                    methodSpecBuilder.addStatement("entry.setTarget($T.class)", typeElement.asType());
                }
            }
            methodSpecBuilder.addStatement("list.add(entry)");
        }
        methodSpecBuilder.addStatement("return list");
        TypeSpec.Builder builder = TypeSpec.classBuilder("RouteEntryLoader")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        builder.addMethod(methodSpecBuilder.build());
        JavaFile javaFile = JavaFile.builder("com.rainbow556", builder.build()).build();
        info("-----------------------");
        info(javaFile.toString());
        info("-----------------------");
        try{
            javaFile.writeTo(mFiler);
        }catch(IOException e1){
            e1.printStackTrace();
        }
        return true;
    }

    private void info(String format, Object... obj){
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(format, obj));
    }
}
