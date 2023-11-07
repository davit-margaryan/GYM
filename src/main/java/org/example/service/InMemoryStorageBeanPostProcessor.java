package org.example.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class InMemoryStorageBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof InMemoryStorage inMemoryStorage) {
            inMemoryStorage.initializeStorage();
        }
        return bean;
    }
}
