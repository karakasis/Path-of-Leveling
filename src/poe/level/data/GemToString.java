/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.data;

import java.util.HashMap;

import javafx.util.StringConverter;

/**
 *
 * @author Christos
 */
public class GemToString<T> extends StringConverter<T> {

    private HashMap<String, T> map = new HashMap<>();

    @Override
    public T fromString(String string) {
        if (!map.containsKey(string)) {
            return null;
        }
        return map.get(string);
    }

    @Override
    public String toString(T t) {
        if (t != null) {
            Gem g = (Gem) t;
            String str = g.getGemName();
            map.put(str, t);
            return str;
        } else {
            return "";
        }
    }
}
