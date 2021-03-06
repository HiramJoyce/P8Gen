package com.tools.gen.utils;

import com.tools.gen.Main;
import com.tools.gen.entity.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class P8TradeInfoReader {

    public static P8TradeInfo read(String filePath) throws Exception {
        // check file format and return file lines
        List<String> lines = checkFile(filePath);

        // deal trade code and name
        P8TradeInfo tradeInfo = new P8TradeInfo();

        // get trade basic info
        List<String> basicInfoLines = getBasicInfoLines(lines);
        // deal trade basic info
        tradeInfo.setTradeCd(basicInfoLines.get(0));
        tradeInfo.setTradeName(basicInfoLines.get(1));

        // get trade inVo lines
        List<String> inVoLines = getInVoLines(lines);
        // deal trade inVo
        genAndSetInVo(tradeInfo, inVoLines);

        // get trade outVo lines
        List<String> outVoLines = getOutVoLines(lines);
        // deal trade outVo
        genAndSetOutVo(tradeInfo, outVoLines);

        // deal service
        genAndSetService(tradeInfo);

        // deal serviceImpl
        genAndSetServiceImpl(tradeInfo);
        return tradeInfo;
    }

    public static List<String> checkFile(String filePath) throws Exception {
        List<String> lines = new ArrayList<>();
        // 第一行联机交易码
        BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
        String line = reader.readLine();
        if (StringUtils.isBlank(line)) {
            throw new UnknownFormatFlagsException("第一行应填写联机交易码");
        }
        lines.add(line);

        // 第二行联机交易中文名
        line = reader.readLine();
        if (StringUtils.isBlank(line)) {
            Logger.warn("trade name is empty.");
        }
        lines.add(line);

        // 三个短横线分隔
        line = reader.readLine();
        if (!StringUtils.isEqual("---", line)) {
            throw new UnknownFormatFlagsException("第三行应填写三个短横线分隔");
        }
        lines.add(line);

        // 请求报文
        while (!StringUtils.isBlank(line = reader.readLine()) && !StringUtils.isEqual("---", line)) {
            lines.add(line);
        }

        // 三个短横线分隔
        if (!StringUtils.isEqual("---", line)) {
            throw new UnknownFormatFlagsException("应填写三个短横线分隔请求报文和响应报文");
        }
        lines.add(line);

        // 响应报文
        while (!StringUtils.isBlank(line = reader.readLine()) && !StringUtils.isEqual("---", line)) {
            lines.add(line);
        }

        Logger.info("trade check pass, file has " + lines.size() + " lines.");
        return lines;
    }

    public static List<String> getBasicInfoLines(List<String> lines) {
        List<String> basicInfoLines = new ArrayList<>();

        basicInfoLines.add(lines.get(0));
        basicInfoLines.add(lines.get(1));

        Logger.info("basic lines size - " + basicInfoLines.size());

        return basicInfoLines;
    }

    public static List<String> getInVoLines(List<String> lines) {
        List<String> inVoLines = new ArrayList<>();

        for (int i = lines.indexOf("---") + 1; i < lines.lastIndexOf("---"); i++) {
            inVoLines.add(lines.get(i));
        }

        Logger.info("inVo  lines size - " + inVoLines.size());

        return inVoLines;
    }

    public static List<String> getOutVoLines(List<String> lines) {
        List<String> outVoLines = new ArrayList<>();

        for (int i = lines.lastIndexOf("---") + 1; i < lines.size(); i++) {
            outVoLines.add(lines.get(i));
        }

        Logger.info("outVo lines size - " + outVoLines.size());

        return outVoLines;
    }

    private static void genAndSetInVo(P8TradeInfo tradeInfo, List<String> lines) {
        Clazz inVo = new Clazz();
        inVo.setPkg(Main.basePackage + ".business.vo");
        Set<String> importSet = new TreeSet<>();
        importSet.add("com.ccb.openframework.datatransform.message.TxRequestMsgBodyEntity");
        importSet.add("java.io.Serializable");

        List<List<String>> fieldLinesList = getFieldLinesListAndImportSet(lines, importSet);

        inVo.setImportSet(importSet);
        inVo.setVisibility("public");
        inVo.setName(tradeInfo.getTradeCd() + "InVo");
        Set<String> implementSet = new TreeSet<>();
        implementSet.add("Serializable");
        implementSet.add("TxRequestMsgBodyEntity");
        inVo.setImplementSet(implementSet);

        List<Field> staticFieldLists = new ArrayList<>();
        staticFieldLists.add(getSerialVersionUID());
        inVo.setStaticFieldList(staticFieldLists);

        List<Field> fieldLists = new ArrayList<>();
        List<Method> methodList = new ArrayList<>();
        List<Clazz> grpList = new ArrayList<>();
        tradeInfo.setGrpList(grpList);

        dealFieldListAndMethodList(tradeInfo, fieldLists, methodList, fieldLinesList);

        inVo.setFieldList(fieldLists);
        // TODO Override toString
        inVo.setMethodList(methodList);

        tradeInfo.setInVo(inVo);
    }

    private static void genAndSetOutVo(P8TradeInfo tradeInfo, List<String> lines) {
        Clazz outVo = new Clazz();
        outVo.setPkg(Main.basePackage + ".business.vo");
        Set<String> importSet = new TreeSet<>();
        importSet.add("com.ccb.openframework.datatransform.message.TxResponseMsgBodyEntity");
        importSet.add("java.io.Serializable");

        List<List<String>> fieldLinesList = getFieldLinesListAndImportSet(lines, importSet);

        outVo.setImportSet(importSet);
        outVo.setVisibility("public");
        outVo.setName(tradeInfo.getTradeCd() + "OutVo");
        Set<String> implementSet = new TreeSet<>();
        implementSet.add("Serializable");
        implementSet.add("TxResponseMsgBodyEntity");
        outVo.setImplementSet(implementSet);

        List<Field> staticFieldLists = new ArrayList<>();
        staticFieldLists.add(getSerialVersionUID());
        outVo.setStaticFieldList(staticFieldLists);

        List<Field> fieldLists = new ArrayList<>();
        List<Method> methodList = new ArrayList<>();

        dealFieldListAndMethodList(tradeInfo, fieldLists, methodList, fieldLinesList);

        outVo.setFieldList(fieldLists);
        // TODO Override toString
        outVo.setMethodList(methodList);

        tradeInfo.setOutVo(outVo);
    }

    public static List<List<String>> getFieldLinesListAndImportSet(List<String> lines, Set<String> importSet) {
        List<List<String>> fieldLinesList = new ArrayList<>();
        List<String> fieldsLine = null;
        if (lines.size() > 0) {
            for (String line : lines) {
                String[] ss = line.split("\t");
                if ("Group".equals(ss[4])) {
                    if (fieldsLine != null) {
                        fieldLinesList.add(fieldsLine);
                    }
                    if ("*N".equals(ss[3]) || "".equals(ss[3])) {
                        if (importSet != null) importSet.add("java.util.List");
                    }
                    fieldsLine = new ArrayList<>();
                    fieldsLine.add(line);
                } else if (ss[0].startsWith("..")) {
                    if (fieldsLine != null) {
                        fieldsLine.add(line);
                    }
                } else {
                    if (fieldsLine != null) {
                        fieldLinesList.add(fieldsLine);
                    }
                    fieldsLine = new ArrayList<>();
                    fieldsLine.add(line);
                    if ("N".equals(ss[4])) {
                        if (importSet != null) importSet.add("java.math.BigDecimal");
                    }
                    fieldLinesList.add(fieldsLine);
                    fieldsLine = null;
                }
            }
            if (fieldsLine != null) {
                fieldLinesList.add(fieldsLine);
            }
        }
        return fieldLinesList;
    }

    private static Field getSerialVersionUID() {
        return new Field()
                .setVisibility("private").setStatic(true).setFinal(true)
                .setType("long").setName("serialVersionUID").setValue("1L");
    }

    private static void dealFieldListAndMethodList(P8TradeInfo tradeInfo, List<Field> fieldLists, List<Method> methodList, List<List<String>> fieldLinesList) {
        // deal field lines list
        if (fieldLinesList.size() > 0) {
            for (List<String> fieldLines : fieldLinesList) {
                String[] ss = fieldLines.get(0).split("\t");

                // check if exist
                if (fieldLists.size() > 0) {
                    for (Field forField : fieldLists) {

                        String fieldName = CamelCaseUtils.toSmallCamelCase(ss[0]);
                        if (fieldLines.size() != 1) fieldName = fieldName.substring(0, fieldName.length() - 3);

                        if (forField.getName().equals(fieldName)) {
                            throw new RuntimeException("Duplicated field [" + ss[0] + "] in same vo, Please check the trade info.");
                        }
                    }
                }

                Field field;
                // class's String/BigDecimal field
                if (!"Group".equals(ss[4])) {
                    field = new Field().setNote(StringUtils.isBlank(ss[1]) ? null : NoteUtils.singleLine(ss[1], 1)).setVisibility("private").setType("C".equals(ss[4]) ? "String" : "BigDecimal").setName(CamelCaseUtils.toSmallCamelCase(ss[0]));
                }
                // class's class field
                else if ("*N".equals(ss[3]) || "".equals(ss[3])) {
                    String fieldClassName = CamelCaseUtils.toBigCamelCase(ss[0].substring(0, ss[0].length() - 4));
                    field = new Field().setNote(StringUtils.isBlank(ss[1]) ? null : NoteUtils.singleLine(ss[1], 1)).setVisibility("private").setType("List<" + fieldClassName + ">").setName(fieldClassName.substring(0, 1).toLowerCase() + fieldClassName.substring(1) + "List");
                    genGrpList(tradeInfo, field, fieldLines);
                } else {
                    String fieldClassName = ss[0].endsWith("_GRP") || ss[0].endsWith("_Grp") ? CamelCaseUtils.toBigCamelCase(ss[0].substring(0, ss[0].length() - 4)) : CamelCaseUtils.toBigCamelCase(ss[0]);
                    field = new Field().setNote(StringUtils.isBlank(ss[1]) ? null : NoteUtils.singleLine(ss[1], 1)).setVisibility("private").setType(fieldClassName).setName(fieldClassName.substring(0, 1).toLowerCase() + fieldClassName.substring(1));
                    genGrpList(tradeInfo, field, fieldLines);
                }
                fieldLists.add(field);
                methodList.addAll(genGetterAndSetter(field));
            }
        }
    }

    private static void genGrpList(P8TradeInfo tradeInfo, Field field, List<String> fieldLines) {
        String targetClazzName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        if (field.getName().endsWith("List")) {
            targetClazzName = targetClazzName.substring(0, targetClazzName.length() - 4);
        }
        Clazz clazz = null;
        if (tradeInfo.getGrpList().size() > 0) {
            for (Clazz forClazz : tradeInfo.getGrpList()) {
                if (forClazz.getName().equals(targetClazzName)) {
                    clazz = forClazz;
                }
            }
        }

        if (clazz != null) {

            Set<String> importSet = clazz.getImportSet();

            List<Field> fieldLists = clazz.getFieldList();
            List<Method> methodList = clazz.getMethodList();

            Field innerField;
            A : for (int i = 1; i < fieldLines.size(); i++) {
                String fieldLine = fieldLines.get(i);
                String[] ss = fieldLine.split("\t");

                if (fieldLists.size() > 0) {
                    for (Field forField : fieldLists) {
                        if (forField.getName().equals(CamelCaseUtils.toSmallCamelCase(ss[0]))) {
                            if (!forField.getType().equals("C".equals(ss[4]) ? "String" : "BigDecimal")) {
                                Logger.warn("Field [" + forField.getName() + "] type diff [" + forField.getType() + ", " + ("C".equals(ss[4]) ? "String" : "BigDecimal") + "], will use the one who was first defined.");
                            }
                            continue A;
                        }
                    }
                }

                if ("N".equals(ss[4])) {
                    importSet.add("java.math.BigDecimal");
                }
                innerField = new Field().setNote(StringUtils.isBlank(ss[1]) ? null : NoteUtils.singleLine(ss[1], 1)).setVisibility("private").setType("C".equals(ss[4]) ? "String" : "BigDecimal").setName(CamelCaseUtils.toSmallCamelCase(ss[0]));
                fieldLists.add(innerField);
                methodList.addAll(genGetterAndSetter(innerField));
            }
        } else {
            clazz = new Clazz();
            clazz.setPkg(Main.basePackage + ".business.vo");
            Set<String> importSet = new TreeSet<>();

            clazz.setVisibility("public");
            clazz.setName(targetClazzName);

            List<Field> fieldLists = new ArrayList<>();
            List<Method> methodList = new ArrayList<>();

            Field innerField;
            for (int i = 1; i < fieldLines.size(); i++) {
                String fieldLine = fieldLines.get(i);
                String[] ss = fieldLine.split("\t");
                if ("N".equals(ss[4])) {
                    importSet.add("java.math.BigDecimal");
                }
                innerField = new Field().setNote(StringUtils.isBlank(ss[1]) ? null : NoteUtils.singleLine(ss[1], 1)).setVisibility("private").setType("C".equals(ss[4]) ? "String" : "BigDecimal").setName(CamelCaseUtils.toSmallCamelCase(ss[0]));
                fieldLists.add(innerField);
                methodList.addAll(genGetterAndSetter(innerField));
            }

            clazz.setImportSet(importSet);

            clazz.setFieldList(fieldLists);
            // TODO Override toString

            clazz.setMethodList(methodList);
            tradeInfo.getGrpList().add(clazz);
        }
    }

    private static List<Method> genGetterAndSetter(Field field) {
        List<Method> methodList = new ArrayList<>();

        // gen setter
        Method setter = new Method();
        setter.setVisibility("public");
        setter.setReturnType("void");
        setter.setName("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
        List<Param> paramList = new ArrayList<>();
        Param param = new Param();
        param.setType(field.getType());
        param.setName(field.getName());
        paramList.add(param);
        setter.setParamList(paramList);
        setter.setContent(Indents.method("this." + field.getName() + " = " + field.getName() + ";", 1));

        // gen getter
        Method getter = new Method();
        getter.setVisibility("public");
        getter.setReturnType(field.getType());
        getter.setName("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
        getter.setContent(Indents.method("return " + field.getName() + ";", 1));

        methodList.add(setter);
        methodList.add(getter);

        return methodList;
    }

    private static void genAndSetService(P8TradeInfo tradeInfo) {
        Interface service = new Interface();
        service.setPkg(Main.basePackage + ".business.service");

        Set<String> importSet = new TreeSet<>();
        importSet.add("com.ccb.openframework.datatransform.message.TxRequestMsg");
        importSet.add(Main.basePackage + ".business.vo." + tradeInfo.getTradeCd() + "OutVo");
        service.setImportSet(importSet);

        service.setVisibility("public");
        service.setName(tradeInfo.getTradeCd() + "Service");

        List<Method> methodList = new ArrayList<>();
        Method method = new Method();
        method.setReturnType(tradeInfo.getTradeCd() + "OutVo");
        method.setName("doService");
        List<Param> paramList = new ArrayList<>();
        Param param = new Param();
        param.setType("TxRequestMsg");
        param.setName("txRequestMsg");
        paramList.add(param);
        method.setParamList(paramList);
        methodList.add(method);
        service.setEmptyMethodList(methodList);

        tradeInfo.setService(service);
    }

    private static void genAndSetServiceImpl(P8TradeInfo tradeInfo) {
        Clazz serviceImpl = new Clazz();
        serviceImpl.setPkg(Main.basePackage + ".business.service.impl");

        Set<String> importSet = new TreeSet<>();
        importSet.add("org.springframework.stereotype.Service");
        importSet.add(Main.basePackage + ".business.service." + tradeInfo.getTradeCd() + "Service");
        importSet.add(Main.basePackage + ".business.vo." + tradeInfo.getTradeCd() + "InVo");
        importSet.add(Main.basePackage + ".business.vo." + tradeInfo.getTradeCd() + "OutVo");
        importSet.add("com.ccb.openframework.datatransform.message.TxRequestMsg");
        importSet.add("com.ccb.openframework.log.Log");
        importSet.add("com.ccb.openframework.log.LogFactory");
        serviceImpl.setImportSet(importSet);

        serviceImpl.setNote(NoteUtils.multiLine(Collections.singletonList(tradeInfo.getTradeName()), 0));

        List<Annotation> annotationList = new ArrayList<>();
        Annotation annotation = new Annotation();
        annotation.setName("Service");
        annotation.setDefaultValue(tradeInfo.getTradeCd().substring(0, 1).toLowerCase() + tradeInfo.getTradeCd().substring(1) + "ServiceImpl");
        annotationList.add(annotation);
        serviceImpl.setAnnotationList(annotationList);

        serviceImpl.setVisibility("public");
        serviceImpl.setName(tradeInfo.getTradeCd() + "ServiceImpl");

        Set<String> implementSet = new TreeSet<>();
        implementSet.add(tradeInfo.getTradeCd() + "Service");
        serviceImpl.setImplementSet(implementSet);

        List<Field> fieldList = new ArrayList<>();
        Field field = new Field();
        field.setVisibility("private");
        field.setStatic(true);
        field.setFinal(true);
        field.setType("Log");
        field.setName("logger");
        field.setValue("LogFactory.getLog(" + tradeInfo.getTradeCd() + "ServiceImpl.class)");
        fieldList.add(field);
        serviceImpl.setStaticFieldList(fieldList);

        List<Method> methodList = new ArrayList<>();
        Method method = new Method();
        List<Annotation> methodAnnotationList = new ArrayList<>();
        Annotation methodAnnotation = new Annotation();
        methodAnnotation.setName("Override");
        methodAnnotationList.add(methodAnnotation);
        method.setAnnotationList(methodAnnotationList);
        method.setVisibility("public");
        method.setReturnType(tradeInfo.getTradeCd() + "OutVo");
        method.setName("doService");
        List<Param> paramList = new ArrayList<>();
        Param param = new Param();
        param.setType("TxRequestMsg");
        param.setName("txRequestMsg");
        paramList.add(param);
        method.setParamList(paramList);
        method.setContent(
                Indents.method("logger.info(\"--->>> call " + tradeInfo.getTradeCd() + "ServiceImpl.doService()\");", 1) +
                Indents.method(tradeInfo.getTradeCd() + "InVo inVo = (" + tradeInfo.getTradeCd() + "InVo) txRequestMsg.getMsgBody().getMsgBodyEntity();", 1) +
                Indents.method(tradeInfo.getTradeCd() + "OutVo outVo = new " + tradeInfo.getTradeCd() + "OutVo();", 1) +
                Indents.method("// TODO your business code here", 1) +
                Indents.method("", 1) +
                Indents.method("return outVo;", 1)
        );
        methodList.add(method);
        serviceImpl.setMethodList(methodList);

        tradeInfo.setServiceImpl(serviceImpl);
    }
}
