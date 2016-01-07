package data;

/**
 * Created by penpen on 07.01.16.
 */
public class MenuItem {
    private Boolean selected;
    private String description;

    public MenuItem(String description) {
        this.description = description;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
