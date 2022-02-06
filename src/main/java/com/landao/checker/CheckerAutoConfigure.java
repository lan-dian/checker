package com.landao.checker;

import com.landao.checker.core.CheckAspect;
import com.landao.checker.utils.CheckerManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(CheckerProperties.class)
@Import({CheckAspect.class, CheckerManager.class})
public class CheckerAutoConfigure {



}
