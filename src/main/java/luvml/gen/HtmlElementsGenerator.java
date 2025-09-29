package luvml.gen;

import com.squareup.javapoet.*;
import luvx.Frag_I;
import luvx.Attr_I;
import luvml.*;
import luvml.element.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

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
 *    - All metadata is centralized in HtmlElementData (no duplication)
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
    // Element classification now uses HtmlElementData helper methods
    
    // ======================== GENERATION METHODS ========================
    
    private void generateBlockContainerMethods(TypeSpec.Builder classBuilder, String elementName, ParameterizedTypeName fragInterface) {
        // Fragments overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(BlockContainerElement.class)
            .addParameter(ArrayTypeName.of(fragInterface), "fragments")
            .varargs()
            .addStatement("return blockContainer($S, fragments)", elementName)
            .build());

        // Iterable<Frag_I> overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(BlockContainerElement.class)
            .addParameter(ParameterizedTypeName.get(ClassName.get(Iterable.class), fragInterface), "fragments")
            .addStatement("return blockContainer($S, fragments)", elementName)
            .build());

        // String overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(BlockContainerElement.class)
            .addParameter(ArrayTypeName.of(String.class), "textContent")
            .varargs()
            .addStatement("return blockContainer($S, textContent)", elementName)
            .build());

        // No parameters
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(BlockContainerElement.class)
            .addStatement("return blockContainer($S)", elementName)
            .build());
    }
    
    private void generateInlineContainerMethods(TypeSpec.Builder classBuilder, String elementName, ParameterizedTypeName fragInterface) {
        // Fragments overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(InlineContainerElement.class)
            .addParameter(ArrayTypeName.of(fragInterface), "fragments")
            .varargs()
            .addStatement("return inlineContainer($S, fragments)", elementName)
            .build());

        // Iterable<Frag_I> overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(InlineContainerElement.class)
            .addParameter(ParameterizedTypeName.get(ClassName.get(Iterable.class), fragInterface), "fragments")
            .addStatement("return inlineContainer($S, fragments)", elementName)
            .build());

        // String overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(InlineContainerElement.class)
            .addParameter(ArrayTypeName.of(String.class), "textContent")
            .varargs()
            .addStatement("return inlineContainer($S, textContent)", elementName)
            .build());

        // No parameters
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(InlineContainerElement.class)
            .addStatement("return inlineContainer($S)", elementName)
            .build());
    }
    
    private void generateBlockVoidMethods(TypeSpec.Builder classBuilder, String elementName, ParameterizedTypeName attrInterface) {
        // Attributes overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(BlockVoidElement.class)
            .addParameter(ArrayTypeName.of(attrInterface), "attributes")
            .varargs()
            .addStatement("return blockVoidElement($S, attributes)", elementName)
            .build());

        // No parameters
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(BlockVoidElement.class)
            .addStatement("return blockVoidElement($S)", elementName)
            .build());
    }
    
    private void generateInlineVoidMethods(TypeSpec.Builder classBuilder, String elementName, ParameterizedTypeName attrInterface) {
        // Attributes overload
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(InlineVoidElement.class)
            .addParameter(ArrayTypeName.of(attrInterface), "attributes")
            .varargs()
            .addStatement("return inlineVoidElement($S, attributes)", elementName)
            .build());

        // No parameters
        classBuilder.addMethod(MethodSpec.methodBuilder(elementName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(InlineVoidElement.class)
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
            
            // Block container utility methods
            .addMethod(MethodSpec.methodBuilder("blockContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(BlockContainerElement.class)
                .addParameter(String.class, "tagName")
                .addParameter(ArrayTypeName.of(fragInterface), "fragments")
                .varargs()
                .addStatement("return new $T(tagName).addContent(fragments)", BlockContainerElement.class)
                .build())

            // Iterable<Frag_I> overload for blockContainer
            .addMethod(MethodSpec.methodBuilder("blockContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(BlockContainerElement.class)
                .addParameter(String.class, "tagName")
                .addParameter(ParameterizedTypeName.get(ClassName.get(Iterable.class), fragInterface), "fragments")
                .beginControlFlow("if (fragments instanceof $T)", ParameterizedTypeName.get(ClassName.get(Collection.class), fragInterface))
                .addStatement("var fragArray = (($T) fragments).toArray(new $T[0])",
                    ParameterizedTypeName.get(ClassName.get(Collection.class), fragInterface),
                    fragInterface)
                .addStatement("return new $T(tagName).addContent(fragArray)", BlockContainerElement.class)
                .nextControlFlow("else")
                .addStatement("var fragList = new $T<$T>()", ArrayList.class, fragInterface)
                .addStatement("fragments.forEach(fragList::add)")
                .addStatement("return new $T(tagName).addContent(fragList.toArray(new $T[0]))",
                    BlockContainerElement.class,
                    fragInterface)
                .endControlFlow()
                .build())

            .addMethod(MethodSpec.methodBuilder("blockContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(BlockContainerElement.class)
                .addParameter(String.class, "tagName")
                .addParameter(ArrayTypeName.of(String.class), "textContent")
                .varargs()
                .addStatement("return new $T(tagName).addContent(textContent)", BlockContainerElement.class)
                .build())

            .addMethod(MethodSpec.methodBuilder("blockContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(BlockContainerElement.class)
                .addParameter(String.class, "tagName")
                .addStatement("return new $T(tagName)", BlockContainerElement.class)
                .build())
            
            // Inline container utility methods
            .addMethod(MethodSpec.methodBuilder("inlineContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(InlineContainerElement.class)
                .addParameter(String.class, "tagName")
                .addParameter(ArrayTypeName.of(fragInterface), "fragments")
                .varargs()
                .addStatement("return new $T(tagName).addContent(fragments)", InlineContainerElement.class)
                .build())

            // Iterable<Frag_I> overload for inlineContainer
            .addMethod(MethodSpec.methodBuilder("inlineContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(InlineContainerElement.class)
                .addParameter(String.class, "tagName")
                .addParameter(ParameterizedTypeName.get(ClassName.get(Iterable.class), fragInterface), "fragments")
                .beginControlFlow("if (fragments instanceof $T)", ParameterizedTypeName.get(ClassName.get(Collection.class), fragInterface))
                .addStatement("var fragArray = (($T) fragments).toArray(new $T[0])",
                    ParameterizedTypeName.get(ClassName.get(Collection.class), fragInterface),
                    fragInterface)
                .addStatement("return new $T(tagName).addContent(fragArray)", InlineContainerElement.class)
                .nextControlFlow("else")
                .addStatement("var fragList = new $T<$T>()", ArrayList.class, fragInterface)
                .addStatement("fragments.forEach(fragList::add)")
                .addStatement("return new $T(tagName).addContent(fragList.toArray(new $T[0]))",
                    InlineContainerElement.class,
                    fragInterface)
                .endControlFlow()
                .build())

            .addMethod(MethodSpec.methodBuilder("inlineContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(InlineContainerElement.class)
                .addParameter(String.class, "tagName")
                .addParameter(ArrayTypeName.of(String.class), "textContent")
                .varargs()
                .addStatement("return new $T(tagName).addContent(textContent)", InlineContainerElement.class)
                .build())

            .addMethod(MethodSpec.methodBuilder("inlineContainer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(InlineContainerElement.class)
                .addParameter(String.class, "tagName")
                .addStatement("return new $T(tagName)", InlineContainerElement.class)
                .build())
            
            // Block void element utility methods
            .addMethod(MethodSpec.methodBuilder("blockVoidElement")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(BlockVoidElement.class)
                .addParameter(String.class, "tagName")
                .addParameter(ArrayTypeName.of(attrInterface), "attributes")
                .varargs()
                .addStatement("return new $T(tagName).addAttributes(attributes)", BlockVoidElement.class)
                .build())

            .addMethod(MethodSpec.methodBuilder("blockVoidElement")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(BlockVoidElement.class)
                .addParameter(String.class, "tagName")
                .addStatement("return new $T(tagName)", BlockVoidElement.class)
                .build())

            // Inline void element utility methods
            .addMethod(MethodSpec.methodBuilder("inlineVoidElement")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(InlineVoidElement.class)
                .addParameter(String.class, "tagName")
                .addParameter(ArrayTypeName.of(attrInterface), "attributes")
                .varargs()
                .addStatement("return new $T(tagName).addAttributes(attributes)", InlineVoidElement.class)
                .build())

            .addMethod(MethodSpec.methodBuilder("inlineVoidElement")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(InlineVoidElement.class)
                .addParameter(String.class, "tagName")
                .addStatement("return new $T(tagName)", InlineVoidElement.class)
                .build())
                    
                
                ;
        
        // Generate markup rendering behavior aware static factory methods for all elements
        for (var element : HtmlElementData.ALL_ELEMENTS.values()) {
            var elementName = element.element();
            var elementType = element.elementType();

            switch (elementType) {
                case CONTAINER, RAW_TEXT, ESCAPABLE_RAW_TEXT -> {
                    if (element.isPhrasingElement()) {
                        generateInlineContainerMethods(classBuilder, elementName, fragInterface);
                    } else {
                        generateBlockContainerMethods(classBuilder, elementName, fragInterface);
                    }
                }
                case VOID -> {
                    if (element.isBlockVoidElement()) {
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
}