package com.gccloud.common.utils.cover;

public interface ICoverLife<S, T> {
    void after(Object source, T target);
}
