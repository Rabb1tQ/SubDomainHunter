package com.rabbitq.util;

import com.rabbitq.annotations.SubDomainInterfaceImplementation;
import com.rabbitq.models.SubDomainInterface;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SubDoaminClassScanner {
    public static List<SubDomainInterface> scan() {
        Set<Class<?>> classes = new Reflections("com.rabbitq.models.impl")  // 替换为您的包名
                .getTypesAnnotatedWith(SubDomainInterfaceImplementation.class);

        return classes.stream()
                .map(clazz -> {
                    try {
                        return (SubDomainInterface) clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }
}