package test1;

import java.util.Arrays;

/**
 * Created by cathym on 2015/6/8.
 */
public class StringArray {

    public static void main(String args[]) {
        String haha = "https://10.208.128.232/api/userfw/v1/post-entry";
        String[] hahaha = haha.split("/");
        String[] ip = hahaha[2].split(":");
        System.out.println(Arrays.toString(hahaha).replaceAll("[\\[\\]\\s,]", ""));
    }

}
