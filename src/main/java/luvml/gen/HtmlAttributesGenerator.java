package luvml.gen;

import com.squareup.javapoet.*;
import luvml.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static luvml.AttributeType.*;

/**
 * Generates static factory methods for all HTML attributes as class A for compact usage.
 * 
 * DESIGN DECISIONS FOR CLEAN CODE GENERATION:
 * 
 * 1. **Compact Class Name**: 
 *    - Generates class 'A' instead of 'HtmlAttributes' for very compact syntax
 *    - Enables usage like A.id("test"), A.className("btn"), A.href("/link")
 *    - Reduces ambiguity when multiple similarly named functions exist
 * 
 * 2. **Static Imports for Reduced Boilerplate**: 
 *    - Uses static imports for all enum types (AttributeCategory.*, AttributeType.*, etc.)
 *    - This reduces verbose enum references from "AttributeCategory.GLOBAL" to just "GLOBAL"
 *    - Makes generated DSL code much cleaner and more readable
 *    - Pattern: import static luvml.AttributeCategory.*; import static luvml.AttributeType.*; etc.
 * 
 * 3. **Clean DSL Pattern**:
 *    - Generated methods follow pattern: id(value) -> Set.of(GLOBAL), STRING, UNIVERSAL
 *    - No verbose class prefixes in the actual method calls
 *    - Maintains readability while preserving all metadata
 * 
 * 4. **JavaPoet Code Generation Strategy**:
 *    - All metadata is centralized in HtmlAttributeData (no duplication)
 *    - Static imports reduce token count and visual noise
 *    - Generated code matches hand-written code quality standards
 * 
 * 5. **Attribute Type Handling**:
 *    - STRING attributes: simple string parameter
 *    - BOOLEAN attributes: boolean parameter (creates BooleanAttribute)
 *    - ENUM attributes: factory methods for each enum value (type-safe)
 *    - Other types: string parameter (extensible for future validation)
 * 
 * 6. **Reserved Keywords & Naming**:
 *    - Primary: classAttr(), forAttr() (clear, explicit)
 *    - Secondary: class_(), for_() (shorter alternatives)
 *    - Enum factory methods: target_blank(), dir_ltr() (type-safe, concise)
 * 
 * IMPORTANT: When modifying this generator, preserve the static import pattern
 * and clean enum reference style demonstrated in the generated A.java
 * 
 * TO RUN THIS GENERATOR:
 * 1. From xyz-jphil-luvml-gen directory: mvn compile exec:java -Dexec.mainClass="luvml.gen.HtmlAttributesGenerator"
 * 2. Or use the test script: cd test-scripts && jbang RunAttributeGenerator.java
 * 3. Ensure xyz-jphil-luvml project is compiled first for dependencies
 */
public class HtmlAttributesGenerator {
    
    // Reserved keyword mappings: attribute name -> [primary method, secondary method]
    private static final Map<String, String[]> RESERVED_KEYWORDS = Map.of(
        "class", new String[]{"classAttr", "class_"},
        "for", new String[]{"forAttr", "for_"}
    );
    
    // Track generated convenience methods for enum attributes
    private final java.util.Set<String> generatedConvenienceMethods = new java.util.HashSet<>();
    
    public static void main(String[] args) throws IOException {
        System.out.println("Generating HTML attributes DSL...");
        var generator = new HtmlAttributesGenerator();
        var targetDir = Paths.get("../xyz-jphil-luvml/src/main/java/luvml");
        generator.generateTo(targetDir);
        System.out.println("Generated A.java successfully!");
    }
    
    public void generateTo(Path targetDir) throws IOException {
        generateHtmlAttributes(targetDir);
    }
    
    /**
     * Generates A class with static factory methods for all HTML attributes.
     */
    private void generateHtmlAttributes(Path targetDir) throws IOException {
        var classBuilder = TypeSpec.classBuilder("A")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            
            // Private constructor
            .addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addStatement("// Utility class")
                .build())

            // Utility method for string attributes (lightweight)
            .addMethod(MethodSpec.methodBuilder("stringAttribute")
                .addModifiers(Modifier.STATIC)
                .returns(HtmlAttribute.class)
                .addParameter(String.class, "name")
                .addParameter(String.class, "value")
                .addStatement("return new $T(name, value)", HtmlAttribute.class)
                .build())

