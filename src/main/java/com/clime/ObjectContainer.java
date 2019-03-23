package com.clime;

import java.lang.reflect.Method;
import java.util.Map;

public class ObjectContainer {

    private Object object;
    private Map<String, Method> methods;

    public ObjectContainer(Object object, Map<String, Method> methods) {
        this.object = object;
        this.methods = methods;
    }

    public Object getObject() {
        return object;
    }

    public Map<String, Method> getMethods() {
        return methods;
    }
}
