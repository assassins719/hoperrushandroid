package com.hoperrush.pojo;

/**
 * Casperon Technology on 1/5/2016.
 */
public class CategoryDetailPojo
{
    private String cat_id = "", cat_name = "", cat_image = "", icon_normal = "", hasChild = "";
    private boolean isAddressSelected;

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getCat_image() {
        return cat_image;
    }

    public void setCat_image(String cat_image) {
        this.cat_image = cat_image;
    }

    public String getIcon_normal() {
        return icon_normal;
    }

    public void setIcon_normal(String icon_normal) {
        this.icon_normal = icon_normal;
    }

    public String getHasChild() {
        return hasChild;
    }

    public void setHasChild(String hasChild) {
        this.hasChild = hasChild;
    }
    public void setcategorySelected(boolean addressSelected) {
        isAddressSelected = addressSelected;
    }
    public boolean isCategorySelected() {
        return isAddressSelected;
    }
}