            // Utility method for scoped string attributes (for conflicting names)
            .addMethod(MethodSpec.methodBuilder("scopedStringAttribute")
                .addModifiers(Modifier.STATIC)
                .returns(ScopedHtmlAttribute.class)
                .addParameter(String.class, "name")
                .addParameter(String.class, "value")
                .addParameter(AttributeScope.class, "scope")
                .addStatement("return new $T(name, value, scope)", ScopedHtmlAttribute.class)
                .build())

            // Utility method for boolean attributes (lightweight)
            .addMethod(MethodSpec.methodBuilder("booleanAttribute")
                .addModifiers(Modifier.STATIC)
                .returns(BooleanAttribute.class)
                .addParameter(String.class, "name")
                .addParameter(boolean.class, "present")
                .addStatement("return new $T(name, present)", BooleanAttribute.class)
                .build())

            // Utility method for enum attributes (lightweight)
            .addMethod(MethodSpec.methodBuilder("enumAttribute")
                .addModifiers(Modifier.STATIC)
                .returns(EnumAttribute.class)
                .addParameter(String.class, "name")
                .addParameter(String.class, "value")
                .addStatement("return new $T(name, value)", EnumAttribute.class)
                .build())

            // Utility method for scoped enum attributes (for conflicting names)
            .addMethod(MethodSpec.methodBuilder("scopedEnumAttribute")
                .addModifiers(Modifier.STATIC)
                .returns(ScopedEnumAttribute.class)
                .addParameter(String.class, "name")
                .addParameter(String.class, "value")
                .addParameter(AttributeScope.class, "scope")
                .addStatement("return new $T(name, value, scope)", ScopedEnumAttribute.class)
                .build());
        
        // Track generated method signatures to prevent duplicates
        var generatedMethods = new java.util.HashSet<String>();
        
        // Clear convenience methods tracker for this generation run
        generatedConvenienceMethods.clear();
        
        // Generate static factory methods for all attributes
        for (var attribute : HtmlAttributeData.ALL_ATTRIBUTES.values()) {
            var attrName = attribute.attribute();
            var type = attribute.type();
            var scope = attribute.scope().toString();  // Clean enum name due to static import

            // Skip data-* template and event handlers for now (they need special handling)
            if (attrName.contains("*") || attrName.startsWith("on")) {
                continue;
            }

            // Handle reserved keywords with multiple method names
            String[] methodNames;
            if (RESERVED_KEYWORDS.containsKey(attrName)) {
                methodNames = RESERVED_KEYWORDS.get(attrName);
            } else {
                methodNames = new String[]{toMethodName(attrName)};
            }

            // Generate methods for each method name (primary and secondary)
            for (var methodName : methodNames) {
                if (type == BOOLEAN) {
                    // Boolean attribute - parameterless version (true)
                    var signature1 = methodName + "()";
                    if (!generatedMethods.contains(signature1)) {
                        generatedMethods.add(signature1);
                        classBuilder.addMethod(MethodSpec.methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                            .returns(BooleanAttribute.class)
                            .addStatement("return booleanAttribute($S, true)", attrName)
                            .build());
                    }

                    // Boolean attribute - with parameter
                    var signature2 = methodName + "(boolean)";
                    if (!generatedMethods.contains(signature2)) {
                        generatedMethods.add(signature2);
                        classBuilder.addMethod(MethodSpec.methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                            .returns(BooleanAttribute.class)
                            .addParameter(boolean.class, "value")
                            .addStatement("return booleanAttribute($S, value)", attrName)
                            .build());
                    }

                } else if (type == ENUM) {
                    // For enum attributes, generate factory methods for each enum value
                    generateEnumFactoryMethods(classBuilder, attrName, attribute.enumValues(),
                        attribute.scope());
                    
                    // Generate generic method for custom values
                    // For conflicting attributes (type, size), use scoped method names and ScopedHtmlAttribute
                    // For non-conflicting attributes, use simple method names and regular EnumAttribute
                    var conflictingAttributes = Set.of("type", "size");
                    
                    if (conflictingAttributes.contains(methodName)) {
                        var scopedMethodName = getScopedMethodName(methodName, attribute.scope());
                        var signature = scopedMethodName + "(String)";
                        if (!generatedMethods.contains(signature)) {
                            generatedMethods.add(signature);
                            classBuilder.addMethod(MethodSpec.methodBuilder(scopedMethodName)
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .returns(ScopedEnumAttribute.class)
                                .addParameter(String.class, "value")
                                .addStatement("return scopedEnumAttribute($S, value, $L)", attrName, scope)
                                .build());
                        }
                    } else {
                        var signature = methodName + "(String)";
                        if (!generatedMethods.contains(signature)) {
                            generatedMethods.add(signature);
                            classBuilder.addMethod(MethodSpec.methodBuilder(methodName)
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .returns(EnumAttribute.class)
                                .addParameter(String.class, "value")
                                .addStatement("return enumAttribute($S, value)", attrName)
                                .build());
                        }
                    }
                        
                } else {
                    // String or other types - generate ONLY ONCE per method name
                    var signature = methodName + "(String)";
                    if (!generatedMethods.contains(signature)) {
                        generatedMethods.add(signature);
                        classBuilder.addMethod(MethodSpec.methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                            .returns(HtmlAttribute.class)
                            .addParameter(String.class, "value")
                            .addStatement("return stringAttribute($S, value)", attrName)
                            .build());
                    }
                }
            }
        }
        
