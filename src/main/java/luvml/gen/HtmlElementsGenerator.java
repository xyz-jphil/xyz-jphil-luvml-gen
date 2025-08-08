package luvml.gen;

import com.squareup.javapoet.*;
import luvx.Frag_I;
import luvx.Attr_I;
import luvml.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import static luvml.ElementType.*;

/**
 * Generates static factory methods for all HTML elements as class E for compact usage.
 * 
 * DESIGN DECISIONS FOR CLEAN CODE GENERATION:
 * 
 * 1. **Compact Class Name**: 
 *    - Generates class 'E' instead of 'HtmlElements' for very compact syntax
 *    - Enables usage like E.a(), E.div(), E.span() instead of HtmlElements.a()
 *    - Reduces ambiguity when multiple similarly named functions exist
 * 
 * 2. **Static Imports for Reduced Boilerplate**: 
 *    - Uses static imports for all enum types (ContentCategory.*, DisplayType.*, etc.)
 *    - This reduces verbose enum references from "ContentCategory.INTERACTIVE" to just "INTERACTIVE"
 *    - Makes generated DSL code much cleaner and more readable
 *    - Pattern: import static luvml.ContentCategory.*; import static luvml.DisplayType.*; etc.
 * 
 * 3. **Clean DSL Pattern**:
 *    - Generated methods follow pattern: div(fragments) -> Set.of(FLOW), BLOCK, CONTAINER, Set.of(FLOW_CTX)
 *    - No verbose class prefixes in the actual method calls
 *    - Maintains readability while preserving all metadata
 * 
 * 4. **JavaPoet Code Generation Strategy**:
 *    - formatSetLiteral() helper creates concise Set.of(ENUM1, ENUM2) syntax
 *    - Static imports reduce token count and visual noise
 *    - Generated code matches hand-written code quality standards
 * 
 * IMPORTANT: When modifying this generator, preserve the static import pattern
 * and clean enum reference style demonstrated in the generated E.java
 */
public class HtmlElementsGenerator {
    
    public void generateTo(Path targetDir) throws IOException {
        generateHtmlElements(targetDir);
    }
    
    // ======================== ELEMENT CLASSIFICATION ========================
    
    /**
     * Phrasing elements that flow inline with text content.
     * AI systems read these as continuous content without structural boundaries.
     */
    private static final Set<String> PHRASING_ELEMENTS = Set.of(
        "a", "abbr", "b", "bdi", "bdo", "br", "button", "cite", "code", 
        "data", "del", "dfn", "em", "i", "ins", "kbd", "label", "mark", 
        "meter", "noscript", "output", "progress", "q", "ruby", "s", 
        "samp", "small", "span", "strong", "sub", "sup", "time", "u", 
        "var", "wbr"
    );
    
    /**
     * Void elements that create structural boundaries (block-level).
     * AI systems see clear structural separation for better document comprehension.
     */
    private static final Set<String> BLOCK_VOID_ELEMENTS = Set.of(
        "hr", "meta", "link", "base"
    );
    
    private boolean isPhrasingElement(String elementName) {
        return PHRASING_ELEMENTS.contains(elementName);
    }
    
    private boolean isBlockVoidElement(String elementName) {
        return BLOCK_VOID_ELEMENTS.contains(elementName);
    }
    
    // ======================== GENERATION METHODS ========================
    
    private void generateBlockContainerMethods(TypeSpec.Builder classBuilder, String elementName, ParameterizedTypeName fragInterface) {
        // Fragments overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("luvml", "BlockContainerElement"))
            .addParameter(ArrayTypeName.of(fragInterface), "fragments")
            .varargs()
            .addStatement("return blockContainer($S, fragments)", elementName)
            .build());
        
