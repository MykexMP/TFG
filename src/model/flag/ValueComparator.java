package model.flag;

import java.util.Comparator;

public class ValueComparator implements Comparator<Flag> {
    @Override
    public int compare(Flag o1, Flag o2) {
        return Float.compare(o1.getValue(), o2.getValue());
    }
}
