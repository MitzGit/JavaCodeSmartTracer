package com.zmpc.common;

import java.util.Objects;

public class Str {

    private String value;

    public Str(String value) {
        this.value = value;
    }

    public String val() {
        return value;
    }

    public String str() {
        return value;
    }

    public static Str str(String s) {
        return new Str(s);
    }

    public static String str(Str s) {
        return s.value;
    }

    public void set(String value) {
        this.value = value;
    }

    public void set(Str str) {
        this.value = str.value;
    }

    public void set(StrBuilder strBuilder) {
        this.value = strBuilder.str();
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean eq(Str str) {
        if (this.value == null || str == null) return false;
        return Objects.equals(value, str.value);
    }

    public int len() {
        return value.length();
    }

    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Str str = (Str) o;
        return Objects.equals(value, str.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean empt() {
        return value.isEmpty();
    }

    public boolean blank() {
        return value.isBlank();
    }

    public static int indx(Str text, Str str) {
        return text.indx(str);
    }

    public static int indx(Str text, String str) {
        return indx(text, str, 0);
    }

    public static int indx(Str text, char ch) {
        return text.value.indexOf(ch);
    }

    public static int indx(Str text, char ch, int fromIndex) {
        return text.value.indexOf(ch, fromIndex);
    }

    public static int indx(Str text, Str str, int fromIndex) {
        return text.value.indexOf(str.value, fromIndex);
    }

    public static int indx(Str text, String str, int fromIndex) {
        return text.value.indexOf(str, fromIndex);
    }

    public int indx(String str) {
        return value.indexOf(str);
    }

    public int indx(Str str) {
        return value.indexOf(str.value);
    }

    public static int lastIndx(Str text, String str) {
        return text.lastIndx(str);
    }

    public int lastIndx(String str) {
        return value.lastIndexOf(str);
    }

    public int lastIndx(Str str) {
        return value.lastIndexOf(str.value);
    }

    public static Str subs(Str text, int beginIndex) {
        return text.substring(beginIndex);
    }

    public static Str subs(Str text, int beginIndex, int endIndex) {
        return text.substring(beginIndex, endIndex);
    }

    public Str subs(int beginIndex) {
        return substring(beginIndex);
    }

    public Str subs(int beginIndex, int endIndex) {
        return substring(beginIndex, endIndex);
    }

    public Str substring(int beginIndex) {
        return new Str(value.substring(beginIndex));
    }

    public Str substring(int beginIndex, int endIndex) {
        return new Str(value.substring(beginIndex, endIndex));
    }

    public Str tr() {
        return new Str(value.trim());
    }

    public Str plus(Str str) {
        return new Str(value.concat(str.value));
    }

    public void replace(CharSequence target, CharSequence replacement) {
        value = value.replace(target, replacement);
    }

    public String[] split(String regex) {
        return value.split(regex);
    }
}