        // Iterable<Frag_I> overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("luvml", "BlockContainerElement"))
            .addParameter(ParameterizedTypeName.get(ClassName.get("java.lang", "Iterable"), fragInterface), "fragments")
            .addStatement("return blockContainer($S, fragments)", elementName)
            .build());
        
        // String overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("luvml", "BlockContainerElement"))
            .addParameter(ArrayTypeName.of(String.class), "textContent")
            .varargs()
            .addStatement("return blockContainer($S, textContent)", elementName)
            .build());
        
        // No parameters
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("luvml", "BlockContainerElement"))
            .addStatement("return blockContainer($S)", elementName)
            .build());
    }
    
    private void generateInlineContainerMethods(TypeSpec.Builder classBuilder, String elementName, ParameterizedTypeName fragInterface) {
        // Fragments overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("luvml", "InlineContainerElement"))
            .addParameter(ArrayTypeName.of(fragInterface), "fragments")
            .varargs()
            .addStatement("return inlineContainer($S, fragments)", elementName)
            .build());
        
        // Iterable<Frag_I> overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("luvml", "InlineContainerElement"))
            .addParameter(ParameterizedTypeName.get(ClassName.get("java.lang", "Iterable"), fragInterface), "fragments")
            .addStatement("return inlineContainer($S, fragments)", elementName)
            .build());
        
        // String overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("luvml", "InlineContainerElement"))
            .addParameter(ArrayTypeName.of(String.class), "textContent")
            .varargs()
            .addStatement("return inlineContainer($S, textContent)", elementName)
            .build());
        
        // No parameters
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("luvml", "InlineContainerElement"))
            .addStatement("return inlineContainer($S)", elementName)
            .build());
    }
    
    private void generateBlockVoidMethods(TypeSpec.Builder classBuilder, String elementName, ParameterizedTypeName attrInterface) {
        // Attributes overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("luvml", "BlockVoidElement"))
            .addParameter(ArrayTypeName.of(attrInterface), "attributes")
            .varargs()
            .addStatement("return blockVoidElement($S, attributes)", elementName)
            .build());
        
        // No parameters
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("luvml", "BlockVoidElement"))
            .addStatement("return blockVoidElement($S)", elementName)
            .build());
    }
    
    private void generateInlineVoidMethods(TypeSpec.Builder classBuilder, String elementName, ParameterizedTypeName attrInterface) {
        // Attributes overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("luvml", "InlineVoidElement"))
            .addParameter(ArrayTypeName.of(attrInterface), "attributes")
            .varargs()
            .addStatement("return inlineVoidElement($S, attributes)", elementName)
            .build());
        
        // No parameters
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("luvml", "InlineVoidElement"))
            .addStatement("return inlineVoidElement($S)", elementName)
            .build());
    }
    
    /**
     * Generates E class with 114 static factory methods and static metadata maps.
     */
    private void generateHtmlElements(Path targetDir) throws IOException {
        var fragInterface = ParameterizedTypeName.get(ClassName.get(Frag_I.class), WildcardTypeName.subtypeOf(Object.class));
        var attrInterface = ParameterizedTypeName.get(ClassName.get(Attr_I.class), WildcardTypeName.subtypeOf(Object.class));
        
        var classBuilder = TypeSpec.classBuilder("E")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            
            // Private constructor
            .addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addStatement("// Utility class")
                .build())
                
            // Add static metadata maps for lightweight elements
            .addField(generateContentCategoriesMap())
            .addField(generateDisplayTypesMap())
            .addField(generateElementTypesMap())
            .addField(generateValidContextsMap())
            
            // Add public getters for on-demand metadata fetching
            .addMethod(generateGetContentCategoriesMethod())
            .addMethod(generateGetDisplayTypeMethod())
            .addMethod(generateGetElementTypeMethod())
            .addMethod(generateGetValidContextsMethod())
            
            // Block container utility methods
            .addMethod(MethodSpec.methodBuilder("blockContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("luvml", "BlockContainerElement"))
                .addParameter(String.class, "tagName")
                .addParameter(ArrayTypeName.of(fragInterface), "fragments")
                .varargs()
                .addStatement("return new $T(tagName).addContent(fragments)", ClassName.get("luvml", "BlockContainerElement"))
                .build())
                
            // Iterable<Frag_I> overload for blockContainer  
            .addMethod(MethodSpec.methodBuilder("blockContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("luvml", "BlockContainerElement"))
                .addParameter(String.class, "tagName")
                .addParameter(ParameterizedTypeName.get(ClassName.get("java.lang", "Iterable"), fragInterface), "fragments")
                .beginControlFlow("if (fragments instanceof $T)", ParameterizedTypeName.get(ClassName.get("java.util", "Collection"), fragInterface))
                .addStatement("var fragArray = (($T) fragments).toArray(new $T[0])", 
                    ParameterizedTypeName.get(ClassName.get("java.util", "Collection"), fragInterface), 
                    fragInterface)
                .addStatement("return new $T(tagName).addContent(fragArray)", ClassName.get("luvml", "BlockContainerElement"))
                .nextControlFlow("else")
                .addStatement("var fragList = new $T<$T>()", ClassName.get("java.util", "ArrayList"), fragInterface)
                .addStatement("fragments.forEach(fragList::add)")
                .addStatement("return new $T(tagName).addContent(fragList.toArray(new $T[0]))", 
                    ClassName.get("luvml", "BlockContainerElement"), 
                    fragInterface)
                .endControlFlow()
                .build())
                
            .addMethod(MethodSpec.methodBuilder("blockContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("luvml", "BlockContainerElement"))
                .addParameter(String.class, "tagName")
                .addParameter(ArrayTypeName.of(String.class), "textContent")
                .varargs()
                .addStatement("return new $T(tagName).addContent(textContent)", ClassName.get("luvml", "BlockContainerElement"))
                .build())
                
            .addMethod(MethodSpec.methodBuilder("blockContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("luvml", "BlockContainerElement"))
                .addParameter(String.class, "tagName")                
                .addStatement("return new $T(tagName)", ClassName.get("luvml", "BlockContainerElement"))
                .build())
            
            // Inline container utility methods
            .addMethod(MethodSpec.methodBuilder("inlineContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("luvml", "InlineContainerElement"))
                .addParameter(String.class, "tagName")
                .addParameter(ArrayTypeName.of(fragInterface), "fragments")
                .varargs()
                .addStatement("return new $T(tagName).addContent(fragments)", ClassName.get("luvml", "InlineContainerElement"))
                .build())
                
            // Iterable<Frag_I> overload for inlineContainer  
            .addMethod(MethodSpec.methodBuilder("inlineContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("luvml", "InlineContainerElement"))
                .addParameter(String.class, "tagName")
                .addParameter(ParameterizedTypeName.get(ClassName.get("java.lang", "Iterable"), fragInterface), "fragments")
                .beginControlFlow("if (fragments instanceof $T)", ParameterizedTypeName.get(ClassName.get("java.util", "Collection"), fragInterface))
                .addStatement("var fragArray = (($T) fragments).toArray(new $T[0])", 
                    ParameterizedTypeName.get(ClassName.get("java.util", "Collection"), fragInterface), 
                    fragInterface)
                .addStatement("return new $T(tagName).addContent(fragArray)", ClassName.get("luvml", "InlineContainerElement"))
                .nextControlFlow("else")
                .addStatement("var fragList = new $T<$T>()", ClassName.get("java.util", "ArrayList"), fragInterface)
                .addStatement("fragments.forEach(fragList::add)")
                .addStatement("return new $T(tagName).addContent(fragList.toArray(new $T[0]))", 
                    ClassName.get("luvml", "InlineContainerElement"), 
                    fragInterface)
                .endControlFlow()
                .build())
                
            .addMethod(MethodSpec.methodBuilder("inlineContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("luvml", "InlineContainerElement"))
                .addParameter(String.class, "tagName")
                .addParameter(ArrayTypeName.of(String.class), "textContent")
                .varargs()
                .addStatement("return new $T(tagName).addContent(textContent)", ClassName.get("luvml", "InlineContainerElement"))
                .build())
                
            .addMethod(MethodSpec.methodBuilder("inlineContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("luvml", "InlineContainerElement"))
                .addParameter(String.class, "tagName")                
                .addStatement("return new $T(tagName)", ClassName.get("luvml", "InlineContainerElement"))
                .build())
            
            // Block void element utility methods
            .addMethod(MethodSpec.methodBuilder("blockVoidElement")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("luvml", "BlockVoidElement"))
                .addParameter(String.class, "tagName")
                .addParameter(ArrayTypeName.of(attrInterface), "attributes")
                .varargs()
                .addStatement("return new $T(tagName).addAttributes(attributes)", ClassName.get("luvml", "BlockVoidElement"))
                .build())
                
            .addMethod(MethodSpec.methodBuilder("blockVoidElement")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("luvml", "BlockVoidElement"))
                .addParameter(String.class, "tagName")
                .addStatement("return new $T(tagName)", ClassName.get("luvml", "BlockVoidElement"))
                .build())
            
            // Inline void element utility methods
            .addMethod(MethodSpec.methodBuilder("inlineVoidElement")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("luvml", "InlineVoidElement"))
                .addParameter(String.class, "tagName")
                .addParameter(ArrayTypeName.of(attrInterface), "attributes")
                .varargs()
                .addStatement("return new $T(tagName).addAttributes(attributes)", ClassName.get("luvml", "InlineVoidElement"))
                .build())
                
            .addMethod(MethodSpec.methodBuilder("inlineVoidElement")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("luvml", "InlineVoidElement"))
                .addParameter(String.class, "tagName")
                .addStatement("return new $T(tagName)", ClassName.get("luvml", "InlineVoidElement"))
                .build())
                    
                
                ;
        
        // Generate markup rendering behavior aware static factory methods for all elements
        for (var element : HtmlElementData.ALL_ELEMENTS) {
            var elementName = element.element();
            var elementType = element.elementType();
            
            switch (elementType) {
                case CONTAINER, RAW_TEXT, ESCAPABLE_RAW_TEXT -> {
                    if (isPhrasingElement(elementName)) {
                        generateInlineContainerMethods(classBuilder, elementName, fragInterface);
                    } else {
                        generateBlockContainerMethods(classBuilder, elementName, fragInterface);
                    }
                }
                case VOID -> {
                    if (isBlockVoidElement(elementName)) {
                        generateBlockVoidMethods(classBuilder, elementName, attrInterface);
                    } else {
                        generateInlineVoidMethods(classBuilder, elementName, attrInterface);
                    }
                }
            }
        }
        
        JavaFile.builder("luvml", classBuilder.build())
            .addStaticImport(ContentCategory.class, "*")
            .addStaticImport(DisplayType.class, "*") 
            .addStaticImport(ElementType.class, "*")
            .addStaticImport(Context.class, "*")
            .build()
            .writeTo(targetDir.getParent());
    }
    
    /**
     * Formats a set of enums as a Set.of() literal for code generation.
     * Uses clean enum names without class prefixes due to static imports.
     * Example: Set.of(FLOW, PHRASING) instead of Set.of(ContentCategory.FLOW, ContentCategory.PHRASING)
     */
    private String formatSetLiteral(Set<?> enumSet, String enumClassName) {
        if (enumSet.isEmpty()) {
            return "Set.of()";
        }
        
        // Use clean enum names without class prefixes since we're using static imports
        var enumValues = enumSet.stream()
            .map(e -> e.toString())  // Just the enum name, no class prefix
            .toArray(String[]::new);
            
        return "Set.of(" + String.join(", ", enumValues) + ")";
    }
    
    // ======================== STATIC METADATA MAPS GENERATION ========================
    
    /**
     * Generates static CONTENT_CATEGORIES map for lightweight elements.
     */
    private FieldSpec generateContentCategoriesMap() {
        var mapType = ParameterizedTypeName.get(ClassName.get("java.util", "Map"), 
            ClassName.get(String.class), 
            ParameterizedTypeName.get(ClassName.get("java.util", "Set"), ClassName.get("luvml", "ContentCategory")));
            
        var mapBuilder = CodeBlock.builder();
        mapBuilder.add("new $T<String, $T<$T>>() {\n", 
            ClassName.get("java.util", "HashMap"), 
            ClassName.get("java.util", "Set"), 
            ClassName.get("luvml", "ContentCategory"));
        mapBuilder.add("    {\n");
        
        for (var element : HtmlElementData.ALL_ELEMENTS) {
            var categoriesLiteral = formatSetLiteral(element.contentCategories(), "ContentCategory");
            mapBuilder.add("        put($S, $L);\n", element.element(), categoriesLiteral);
        }
        
        mapBuilder.add("    }\n");
        mapBuilder.add("}");
        
        return FieldSpec.builder(mapType, "CONTENT_CATEGORIES")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer(mapBuilder.build())
            .build();
    }
    
    /**
     * Generates static DISPLAY_TYPES map for lightweight elements.
     */
    private FieldSpec generateDisplayTypesMap() {
        var mapType = ParameterizedTypeName.get(ClassName.get("java.util", "Map"), 
            ClassName.get(String.class), 
            ClassName.get("luvml", "DisplayType"));
            
        var mapBuilder = CodeBlock.builder();
        mapBuilder.add("new $T<String, $T>() {\n", 
            ClassName.get("java.util", "HashMap"), 
            ClassName.get("luvml", "DisplayType"));
        mapBuilder.add("    {\n");
        
        for (var element : HtmlElementData.ALL_ELEMENTS) {
            mapBuilder.add("        put($S, $L);\n", element.element(), element.displayType().toString());
        }
        
        mapBuilder.add("    }\n");
        mapBuilder.add("}");
        
        return FieldSpec.builder(mapType, "DISPLAY_TYPES")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer(mapBuilder.build())
            .build();
    }
    
    /**
     * Generates static ELEMENT_TYPES map for lightweight elements.
     */
    private FieldSpec generateElementTypesMap() {
        var mapType = ParameterizedTypeName.get(ClassName.get("java.util", "Map"), 
            ClassName.get(String.class), 
            ClassName.get("luvml", "ElementType"));
            
        var mapBuilder = CodeBlock.builder();
        mapBuilder.add("new $T<String, $T>() {\n", 
            ClassName.get("java.util", "HashMap"), 
            ClassName.get("luvml", "ElementType"));
        mapBuilder.add("    {\n");
        
        for (var element : HtmlElementData.ALL_ELEMENTS) {
            mapBuilder.add("        put($S, $L);\n", element.element(), element.elementType().toString());
        }
        
        mapBuilder.add("    }\n");
        mapBuilder.add("}");
        
        return FieldSpec.builder(mapType, "ELEMENT_TYPES")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer(mapBuilder.build())
            .build();
    }
    
    /**
     * Generates static VALID_CONTEXTS map for lightweight elements.
     */
    private FieldSpec generateValidContextsMap() {
        var mapType = ParameterizedTypeName.get(ClassName.get("java.util", "Map"), 
            ClassName.get(String.class), 
            ParameterizedTypeName.get(ClassName.get("java.util", "Set"), ClassName.get("luvml", "Context")));
            
        var mapBuilder = CodeBlock.builder();
        mapBuilder.add("new $T<String, $T<$T>>() {\n", 
            ClassName.get("java.util", "HashMap"), 
            ClassName.get("java.util", "Set"), 
            ClassName.get("luvml", "Context"));
        mapBuilder.add("    {\n");
        
        for (var element : HtmlElementData.ALL_ELEMENTS) {
            var contextsLiteral = formatSetLiteral(element.contexts(), "Context");
            mapBuilder.add("        put($S, $L);\n", element.element(), contextsLiteral);
        }
        
        mapBuilder.add("    }\n");
        mapBuilder.add("}");
        
        return FieldSpec.builder(mapType, "VALID_CONTEXTS")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer(mapBuilder.build())
            .build();
    }
    
    // ======================== PUBLIC GETTER METHODS ========================
    
    /**
     * Generates public getter for content categories.
     */
    private MethodSpec generateGetContentCategoriesMethod() {
        return MethodSpec.methodBuilder("getContentCategories")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ParameterizedTypeName.get(ClassName.get("java.util", "Set"), ClassName.get("luvml", "ContentCategory")))
            .addParameter(String.class, "elementName")
            .addStatement("return CONTENT_CATEGORIES.getOrDefault(elementName, $T.of())", ClassName.get("java.util", "Set"))
            .build();
    }
    
    /**
     * Generates public getter for display type.
     */
    private MethodSpec generateGetDisplayTypeMethod() {
        return MethodSpec.methodBuilder("getDisplayType")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("luvml", "DisplayType"))
            .addParameter(String.class, "elementName")
            .addStatement("return DISPLAY_TYPES.get(elementName)")
            .build();
    }
    
    /**
     * Generates public getter for element type.
     */
    private MethodSpec generateGetElementTypeMethod() {
        return MethodSpec.methodBuilder("getElementType")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get("luvml", "ElementType"))
            .addParameter(String.class, "elementName")
            .addStatement("return ELEMENT_TYPES.get(elementName)")
            .build();
    }
    
    /**
     * Generates public getter for valid contexts.
     */
    private MethodSpec generateGetValidContextsMethod() {
        return MethodSpec.methodBuilder("getValidContexts")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ParameterizedTypeName.get(ClassName.get("java.util", "Set"), ClassName.get("luvml", "Context")))
            .addParameter(String.class, "elementName")
            .addStatement("return VALID_CONTEXTS.getOrDefault(elementName, $T.of())", ClassName.get("java.util", "Set"))
            .build();
    }
}