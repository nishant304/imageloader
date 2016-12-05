package com.example.nishant.imageloader.network.parser;

import java.util.List;

/**
 * Created by nishant on 11/30/2016.
 */

public interface Parser {
     <T> T parse(String json, Class<T> tClass);
     <T> List<T> toList(String json, Class<T> tClass);
}
