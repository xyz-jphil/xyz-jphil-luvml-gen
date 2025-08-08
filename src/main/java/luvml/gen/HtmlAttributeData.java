package luvml.gen;

import java.util.*;
import static java.util.Collections.EMPTY_SET;
import luvml.*;

import static java.util.Set.of;
import static luvml.AttributeCategory.*;
import static luvml.AttributeType.*;
import static luvml.AttributeScope.*;

public record HtmlAttributeData(
    String attribute,
    Set<AttributeCategory> categories,
    AttributeType type,
    AttributeScope scope,
    String description,
    Set<String> enumValues  // For ENUM type attributes
) {
    
    private static Set<AttributeCategory> categories(AttributeCategory... categories) {
        if(categories==null || categories.length<1)return EMPTY_SET;
        return of(categories);
    }
    
    
    
    private static Set<String> enumValues(String... values) {
        return values.length == 0 ? Set.of() : of(values);
    }
    
    private static HtmlAttributeData attribute(String name, AttributeType type, AttributeScope scope,
                                             Set<AttributeCategory> categories, String description) {
        return new HtmlAttributeData(name, categories, type, scope, description, Set.of());
    }
    
    private static HtmlAttributeData enumAttribute(String name, AttributeScope scope,
                                                 Set<AttributeCategory> categories, String description,
                                                 String... enumValues) {
        return new HtmlAttributeData(name, categories, ENUM, scope, description, of(enumValues));
    }
    
    public static final List<HtmlAttributeData> ALL_ATTRIBUTES = List.of(
        // Global attributes
        attribute("accesskey", STRING, UNIVERSAL, categories(GLOBAL), "Keyboard shortcut for element"),
        attribute("class", TOKEN_LIST, UNIVERSAL, categories(GLOBAL), "CSS class names"),
        enumAttribute("contenteditable", UNIVERSAL, categories(GLOBAL), "Whether content is editable", "true", "false", "plaintext-only"),
        enumAttribute("dir", UNIVERSAL, categories(GLOBAL), "Text directionality", "ltr", "rtl", "auto"),
        attribute("draggable", BOOLEAN, UNIVERSAL, categories(GLOBAL), "Whether element is draggable"),
        attribute("hidden", BOOLEAN, UNIVERSAL, categories(GLOBAL), "Whether element is hidden"),
        attribute("id", STRING, UNIVERSAL, categories(GLOBAL), "Unique identifier"),
        attribute("lang", LANGUAGE, UNIVERSAL, categories(GLOBAL), "Language of element content"),
        attribute("spellcheck", BOOLEAN, UNIVERSAL, categories(GLOBAL), "Whether to check spelling"),
        attribute("style", STRING, UNIVERSAL, categories(GLOBAL), "Inline CSS styles"),
        attribute("tabindex", NUMBER, UNIVERSAL, categories(GLOBAL), "Tab order for keyboard navigation"),
        attribute("title", STRING, UNIVERSAL, categories(GLOBAL), "Advisory information about element"),
        enumAttribute("translate", UNIVERSAL, categories(GLOBAL), "Whether content should be translated", "yes", "no"),
        
        // Data attributes (template)
        attribute("data-*", STRING, UNIVERSAL, categories(GLOBAL, DATA), "Custom data attributes"),
        
        // Event handler attributes (common ones)
        attribute("onclick", SCRIPT, UNIVERSAL, categories(GLOBAL, EVENT), "Click event handler"),
        attribute("onload", SCRIPT, UNIVERSAL, categories(GLOBAL, EVENT), "Load event handler"),
        attribute("onchange", SCRIPT, UNIVERSAL, categories(GLOBAL, EVENT), "Change event handler"),
        attribute("onsubmit", SCRIPT, UNIVERSAL, categories(GLOBAL, EVENT), "Submit event handler"),
        attribute("onfocus", SCRIPT, UNIVERSAL, categories(GLOBAL, EVENT), "Focus event handler"),
        attribute("onblur", SCRIPT, UNIVERSAL, categories(GLOBAL, EVENT), "Blur event handler"),
        
        // ARIA attributes (selection)
        attribute("aria-label", STRING, UNIVERSAL, categories(ACCESSIBILITY), "Accessible name for element"),
        attribute("aria-labelledby", TOKEN_LIST, UNIVERSAL, categories(ACCESSIBILITY), "IDs of elements that label this element"),
        attribute("aria-describedby", TOKEN_LIST, UNIVERSAL, categories(ACCESSIBILITY), "IDs of elements that describe this element"),
        attribute("aria-hidden", BOOLEAN, UNIVERSAL, categories(ACCESSIBILITY), "Whether element is hidden from assistive technology"),
        enumAttribute("aria-expanded", UNIVERSAL, categories(ACCESSIBILITY), "Whether collapsible element is expanded", "true", "false", "undefined"),
        
        // Form attributes
        attribute("accept", COMMA_LIST, FORM_ELEMENTS, categories(FORM), "File types the server accepts"),
        attribute("accept-charset", TOKEN_LIST, FORM_ELEMENTS, categories(FORM), "Character encodings for form submission"),
        attribute("action", URL, FORM_ELEMENTS, categories(FORM), "URL for form submission"),
        enumAttribute("autocomplete", FORM_ELEMENTS, categories(FORM), "Whether form control should have autocomplete", "on", "off", "name", "email", "username", "current-password", "new-password"),
        attribute("autofocus", BOOLEAN, FORM_ELEMENTS, categories(FORM), "Whether element should be focused on page load"),
        attribute("checked", BOOLEAN, FORM_ELEMENTS, categories(FORM), "Whether input is checked"),
        attribute("disabled", BOOLEAN, FORM_ELEMENTS, categories(FORM), "Whether form control is disabled"),
        enumAttribute("enctype", FORM_ELEMENTS, categories(FORM), "Encoding type for form submission", "application/x-www-form-urlencoded", "multipart/form-data", "text/plain"),
        attribute("for", STRING, FORM_ELEMENTS, categories(FORM), "ID of form control this label is for"),
        attribute("form", STRING, FORM_ELEMENTS, categories(FORM), "ID of form this element belongs to"),
        attribute("formaction", URL, FORM_ELEMENTS, categories(FORM), "URL for form submission (overrides form action)"),
        enumAttribute("formenctype", FORM_ELEMENTS, categories(FORM), "Encoding type (overrides form enctype)", "application/x-www-form-urlencoded", "multipart/form-data", "text/plain"),
        enumAttribute("formmethod", FORM_ELEMENTS, categories(FORM), "HTTP method (overrides form method)", "get", "post"),
        attribute("formnovalidate", BOOLEAN, FORM_ELEMENTS, categories(FORM), "Skip form validation on submission"),
        attribute("formtarget", STRING, FORM_ELEMENTS, categories(FORM), "Target for form submission (overrides form target)"),
        attribute("max", STRING, FORM_ELEMENTS, categories(FORM), "Maximum value for input"),
        attribute("maxlength", NUMBER, FORM_ELEMENTS, categories(FORM), "Maximum number of characters"),
        enumAttribute("method", FORM_ELEMENTS, categories(FORM), "HTTP method for form submission", "get", "post"),
        attribute("min", STRING, FORM_ELEMENTS, categories(FORM), "Minimum value for input"),
        attribute("minlength", NUMBER, FORM_ELEMENTS, categories(FORM), "Minimum number of characters"),
        attribute("multiple", BOOLEAN, FORM_ELEMENTS, categories(FORM), "Whether multiple values are allowed"),
        attribute("name", STRING, FORM_ELEMENTS, categories(FORM), "Name of form control"),
        attribute("novalidate", BOOLEAN, FORM_ELEMENTS, categories(FORM), "Skip form validation"),
        attribute("pattern", REGEX, FORM_ELEMENTS, categories(FORM), "Regular expression for input validation"),
        attribute("placeholder", STRING, FORM_ELEMENTS, categories(FORM), "Placeholder text for input"),
        attribute("readonly", BOOLEAN, FORM_ELEMENTS, categories(FORM), "Whether form control is read-only"),
        attribute("required", BOOLEAN, FORM_ELEMENTS, categories(FORM), "Whether form control is required"),
        attribute("selected", BOOLEAN, FORM_ELEMENTS, categories(FORM), "Whether option is selected"),
        attribute("size", NUMBER, FORM_ELEMENTS, categories(FORM), "Size of form control"),
        attribute("step", STRING, FORM_ELEMENTS, categories(FORM), "Step value for numeric inputs"),
        enumAttribute("target", FORM_ELEMENTS, categories(FORM), "Target for form submission", "_blank", "_self", "_parent", "_top"),
        enumAttribute("type", FORM_ELEMENTS, categories(FORM), "Type of input control", 
            "text", "password", "email", "url", "tel", "search", "number", "range", "date", "time", "datetime-local", 
            "month", "week", "color", "file", "hidden", "checkbox", "radio", "submit", "reset", "button", "image"),
        attribute("value", STRING, FORM_ELEMENTS, categories(FORM), "Value of form control"),
        
        // Media attributes
        attribute("alt", STRING, MEDIA_ELEMENTS, categories(MEDIA), "Alternative text for image"),
        attribute("autoplay", BOOLEAN, MEDIA_ELEMENTS, categories(MEDIA), "Whether media should autoplay"),
        attribute("controls", BOOLEAN, MEDIA_ELEMENTS, categories(MEDIA), "Whether media controls should be shown"),
        enumAttribute("crossorigin", MEDIA_ELEMENTS, categories(MEDIA), "CORS settings for media", "anonymous", "use-credentials"),
        attribute("height", DIMENSION, MEDIA_ELEMENTS, categories(MEDIA), "Height of media element"),
        attribute("loop", BOOLEAN, MEDIA_ELEMENTS, categories(MEDIA), "Whether media should loop"),
        attribute("muted", BOOLEAN, MEDIA_ELEMENTS, categories(MEDIA), "Whether media should be muted"),
        enumAttribute("preload", MEDIA_ELEMENTS, categories(MEDIA), "How media should be preloaded", "none", "metadata", "auto"),
        attribute("poster", URL, MEDIA_ELEMENTS, categories(MEDIA), "Poster image for video"),
        attribute("src", URL, MEDIA_ELEMENTS, categories(MEDIA), "Source URL for media"),
        attribute("srcset", STRING, MEDIA_ELEMENTS, categories(MEDIA), "Set of source images with descriptors"),
        attribute("width", DIMENSION, MEDIA_ELEMENTS, categories(MEDIA), "Width of media element"),
        
        // Link attributes
        attribute("download", STRING, LINK_ELEMENTS, categories(LINK), "Filename for download"),
        attribute("href", URL, LINK_ELEMENTS, categories(LINK), "Hyperlink reference"),
        attribute("hreflang", LANGUAGE, LINK_ELEMENTS, categories(LINK), "Language of linked resource"),
        attribute("ping", TOKEN_LIST, LINK_ELEMENTS, categories(LINK), "URLs to ping when link is followed"),
        enumAttribute("referrerpolicy", LINK_ELEMENTS, categories(LINK), "Referrer policy for link", 
            "no-referrer", "no-referrer-when-downgrade", "origin", "origin-when-cross-origin", "same-origin", "strict-origin", "strict-origin-when-cross-origin", "unsafe-url"),
        enumAttribute("rel", LINK_ELEMENTS, categories(LINK), "Relationship to linked resource",
            "alternate", "author", "bookmark", "canonical", "dns-prefetch", "external", "help", "icon", "license", "manifest", "next", "nofollow", "noopener", "noreferrer", "opener", "prev", "preconnect", "prefetch", "preload", "prerender", "search", "stylesheet", "tag"),
        attribute("sizes", TOKEN_LIST, LINK_ELEMENTS, categories(LINK), "Sizes of linked resource (for icons)"),
        enumAttribute("type", LINK_ELEMENTS, categories(LINK), "MIME type of linked resource", "text/css", "text/javascript", "image/x-icon", "application/rss+xml"),
        
        // Table attributes
        attribute("colspan", NUMBER, TABLE_ELEMENTS, categories(TABLE), "Number of columns cell spans"),
        attribute("rowspan", NUMBER, TABLE_ELEMENTS, categories(TABLE), "Number of rows cell spans"),
        attribute("headers", TOKEN_LIST, TABLE_ELEMENTS, categories(TABLE), "IDs of header cells for this cell"),
        enumAttribute("scope", TABLE_ELEMENTS, categories(TABLE), "Scope of header cell", "row", "col", "rowgroup", "colgroup"),
        attribute("span", NUMBER, TABLE_ELEMENTS, categories(TABLE), "Number of columns in column group"),
        
        // Interactive attributes
        attribute("open", BOOLEAN, INTERACTIVE_ELEMENTS, categories(INTERACTIVE), "Whether details element is open"),
        
        // Metadata attributes
        attribute("charset", CHARSET, METADATA_ELEMENTS, categories(METADATA), "Character encoding"),
        attribute("content", STRING, METADATA_ELEMENTS, categories(METADATA), "Value of meta element"),
        attribute("http-equiv", STRING, METADATA_ELEMENTS, categories(METADATA), "HTTP header name"),
        attribute("media", STRING, METADATA_ELEMENTS, categories(METADATA), "Media query for linked resource"),
        
        // Microdata attributes
        attribute("itemid", URL, UNIVERSAL, categories(MICRODATA), "Global identifier for microdata item"),
        attribute("itemprop", TOKEN_LIST, UNIVERSAL, categories(MICRODATA), "Microdata property names"),
        attribute("itemref", TOKEN_LIST, UNIVERSAL, categories(MICRODATA), "IDs of additional microdata properties"),
        attribute("itemscope", BOOLEAN, UNIVERSAL, categories(MICRODATA), "Whether element is microdata item"),
        attribute("itemtype", URL, UNIVERSAL, categories(MICRODATA), "Microdata vocabulary URL"),
        
        // Specific element attributes
        attribute("coords", COMMA_LIST, SPECIFIC_ELEMENTS, categories(null), "Coordinates for area element"),
        enumAttribute("shape", SPECIFIC_ELEMENTS, categories(null), "Shape of area element", "rect", "circle", "poly", "default"),
        attribute("usemap", STRING, SPECIFIC_ELEMENTS, categories(null), "Name of image map to use"),
        enumAttribute("wrap", SPECIFIC_ELEMENTS, categories(null), "How text should wrap in textarea", "soft", "hard"),
        attribute("rows", NUMBER, SPECIFIC_ELEMENTS, categories(null), "Number of rows in textarea"),
        attribute("cols", NUMBER, SPECIFIC_ELEMENTS, categories(null), "Number of columns in textarea"),
        attribute("start", NUMBER, SPECIFIC_ELEMENTS, categories(null), "Starting number for ordered list"),
        attribute("reversed", BOOLEAN, SPECIFIC_ELEMENTS, categories(null), "Whether ordered list is reversed"),
        enumAttribute("kind", SPECIFIC_ELEMENTS, categories(null), "Kind of text track", "subtitles", "captions", "descriptions", "chapters", "metadata"),
        attribute("srclang", LANGUAGE, SPECIFIC_ELEMENTS, categories(null), "Language of text track"),
        attribute("label", STRING, SPECIFIC_ELEMENTS, categories(null), "User-readable title for text track"),
        attribute("default", BOOLEAN, SPECIFIC_ELEMENTS, categories(null), "Whether track should be enabled by default"),
        
        // Deprecated attributes (common ones still encountered)
        attribute("align", STRING, SPECIFIC_ELEMENTS, categories(DEPRECATED), "Alignment (deprecated, use CSS)"),
        attribute("bgcolor", COLOR, SPECIFIC_ELEMENTS, categories(DEPRECATED), "Background color (deprecated, use CSS)"),
        attribute("border", NUMBER, SPECIFIC_ELEMENTS, categories(DEPRECATED), "Border width (deprecated, use CSS)"),
        attribute("cellpadding", NUMBER, SPECIFIC_ELEMENTS, categories(DEPRECATED), "Cell padding (deprecated, use CSS)"),
        attribute("cellspacing", NUMBER, SPECIFIC_ELEMENTS, categories(DEPRECATED), "Cell spacing (deprecated, use CSS)"),
        attribute("color", COLOR, SPECIFIC_ELEMENTS, categories(DEPRECATED), "Text color (deprecated, use CSS)"),
        attribute("face", STRING, SPECIFIC_ELEMENTS, categories(DEPRECATED), "Font face (deprecated, use CSS)"),
        attribute("size", NUMBER, SPECIFIC_ELEMENTS, categories(DEPRECATED), "Font size (deprecated, use CSS)")
    );
}