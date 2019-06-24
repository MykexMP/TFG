package model.flag;

public class Flag {
    private String flag;
    private float value;
    private boolean active;

    public Flag (String flag, float value, boolean active){
        this.flag = flag;
        this.value = value;
        this.active = active;
    }

    public Flag (String flag, float value){
        this(flag,value,false);
    }

    public Flag (String flag){
        this(flag,0,false);
    }

    public String getFlag() {
        return flag;
    }
    public float getValue() { return value; }
    public boolean isActive() { return active; }

    public void setFlag(String flag) {
        this.flag = flag;
    }
    public void setValue(float value) {
        this.value = value;
    }
    public void setActive(boolean active) { this.active = active; }
}
