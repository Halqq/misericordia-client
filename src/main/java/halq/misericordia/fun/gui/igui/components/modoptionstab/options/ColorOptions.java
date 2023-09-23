package halq.misericordia.fun.gui.igui.components.modoptionstab.options;

public enum ColorOptions {
    COLORSYNC("ColorSync", false),
    RAINBOW("Rainbow", false),
    RAINBOWSPEED("RainbowSpeed", "0", false),

    RED("Red", "0", false),
    GREEN("Green", "0", false),
    BLUE("Blue", "0", false),
    ALPHA("Alpha", "0", false);

    public final String name;
    public boolean bool = false;
    public String value = "1";
    public boolean typing = false;

    ColorOptions(String name) {
        this.name = name;
    }

    ColorOptions(String name, String value, boolean typing) {
        this.name = name;
        this.value = value;
    }

    ColorOptions(String name, boolean bool) {
        this.name = name;
        this.bool = bool;
    }
}
