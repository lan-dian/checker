package com.landao.checker.core;

import com.landao.checker.core.checker.Checker;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.ControllerAdvice;


@ComponentScan(basePackages = "com.landao.checker.core.checker",
        includeFilters =@ComponentScan.Filter(classes = Checker.class))
public class CheckerScanner {

}
