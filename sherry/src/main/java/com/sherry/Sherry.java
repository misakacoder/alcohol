package com.sherry;

import com.sherry.config.GeneratorConfig;
import com.sherry.enums.ORM;
import com.sherry.util.Generator;

public class Sherry {
    public static void main(String[] args) {
        GeneratorConfig config = new GeneratorConfig("generator.properties");
        new Generator(config).generate(ORM.MYBATIS);
    }
}