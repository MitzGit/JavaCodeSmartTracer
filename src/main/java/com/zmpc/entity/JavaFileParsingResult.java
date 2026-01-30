package com.zmpc.entity;

import com.zmpc.common.Str;

public class JavaFileParsingResult {

    private Str text;

    private Str packageName;

    public JavaFileParsingResult(Str text) {
        this.text = text;
    }

    public Str text() {
        return text;
    }

    public Str getText() {
        return text;
    }

    public void setText(Str text) {
        this.text = text;
    }

}