        // Add special methods for common patterns
        addSpecialMethods(classBuilder);
        
        JavaFile.builder("luvml", classBuilder.build())
            .addStaticImport(AttributeCategory.class, "*")
            .addStaticImport(AttributeType.class, "*") 
            .addStaticImport(AttributeScope.class, "*")
            .build()
            .writeTo(targetDir.getParent());
    }
    
    /**
     * Generate factory methods for each enum value of an enum attribute.
     * Example: for target attribute with values [_blank, _self], generates target_blank(), target_self()
     */
    private void generateEnumFactoryMethods(TypeSpec.Builder classBuilder, String attrName,
                                          Set<String> enumValues, AttributeScope attributeScope) {
        // Generate specific factory methods for each enum value
        for (var enumValue : enumValues) {
            var methodName = toValidJavaMethodName(attrName + "_" + enumValue);
            classBuilder.addMethod(MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(EnumAttribute.class)
                .addStatement("return enumAttribute($S, $S)", attrName, enumValue)
                .build());
        }

        // Generate convenience method ONLY for non-conflicting attributes
        // For conflicting attributes (type, size), use type(String) + type_enumValue() pattern
        var baseMethodName = toValidJavaMethodName(attrName);
        var conflictingAttributes = Set.of("type", "size");

        if (!conflictingAttributes.contains(baseMethodName)) {
            var defaultValue = getDefaultEnumValue(attrName, enumValues);
            var convenienceSignature = baseMethodName + "()";
            if (!generatedConvenienceMethods.contains(convenienceSignature)) {
                generatedConvenienceMethods.add(convenienceSignature);
                classBuilder.addMethod(MethodSpec.methodBuilder(baseMethodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(EnumAttribute.class)
                    .addStatement("return enumAttribute($S, $S)", attrName, defaultValue)
                    .build());
            }
        }
    }
    
    /**
     * Get sensible default value for enum attributes.
     */
    private String getDefaultEnumValue(String attrName, Set<String> enumValues) {
        // For tri-state ARIA attributes (true/false/undefined), default to "false" (safest)
        if (enumValues.contains("true") && enumValues.contains("false") && enumValues.contains("undefined")) {
            return "false";
        }
        
        // For boolean-like enums, prefer "false" or "no" over "true" or "yes" (safer default)
        if (enumValues.contains("false")) return "false";
        if (enumValues.contains("no")) return "no";
        if (enumValues.contains("off")) return "off";
        
        // For target attribute, default to "_self" (most common, safest)
        if (attrName.equals("target") && enumValues.contains("_self")) {
            return "_self";
        }
        
        // For dir attribute, default to "ltr" (most common)
        if (attrName.equals("dir") && enumValues.contains("ltr")) {
            return "ltr";
        }
        
        // For contenteditable, default to "false" (safer)
        if (attrName.equals("contenteditable") && enumValues.contains("false")) {
            return "false";
        }
        
        // For crossorigin, default to "anonymous" (most common)
        if (attrName.equals("crossorigin") && enumValues.contains("anonymous")) {
            return "anonymous";
        }
        
        // For preload, default to "metadata" (balanced default)
        if (attrName.equals("preload") && enumValues.contains("metadata")) {
            return "metadata";
        }
        
        // For method, default to "get" (most common)
        if (attrName.equals("method") && enumValues.contains("get")) {
            return "get";
        }
        
        // Default to first value in set (deterministic fallback)
        return enumValues.iterator().next();
    }
    
    /**
     * Add special convenience methods for common attribute patterns.
     */
    private void addSpecialMethods(TypeSpec.Builder classBuilder) {
        // data method for data-* attributes
        classBuilder.addMethod(MethodSpec.methodBuilder("data")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(HtmlAttribute.class)
            .addParameter(String.class, "name")
            .addParameter(String.class, "value")
            .addStatement("return stringAttribute(\"data-\" + name, value)")
            .build());

        // event method for event handlers
        classBuilder.addMethod(MethodSpec.methodBuilder("event")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(HtmlAttribute.class)
            .addParameter(String.class, "eventName")
            .addParameter(String.class, "handler")
            .addStatement("return stringAttribute(\"on\" + eventName, handler)")
            .build());


        // event method for event handlers
        classBuilder.addMethod(MethodSpec.methodBuilder("xmlns")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(HtmlAttribute.class)
            .addParameter(String.class, "value")
            .addStatement("return stringAttribute(\"xmlns\", value)")
            .build());
    }
    
    /**
     * Convert attribute name to valid Java method name.
     * Examples: "accept-charset" -> "acceptCharset", "http-equiv" -> "httpEquiv"
     */
    private String toMethodName(String attrName) {
        if (attrName.equals("class")) return "classAttr"; // Reserved keyword
        if (attrName.equals("for")) return "forAttr"; // Reserved keyword
        
        return toValidJavaMethodName(attrName);
    }
    
    /**
     * Convert any string to a valid Java method name.
     * Handles dashes, dots, spaces, and special characters.
     * Examples: 
     * - "aria-expanded" -> "ariaExpanded"
     * - "aria-expanded_true" -> "ariaExpandedTrue" 
     * - "accept-charset" -> "acceptCharset"
     * - "_blank" -> "blank"
     */
    private String toValidJavaMethodName(String input) {
        if (input == null || input.isEmpty()) {
            return "attr";
        }
        
        // Remove leading underscores and convert to camelCase
        var cleaned = input.replaceAll("^_+", ""); // Remove leading underscores
        // Split on various separators and special characters
        var parts = cleaned.split("[-._\\s/+:;,=]+");
        var result = new StringBuilder();
        
        for (int i = 0; i < parts.length; i++) {
            var part = parts[i];
            if (!part.isEmpty()) {
                if (i == 0) {
                    // First part: lowercase
                    result.append(part.toLowerCase());
                } else {
                    // Subsequent parts: capitalize first letter
                    result.append(Character.toUpperCase(part.charAt(0)));
                    if (part.length() > 1) {
                        result.append(part.substring(1).toLowerCase());
                    }
                }
            }
        }
        
        var methodName = result.toString();
        
        // Ensure it doesn't start with a number
        if (!methodName.isEmpty() && Character.isDigit(methodName.charAt(0))) {
            methodName = "attr" + Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
        }
        
        // Handle empty result
        if (methodName.isEmpty()) {
            methodName = "attr";
        }
        
        // Handle Java reserved keywords
        if (isJavaReservedKeyword(methodName)) {
            methodName = methodName + "Attr";
        }
        
        return methodName;
    }
    
    /**
     * Generate scoped method name to avoid conflicts for attributes that exist in multiple contexts.
     * Examples: type -> typeForm, typeLink, typeDeprecated
     */
    private String getScopedMethodName(String baseName, AttributeScope scope) {
        // For common conflicting attributes, add scope suffix
        var conflictingAttributes = Set.of("type", "size");
        
        if (conflictingAttributes.contains(baseName)) {
            return switch (scope) {
                case FORM_ELEMENTS -> baseName + "Form";
                case LINK_ELEMENTS -> baseName + "Link";
                case MEDIA_ELEMENTS -> baseName + "Media";
                case TABLE_ELEMENTS -> baseName + "Table";
                case SPECIFIC_ELEMENTS -> baseName + "Specific";
                case METADATA_ELEMENTS -> baseName + "Meta";
                case INTERACTIVE_ELEMENTS -> baseName + "Interactive";
                case UNIVERSAL -> baseName + "Global";
                default -> baseName + "Attr";
            };
        }
        
        // For non-conflicting attributes, return base name
        return baseName;
    }
    
    /**
     * Check if a string is a Java reserved keyword.
     */
    private boolean isJavaReservedKeyword(String word) {
        var keywords = Set.of(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", 
            "class", "const", "continue", "default", "do", "double", "else", "enum", 
            "extends", "final", "finally", "float", "for", "goto", "if", "implements", 
            "import", "instanceof", "int", "interface", "long", "native", "new", "null", 
            "package", "private", "protected", "public", "return", "short", "static", 
            "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", 
            "transient", "try", "void", "volatile", "while", "true", "false", "var"
        );
        return keywords.contains(word.toLowerCase());
    }
}