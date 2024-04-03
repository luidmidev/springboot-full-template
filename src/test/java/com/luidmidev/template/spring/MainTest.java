package com.luidmidev.template.spring;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    private static final String CURRENT_DIR = "template-spring";

    @Test
    void whenUsingSystemProperties_thenReturnCurrentDirectory() {
        String userDirectory = System.getProperty("user.dir");
        assertEquals(CURRENT_DIR, userDirectory);
    }

    @Test
    void whenUsingJavaIoFile_thenReturnCurrentDirectory() {
        String userDirectory = new File("").getAbsolutePath();
        assertEquals(CURRENT_DIR, userDirectory);

    }

}
