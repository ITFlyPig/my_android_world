package com.easybug.plugint;

import java.util.List;

public class AopConfig {
    private List<String> needPackages; // 需要处理的包名
    private boolean isAop;//表示是否进行代码注入

    public List<String> getNeedPackages() {
        return needPackages;
    }

    public boolean isAop() {
        return isAop;
    }

    private  AopConfig(Builder builder) {
        this.isAop = builder.isAop;
        this.needPackages = builder.needPackages;
    }

    public static class Builder{
        private List<String> needPackages; // 需要处理的包名
        private boolean isAop;//表示是否进行代码注入

        public Builder setNeedPages(List<String> needPackages) {
            this.needPackages = needPackages;
            return this;
        }

        public Builder setAop(boolean aop) {
            isAop = aop;
            return this;
        }

        public AopConfig build() {
            return new AopConfig(this);
        }
    }
}
