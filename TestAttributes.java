import luvml.HtmlAttributeData;

public class TestAttributes {
    public static void main(String[] args) {
        long typeCount = HtmlAttributeData.ALL_ATTRIBUTES_LIST.stream()
            .filter(a -> a.attribute().equals("type"))
            .count();
        System.out.println("Number of 'type' attributes in list: " + typeCount);

        HtmlAttributeData.ALL_ATTRIBUTES_LIST.stream()
            .filter(a -> a.attribute().equals("type"))
            .forEach(a -> System.out.println("  - Scope: " + a.scope() + ", Enum values: " + a.enumValues()));
    }
}
