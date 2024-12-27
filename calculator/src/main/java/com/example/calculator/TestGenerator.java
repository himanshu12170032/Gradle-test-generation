package com.example.calculator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class TestGenerator {
    private static final String BASE_PACKAGE = "com.example.calculator.Service";
    private static final String TEST_BASE_PACKAGE = "com.example.calculator";
    private static final String OUTPUT_DIRECTORY = "src/test/java/com/example/calculator";

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        scanAndGenerateTests(BASE_PACKAGE);
    }

    private static void scanAndGenerateTests(String packageName) throws ClassNotFoundException, IOException {
        String packagePath = packageName.replace('.', '/');
        File directory = new File("build/classes/java/main", packagePath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".class"));
            System.out.println("Files found: " + Arrays.toString(files));

            for (File file : files) {
                String className = packageName + "." + file.getName().replace(".class", "");
                System.out.println("Processing class: " + className);
                generateTestClass(className);
            }
        } else {
            System.out.println("Package not found: " + packageName);
        }
    }

    private static void generateTestClass(String className) throws ClassNotFoundException, IOException {
        Class<?> clazz = Class.forName(className);
        if (clazz.getPackage().getName().equals(TEST_BASE_PACKAGE)) {
            return;
        }

        String testClassName = clazz.getSimpleName() + "Test";
        File testClassFile = new File(OUTPUT_DIRECTORY, testClassName + ".java");

        if (testClassFile.exists()) {
            System.out.println("Test file already exists for " + clazz.getSimpleName());
            return;
        }

        System.out.println("Generating test file for " + clazz.getSimpleName());

        StringBuilder testClassContent = new StringBuilder();
        testClassContent.append("package ").append(TEST_BASE_PACKAGE).append(";\n\n");
        testClassContent.append("import org.junit.jupiter.api.Test;\n\n");
        testClassContent.append("public class ").append(testClassName).append(" {\n\n");

        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                String methodName = method.getName();
                testClassContent.append("    @Test\n")
                        .append("    public void test").append(capitalize(methodName)).append("() {\n")
                        .append("        // TODO: Implement test for ").append(methodName).append("\n")
                        .append("    }\n\n");
            }
        }

        testClassContent.append("}\n");
        testClassFile.getParentFile().mkdirs();
        testClassFile.createNewFile();
        java.nio.file.Files.write(testClassFile.toPath(), testClassContent.toString().getBytes());

        System.out.println("Test file generated: " + testClassFile.getPath());
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
