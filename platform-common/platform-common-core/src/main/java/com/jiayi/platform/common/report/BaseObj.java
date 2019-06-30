package com.jiayi.platform.common.report;

public class BaseObj implements Comparable<BaseObj>{
    private long id;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(BaseObj another) {
        if(getId() > another.getId()){
            return 1;
        }
        else if( getId() == another.getId()){
            return 0;
        }else {
            return -1;
        }
    }
}
