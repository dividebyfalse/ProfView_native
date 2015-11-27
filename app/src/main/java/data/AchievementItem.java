package data;

/**
 * Created by penpen on 24.11.15.
 */
public class AchievementItem {
    private String name, category, date, dateofadd;
    private int id, isprem, isstip;

    public AchievementItem(int id, String name, String category, String date, String dateofadd, int isprem, int isstip) {
        super();
        this.id = id;
        this.name = name;
        this.category = category;
        this.date = date;
        this.dateofadd = dateofadd;
        this.isprem = isprem;
        this.isstip = isstip;
    }

    public AchievementItem() {
    }

    public int getIsprem() {
        return isprem;
    }

    public String getCategory() {
        return category;
    }

    public int getIsstip() {
        return isstip;
    }

    public String getDate() {
        return date;
    }

    public String getDateofadd() {
        return dateofadd;
    }

    public String getName() {
        return name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDateofadd(String dateofadd) {
        this.dateofadd = dateofadd;
    }

    public void setIsprem(int isprem) {
        this.isprem = isprem;
    }

    public void setIsstip(int isstip) {
        this.isstip = isstip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
