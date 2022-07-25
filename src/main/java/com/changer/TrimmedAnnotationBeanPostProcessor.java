package com.changer;

import com.changer.annotation.Trimmed;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

@Log4j2
public class TrimmedAnnotationBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanType = bean.getClass();
        if (beanType.isAnnotationPresent(Trimmed.class)) {
            log.debug("Creating String Trimming proxy");
            return createProxy(beanType);
        }
        return bean;
    }

    private static Object createProxy(Class<?> beanType) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(beanType);
        MethodInterceptor methodInterceptor = createInterceptor();
        enhancer.setCallback(methodInterceptor);
        return enhancer.create();
    }

    private static MethodInterceptor createInterceptor() {
        return (object, method, args, methodProxy) -> {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof String arg) {
                    args[i] = arg.trim();
                }
            }
            Object result = methodProxy.invokeSuper(object, args);
            if (result instanceof String str) {
                result = str.trim();
            }
            return result;
        };
    }
}
