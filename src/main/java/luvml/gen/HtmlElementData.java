package luvml.gen;

import java.util.*;
import luvml.*;

import static java.util.Set.of;
import static luvml.ContentCategory.*;
import static luvml.DisplayType.*;
import static luvml.ElementType.*;
import static luvml.Context.*;

public record HtmlElementData(
    String element,
    Set<ContentCategory> contentCategories,
    DisplayType displayType,
    ElementType elementType,
    Set<Context> contexts,
    String description
) {
    
    private static Set<ContentCategory> categories(ContentCategory... categories) {
        return of(categories);
    }
    
    private static Set<Context> contexts(Context... contexts) {
        return of(contexts);
    }
    
    private static HtmlElementData element(String name, DisplayType display, ElementType type,
                                         Set<ContentCategory> categories, Set<Context> contexts, 
                                         String description) {
        return new HtmlElementData(name, categories, display, type, contexts, description);
    }
    
    public static final List<HtmlElementData> ALL_ELEMENTS = List.of(
        element("a", INLINE, CONTAINER, categories(PHRASING, INTERACTIVE, FLOW), contexts(PHRASING_CTX), "Hyperlink"),
        element("abbr", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Abbreviation"),
        element("address", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Contact information"),
        element("area", NONE, VOID, categories(PHRASING, INTERACTIVE, FLOW), contexts(MAP_CTX), "Image map area"),
        element("article", BLOCK, CONTAINER, categories(FLOW, SECTIONING), contexts(FLOW_CTX), "Independent content"),
        element("aside", BLOCK, CONTAINER, categories(FLOW, SECTIONING), contexts(FLOW_CTX), "Sidebar content"),
        element("audio", INLINE_BLOCK, CONTAINER, categories(PHRASING, EMBEDDED, INTERACTIVE, FLOW), contexts(PHRASING_CTX), "Audio content"),
        element("b", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Bold text"),
        element("base", NONE, VOID, categories(METADATA), contexts(HEAD_CTX), "Document base URL"),
        element("bdi", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Bidirectional isolation"),
        element("bdo", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Bidirectional override"),
        element("blockquote", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Block quotation"),
        element("body", BLOCK, CONTAINER, categories(SECTIONING), contexts(HTML_CTX), "Document body"),
        element("br", INLINE, VOID, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Line break"),
        element("button", INLINE_BLOCK, CONTAINER, categories(PHRASING, INTERACTIVE, FORM_ASSOCIATED, FLOW), contexts(PHRASING_CTX), "Button"),
        element("canvas", INLINE_BLOCK, CONTAINER, categories(PHRASING, EMBEDDED, FLOW), contexts(PHRASING_CTX), "Graphics canvas"),
        element("caption", TABLE, CONTAINER, categories(), contexts(TABLE_CTX), "Table caption"),
        element("cite", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Citation"),
        element("code", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Code fragment"),
        element("col", TABLE, VOID, categories(), contexts(COLGROUP_CTX), "Table column"),
        element("colgroup", TABLE, CONTAINER, categories(), contexts(TABLE_CTX), "Table column group"),
        element("data", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Machine-readable data"),
        element("datalist", NONE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Input options"),
        element("dd", BLOCK, CONTAINER, categories(), contexts(DL_CTX), "Description list description"),
        element("del", INLINE, CONTAINER, categories(PHRASING, FLOW, TRANSPARENT), contexts(PHRASING_CTX, FLOW_CTX), "Deleted text"),
        element("details", BLOCK, CONTAINER, categories(FLOW, INTERACTIVE), contexts(FLOW_CTX), "Disclosure widget"),
        element("dfn", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Definition term"),
        element("dialog", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Dialog box"),
        element("div", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Generic container"),
        element("dl", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Description list"),
        element("dt", BLOCK, CONTAINER, categories(), contexts(DL_CTX), "Description list term"),
        element("em", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Emphasized text"),
        element("embed", INLINE_BLOCK, VOID, categories(PHRASING, EMBEDDED, INTERACTIVE, FLOW), contexts(PHRASING_CTX), "External application"),
        element("fieldset", BLOCK, CONTAINER, categories(FLOW, FORM_ASSOCIATED), contexts(FLOW_CTX), "Form field group"),
        element("figcaption", BLOCK, CONTAINER, categories(), contexts(FIGURE_CTX), "Figure caption"),
        element("figure", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Figure with caption"),
        element("footer", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Footer"),
        element("form", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Form"),
        element("h1", BLOCK, CONTAINER, categories(FLOW, HEADING), contexts(FLOW_CTX), "Level 1 heading"),
        element("h2", BLOCK, CONTAINER, categories(FLOW, HEADING), contexts(FLOW_CTX), "Level 2 heading"),
        element("h3", BLOCK, CONTAINER, categories(FLOW, HEADING), contexts(FLOW_CTX), "Level 3 heading"),
        element("h4", BLOCK, CONTAINER, categories(FLOW, HEADING), contexts(FLOW_CTX), "Level 4 heading"),
        element("h5", BLOCK, CONTAINER, categories(FLOW, HEADING), contexts(FLOW_CTX), "Level 5 heading"),
        element("h6", BLOCK, CONTAINER, categories(FLOW, HEADING), contexts(FLOW_CTX), "Level 6 heading"),
        element("head", NONE, CONTAINER, categories(), contexts(HTML_CTX), "Document head"),
        element("header", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Header"),
        element("hgroup", BLOCK, CONTAINER, categories(FLOW, HEADING), contexts(FLOW_CTX), "Heading group"),
        element("hr", BLOCK, VOID, categories(FLOW), contexts(FLOW_CTX), "Horizontal rule"),
        element("html", BLOCK, CONTAINER, categories(), contexts(ROOT_CTX), "Document root"),
        element("i", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Italic text"),
        element("iframe", INLINE_BLOCK, CONTAINER, categories(PHRASING, EMBEDDED, INTERACTIVE, FLOW), contexts(PHRASING_CTX), "Inline frame"),
        element("img", INLINE_BLOCK, VOID, categories(PHRASING, EMBEDDED, INTERACTIVE, FORM_ASSOCIATED, FLOW), contexts(PHRASING_CTX), "Image"),
        element("input", INLINE_BLOCK, VOID, categories(PHRASING, INTERACTIVE, FORM_ASSOCIATED, FLOW), contexts(PHRASING_CTX), "Form input"),
        element("ins", INLINE, CONTAINER, categories(PHRASING, FLOW, TRANSPARENT), contexts(PHRASING_CTX, FLOW_CTX), "Inserted text"),
        element("kbd", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Keyboard input"),
        element("label", INLINE, CONTAINER, categories(PHRASING, INTERACTIVE, FORM_ASSOCIATED, FLOW), contexts(PHRASING_CTX), "Form label"),
        element("legend", BLOCK, CONTAINER, categories(), contexts(FIELDSET_CTX), "Fieldset legend"),
        element("li", BLOCK, CONTAINER, categories(), contexts(UL_CTX, OL_CTX), "List item"),
        element("link", NONE, VOID, categories(METADATA, PHRASING, FLOW), contexts(HEAD_CTX, PHRASING_CTX), "External resource link"),
        element("main", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Main content"),
        element("map", INLINE, CONTAINER, categories(PHRASING, FLOW, TRANSPARENT), contexts(PHRASING_CTX), "Image map"),
        element("mark", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Marked text"),
        element("math", INLINE_BLOCK, CONTAINER, categories(PHRASING, EMBEDDED, FLOW), contexts(PHRASING_CTX), "MathML math"),
        element("menu", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Menu"),
        element("meta", NONE, VOID, categories(METADATA), contexts(HEAD_CTX), "Metadata"),
        element("meter", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Scalar measurement"),
        element("nav", BLOCK, CONTAINER, categories(FLOW, SECTIONING), contexts(FLOW_CTX), "Navigation"),
        element("noscript", INLINE, CONTAINER, categories(METADATA, PHRASING, FLOW), contexts(HEAD_CTX, PHRASING_CTX), "No script fallback"),
        element("object", INLINE_BLOCK, CONTAINER, categories(PHRASING, EMBEDDED, INTERACTIVE, FORM_ASSOCIATED, FLOW), contexts(PHRASING_CTX), "Generic object"),
        element("ol", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Ordered list"),
        element("optgroup", NONE, CONTAINER, categories(), contexts(SELECT_CTX), "Option group"),
        element("option", NONE, CONTAINER, categories(), contexts(SELECT_CTX, DATALIST_CTX), "Select option"),
        element("output", INLINE, CONTAINER, categories(PHRASING, FORM_ASSOCIATED, FLOW), contexts(PHRASING_CTX), "Form output"),
        element("p", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Paragraph"),
        element("picture", INLINE_BLOCK, CONTAINER, categories(PHRASING, EMBEDDED, FLOW), contexts(PHRASING_CTX), "Responsive image"),
        element("pre", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Preformatted text"),
        element("progress", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Progress indicator"),
        element("q", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Inline quotation"),
        element("rp", INLINE, CONTAINER, categories(), contexts(RUBY_CTX), "Ruby parenthesis"),
        element("rt", INLINE, CONTAINER, categories(), contexts(RUBY_CTX), "Ruby text"),
        element("ruby", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Ruby annotation"),
        element("s", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Strikethrough"),
        element("samp", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Sample output"),
        element("script", NONE, RAW_TEXT, categories(METADATA, PHRASING, FLOW, SCRIPT_SUPPORTING), contexts(HEAD_CTX, PHRASING_CTX), "Script"),
        element("search", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Search"),
        element("section", BLOCK, CONTAINER, categories(FLOW, SECTIONING), contexts(FLOW_CTX), "Document section"),
        element("select", INLINE_BLOCK, CONTAINER, categories(PHRASING, INTERACTIVE, FORM_ASSOCIATED, FLOW), contexts(PHRASING_CTX), "Select control"),
        element("slot", INLINE, CONTAINER, categories(PHRASING, FLOW, TRANSPARENT), contexts(PHRASING_CTX), "Shadow DOM slot"),
        element("small", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Small text"),
        element("source", NONE, VOID, categories(), contexts(PICTURE_CTX, AUDIO_CTX, VIDEO_CTX), "Media source"),
        element("span", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Generic inline"),
        element("strong", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Strong importance"),
        element("style", NONE, RAW_TEXT, categories(METADATA), contexts(HEAD_CTX), "Style information"),
        element("sub", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Subscript"),
        element("summary", BLOCK, CONTAINER, categories(), contexts(DETAILS_CTX), "Details summary"),
        element("sup", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Superscript"),
        element("svg", INLINE_BLOCK, CONTAINER, categories(PHRASING, EMBEDDED, FLOW), contexts(PHRASING_CTX), "SVG graphics"),
        element("table", TABLE, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Table"),
        element("tbody", TABLE, CONTAINER, categories(), contexts(TABLE_CTX), "Table body"),
        element("td", TABLE, CONTAINER, categories(), contexts(TR_CTX), "Table cell"),
        element("template", NONE, CONTAINER, categories(METADATA, PHRASING, FLOW, SCRIPT_SUPPORTING), contexts(HEAD_CTX, PHRASING_CTX), "Content template"),
        element("textarea", INLINE_BLOCK, ESCAPABLE_RAW_TEXT, categories(PHRASING, INTERACTIVE, FORM_ASSOCIATED, FLOW), contexts(PHRASING_CTX), "Text area"),
        element("tfoot", TABLE, CONTAINER, categories(), contexts(TABLE_CTX), "Table footer"),
        element("th", TABLE, CONTAINER, categories(), contexts(TR_CTX), "Table header cell"),
        element("thead", TABLE, CONTAINER, categories(), contexts(TABLE_CTX), "Table header"),
        element("time", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Date/time"),
        element("title", NONE, ESCAPABLE_RAW_TEXT, categories(METADATA), contexts(HEAD_CTX), "Document title"),
        element("tr", TABLE, CONTAINER, categories(), contexts(TABLE_CTX), "Table row"),
        element("track", NONE, VOID, categories(), contexts(AUDIO_CTX, VIDEO_CTX), "Media track"),
        element("u", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Underlined text"),
        element("ul", BLOCK, CONTAINER, categories(FLOW), contexts(FLOW_CTX), "Unordered list"),
        element("var", INLINE, CONTAINER, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Variable"),
        element("video", INLINE_BLOCK, CONTAINER, categories(PHRASING, EMBEDDED, INTERACTIVE, FLOW), contexts(PHRASING_CTX), "Video content"),
        element("wbr", INLINE, VOID, categories(PHRASING, FLOW), contexts(PHRASING_CTX), "Line break opportunity")
    );
}