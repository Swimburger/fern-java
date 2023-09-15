/**
 * This file was auto-generated by Fern from our API Definition.
 */
package com.seed.singleUrlEnvironmentDefault.core;

public final class Environment {
    public static final Environment PRODUCTION = new Environment("https://production.com/api");

    public static final Environment STAGING = new Environment("https://staging.com/api");

    private final String url;

    private Environment(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public static Environment custom(String url) {
        return new Environment(url);
    }
}
