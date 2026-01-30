package com.zmpc.common;

public class StrBuilder {

    private StringBuilder sb = new StringBuilder();

    public String val() {
        return sb.toString();
    }

    public String str() {
        return sb.toString();
    }

    public String toString() {
        return sb.toString();
    }

    public StrBuilder add(String str) {
        sb.append(str);
        return this;
    }

    public StrBuilder add(Str str) {
        sb.append(str.val());
        return this;
    }
}
