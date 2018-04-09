package com.mulkearn.kevin.pixelcolor;

public class Colors {

    private int _id;
    private String _colorname;

    public Colors() {
    }

    public Colors(String colorname) {
        this._colorname = colorname;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_colorname() {
        return _colorname;
    }

    public void set_colorname(String _colorname) {
        this._colorname = _colorname;
    }

}

