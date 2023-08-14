package com.seed.multiUrlEnvironment.core;

public final class Environment {
    public static final Environment PRODUCTION = new Environment("https://ec2.aws.com", "https://s3.aws.com");

    public static final Environment STAGING =
            new Environment("https://staging.ec2.aws.com", "https://staging.s3.aws.com");

    private final String ec2;

    private final String s3;

    Environment(String ec2, String s3) {
        this.ec2 = ec2;
        this.s3 = s3;
    }

    public String getec2URL() {
        return this.ec2;
    }

    public String gets3URL() {
        return this.s3;
    }
}
