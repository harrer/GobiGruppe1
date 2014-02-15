package de.lmu.ifi.bio.splicing.genome;

/**
 * Created by Carsten Uhlig on 15.02.14.
 * name = normaler Name von Pattern (z.B.: EGF_2)
 * id = Prosite Pattern identifier (z.B.: P00023)
 * pattern = Pattern als [XSDS]-X....
 * link = link zu mehr informationen
 * description = genauere Beschreibung
 */
public class Pattern {
    private String name, id, pattern, link, description;
    private boolean is_profile = false;

    public Pattern(String id, String name, String pattern) {
        this.name = name;
        this.id = id;
        this.pattern = pattern;
    }

    public Pattern(String name, String id, String pattern, String link, String description) {
        this.name = name;
        this.id = id;
        this.pattern = pattern;
        this.link = link;
        this.description = description;
    }

    public Pattern(String name, String id, String pattern, String description) {
        this.name = name;
        this.id = id;
        this.pattern = pattern;
        this.description = description;
    }

    public Pattern(String name, String id, boolean is_profile) {
        this.name = name;
        this.id = id;
        this.is_profile = is_profile;
    }

    public Pattern(String name, String id, boolean is_profile, String description) {
        this.name = name;
        this.id = id;
        this.is_profile = is_profile;
        this.description = description;
    }

    //TODO Konstruktor mit Link und Profile true

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean hasDescription() {
        if (description == null) return false;
        if (description.isEmpty()) return false;
        return true;
    }

    public boolean hasLink() {
        if (link == null) return false;
        if (link.isEmpty()) return false;
        return true;
    }

    public boolean isProfile() {
        return is_profile;
    }

}
