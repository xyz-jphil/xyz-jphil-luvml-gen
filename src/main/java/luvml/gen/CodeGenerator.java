package luvml.gen;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main code generator for HTML DSL.
 * Generates ContainerElement, VoidElement, HtmlElements, and HtmlAttributes 
 * into the sibling xyz-jphil-luvml project.
 */
public class CodeGenerator {
    
    private static final String TARGET_PROJECT = "../xyz-jphil-luvml";
    private static final String TARGET_JAVA_DIR = TARGET_PROJECT + "/src/main/java";
    private static final String TARGET_PACKAGE = "luvml";
    
    public static void main(String[] args) throws Exception {
        validateMavenExecution();
        
        var targetDir = resolveTargetDirectory();
        System.out.println("Generating code to: " + targetDir.toAbsolutePath());
        
        // Generate only DSL factory methods
        new HtmlElementsGenerator().generateTo(targetDir);
        
        System.out.println("Code generation completed successfully!");
    }
    
    /**
     * Validates that we're running from Maven context, not from JAR.
     */
    private static void validateMavenExecution() {
        var userDir = System.getProperty("user.dir");
        var classPath = System.getProperty("java.class.path");
        
        // Check if we're in the generator project directory
        if (!userDir.endsWith("xyz-jphil-luvml-gen")) {
            throw new IllegalStateException(
                "CodeGenerator must be run from xyz-jphil-luvml-gen directory. " +
                "Current directory: " + userDir
            );
        }
        
        // Check if we're running from Maven (not JAR)
        if (classPath.contains("xyz-jphil-luvml-gen") && classPath.contains(".jar")) {
            throw new IllegalStateException(
                "CodeGenerator must be run from Maven, not from JAR. " +
                "Use: mvn exec:java -Dexec.mainClass=luvml.gen.CodeGenerator"
            );
        }
        
        System.out.println("✓ Maven execution validated");
    }
    
    /**
     * Resolves and validates the target directory for code generation.
     */
    private static Path resolveTargetDirectory() throws Exception {
        var targetDir = Paths.get(TARGET_JAVA_DIR, TARGET_PACKAGE.split("\\."));
        
        // Create target directories if they don't exist
        Files.createDirectories(targetDir);
        
        // Validate that we can write to the target
        if (!Files.isWritable(targetDir)) {
            throw new IllegalStateException(
                "Cannot write to target directory: " + targetDir.toAbsolutePath()
            );
        }
        
        return targetDir;
    }
}