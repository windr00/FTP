package com.ftpserver.event;


import java.lang.reflect.Method;

/**
 * Created by windr on 4/18/16.
 */
public class Event {

    private Object object;

    private String method;

    private Object[] args;

    private Class[] argTypes;

    public Event(Object obj, String mtd) {
        this.object = obj;
        this.method = mtd;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Class[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(Class[] argTypes) {
        this.argTypes = argTypes;
    }

    private void parseArgTypes() {
        this.argTypes = new Class[args.length];

        for (int i = 0; i < args.length; i++) {
            this.argTypes[i] = args[i].getClass();
        }
    }

    void invoke(Object... args) throws Exception {
        this.args = args;
        parseArgTypes();

        Method method = object.getClass().getMethod(this.method, this.argTypes);

        if (method != null) {
            method.invoke(object, args);
        }
    }
}
