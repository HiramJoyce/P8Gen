package com.tools.gen.entity;

import java.util.List;

public class Method {

    // note
    private String note;

    // annotations
    private List<Annotation> annotationList;

    // visibility
    private String visibility;

    // is static
    private boolean isStatic;

    // is final
    private boolean isFinal;

    // is sync
    private boolean isSynchronized;

    // return type
    private String returnType = "void";

    // method name
    private String name;

    // params
    private List<Param> paramList;

    // method content
    private String content;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Annotation> getAnnotationList() {
        return annotationList;
    }

    public void setAnnotationList(List<Annotation> annotationList) {
        this.annotationList = annotationList;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public boolean isSynchronized() {
        return isSynchronized;
    }

    public void setSynchronized(boolean aSynchronized) {
        isSynchronized = aSynchronized;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Param> getParamList() {
        return paramList;
    }

    public void setParamList(List<Param> paramList) {
        this.paramList = paramList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Method{" +
                "note='" + note + '\'' +
                ", annotationList=" + annotationList +
                ", visibility='" + visibility + '\'' +
                ", isStatic=" + isStatic +
                ", isFinal=" + isFinal +
                ", isSynchronized=" + isSynchronized +
                ", returnType='" + returnType + '\'' +
                ", name='" + name + '\'' +
                ", paramList=" + paramList +
                ", content='" + content + '\'' +
                '}';
    }
}
