package org.example.sortapp.service;

@FunctionalInterface
public interface DelayProvider {
    long getDelayMillis();
}
