package com.fun.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.fun.config.PropertyUtils;
import com.fun.utils.RString;

public class DubboBase {

    private ApplicationConfig applicationConfig = new ApplicationConfig();

    private RegistryConfig registryConfig = new RegistryConfig();

    private String version;

    private String registryAddress;

    ReferenceConfig<GenericService> referenceConfig;

    ReferenceConfigCache configCache;

    private DubboBase(String propertyName) {
        PropertyUtils.Property properties = PropertyUtils.getProperties(propertyName);
        this.registryAddress = properties.getProperty("address");
        this.version = properties.getProperty("version");
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(registryAddress);
        applicationConfig.setName(properties.getProperty("name"));
    }

    /**
     * ReferenceConfig实例很重，封装了与注册中心的连接以及与提供者的连接，
     * 需要缓存，否则重复生成ReferenceConfig可能造成性能问题并且会有内存和连接泄漏。
     * API方式编程时，容易忽略此问题。
     * 这里使用dubbo内置的简单缓存工具类进行缓存
     *
     * @param interfaceClass
     * @return
     */
    public GenericService getGenericService(String interfaceClass) {
        if (referenceConfig == null) {
            referenceConfig = new ReferenceConfig<GenericService>();
            referenceConfig.setApplication(applicationConfig);
            referenceConfig.setRegistry(registryConfig);
            referenceConfig.setVersion(version);
            referenceConfig.setInterface(interfaceClass);
            referenceConfig.setGeneric(true);
        }
        return ReferenceConfigCache.getCache(RString.getChinese(5)).get(referenceConfig);
    }

    /**
     * 释放资源
     */
    public void over() {
        configCache.destroy(referenceConfig);
    }


}
