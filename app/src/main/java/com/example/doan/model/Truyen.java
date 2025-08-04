package com.example.doan.model;

public class Truyen {
    private String id;
    private String tenTruyen;
    private String moTa;

    public Truyen() {}

    public Truyen(String id, String tenTruyen, String moTa) {
        this.id = id;
        this.tenTruyen = tenTruyen;
        this.moTa = moTa;
    }

    public String getId() {
        return id;
    }

    public String getTenTruyen() {
        return tenTruyen;
    }

    public String getMoTa() {
        return moTa;
    }
}

