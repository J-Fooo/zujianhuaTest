package com.jf.compiler.config;

public interface ProcessorConfig {
    String ACTIVITY_PACKAGE = "android.app.Activity";
    String OPTIONS = "moduleName";
    String PATH_METHOD_NAME = "getPathMap";
    String GROUP_METHOD_NAME = "getGroupMap";
    String PARAMMETER_METHOD_NAME = "getParameter";
    String PATH_FILE_NAME = "JRouter$$Path$$";
    String GROUP_FILE_NAME = "JRouter$$Group$$";

    String JROUTER_API_PATH = "com.ddd.annotation_api.jrouter_api.JRouterPath";
    String JROUTER_API_GROUP = "com.ddd.annotation_api.jrouter_api.JRouterGroup";
    String PARAMETER_API = "com.ddd.annotation_api.jrouter_api.ParameterGet";

}
