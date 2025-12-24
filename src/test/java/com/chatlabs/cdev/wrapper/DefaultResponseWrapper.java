package com.chatlabs.cdev.wrapper;

/**
 *
 * @author DD
 */
public class DefaultResponseWrapper implements ResponseWrapper {
    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public Object unwrap(Object result) {
        return null;
    }

    @Override
    public Object wrap(Object data) {
        return null;
    }
}
