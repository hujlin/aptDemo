package com.example;

import com.example.anno.Test;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.example.anno.Test"})
public class TestProcessor extends AbstractProcessor{
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        TypeSpec.Builder tb = TypeSpec.classBuilder("TestClass").addModifiers(Modifier.PUBLIC);
        MethodSpec.Builder mb =  MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class,"args");
        for(TypeElement typeElement: ElementFilter.typesIn(roundEnvironment.getElementsAnnotatedWith(Test.class))){
          CodeBlock cb =  CodeBlock.builder()
                    .addStatement("$T.out.println(\"$L + $L\")", System.class,
                            typeElement.getAnnotation(Test.class).value(), typeElement.getSimpleName())
                    .build();
            mb.addCode(cb);
        }
        tb.addMethod(mb.build());
        JavaFile jf =JavaFile.builder("com.example.apt",tb.build()).build();
        try {
            jf.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
