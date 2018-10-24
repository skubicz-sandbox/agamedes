package com.kubicz.mavenexecutor.window;

import com.intellij.util.xmlb.annotations.Property;
import com.intellij.util.xmlb.annotations.Transient;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class History {

    @Property
    private int maxItemsCount;

    @Property
    private List<String> items;

    protected History () {
        this(20, new ArrayList<>());
    }

    public History(int maxItemsCount, List<String> items) {
        this.maxItemsCount = maxItemsCount;
        this.items = items;
    }

    public void add(String item) {
        if(StringUtils.isBlank(item)) {
            return;
        }

        if(!items.contains(item)) {
            items.add(0, item);
        }
        while (items.size() > maxItemsCount) {
            items.remove(maxItemsCount);
        }
    }

    public List<String> getItems() {
        return items;
    }

    public String [] asArray() {
        return items.toArray(new String[0]);
    }

}