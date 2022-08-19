package com.example.authapp.utils;

import java.util.Locale;
public class StringManipulation {
    public static String expandUserName(String username){
        return username.replace(".","");
    }
    public static String compressName(String name){
        return name.toLowerCase();
    }
}
