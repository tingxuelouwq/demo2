package com.example.demo2.user.controller;

import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * kevin<br/>
 * 2020/11/23 17:31<br/>
 */
public class UserAddModel {

    private LocalDateTime regtime;

    public LocalDateTime getRegtime() {
        return regtime;
    }

    public void setRegtime(LocalDateTime regtime) {
        this.regtime = regtime;
    }

    public static void main(String[] args) {
        long timestamp = 1616050028;
        long nonce = 2125040331;
        String secretKey = "hg123456";
        StringBuilder builder = new StringBuilder();
        char[] originSignature = builder.append(timestamp)
                .append(nonce)
                .append(secretKey)
                .append("HGNETPB")
                .toString().toCharArray();
        Arrays.sort(originSignature);
        String sortSignature = new String(originSignature);
        String signature = DigestUtils.md5Hex(sortSignature);
        System.out.println(sortSignature);
        System.out.println(signature.toUpperCase());
    }
}
