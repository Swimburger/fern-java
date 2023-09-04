package com.seed.trace.core;

public final class Environment {
    public static final Environment PROD = new Environment("https://api.trace.come");

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
