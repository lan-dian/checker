package com.landao.inspector.core;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.landao.inspector.core.inspector",
        includeFilters =@ComponentScan.Filter(classes = Inspector.class))
public class InspectorScanner {

}
