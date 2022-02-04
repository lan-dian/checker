package com.landao.inspector;

import com.landao.inspector.core.InspectAspect;
import com.landao.inspector.utils.InspectorManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(InspectorProperties.class)
@Import({InspectAspect.class, InspectorManager.class})
public class InspectorAutoConfigure {



}
