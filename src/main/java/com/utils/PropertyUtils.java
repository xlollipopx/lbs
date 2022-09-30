package com.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

@Slf4j
public class PropertyUtils {

    public static Properties loadPropertyFile(String fileName) {
        Properties props = new Properties();
        try {
            props.load(new ClassPathResource(fileName).getInputStream());
        } catch (Exception e) {
            log.error("Can't load file: {}. Exception: {}", fileName, e);
        }
        return props;
    }
}