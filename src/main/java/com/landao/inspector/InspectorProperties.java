package com.landao.inspector;

import com.landao.inspector.model.enums.InspectMode;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "inspector")
public class InspectorProperties {

    private InspectMode inspectMode=InspectMode.InspectAll;

    private Interceptor interceptor=new Interceptor();

    public InspectorProperties() {
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    public InspectMode getInspectMode() {
        return inspectMode;
    }

    public void setInspectMode(InspectMode inspectMode) {
        this.inspectMode = inspectMode;
    }

    public static class Interceptor{

        /**
         * 拦截器执行的顺序
         */
        private Integer order=0;

        /**
         * 拦截的路径
         */
        private String[] includedPathPatterns ={"/**"};

        /**
         * 不拦截的路径
         */
        private String[] excludePathPatterns;

        public Interceptor() {
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public String[] getExcludePathPatterns() {
            return excludePathPatterns;
        }

        public void setExcludePathPatterns(String[] excludePathPatterns) {
            this.excludePathPatterns = excludePathPatterns;
        }

        public String[] getIncludedPathPatterns() {
            return includedPathPatterns;
        }

        public void setIncludedPathPatterns(String[] includedPathPatterns) {
            this.includedPathPatterns = includedPathPatterns;
        }
    }


}
