package com.landao.checker.core;

import com.landao.checker.core.checker.Checker;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.landao.checker.core.checker",
        includeFilters =@ComponentScan.Filter(classes = Checker.class))
public class CheckerScanner {

}
