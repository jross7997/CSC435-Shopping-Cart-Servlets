/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shop;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Justin
 */
//Just used for the API
public class FoodInfo {

    @JsonProperty("_index")
    private String index;

    @JsonProperty("_type")
    private String type;

    @JsonProperty("_id")
    private String id;

    @JsonProperty("_score")
    private int score;

    @JsonProperty("fields")
    private Fields field;

    public String getIndex() {
        return index;
    }

    public void setIndex(String i) {
        index = i;
    }

    public String getType() {
        return type;
    }

    public void setType(String i) {
        type = i;
    }

    public String getId() {
        return id;
    }

    public void setId(String i) {
        id = i;
    }

    public int getScore() {
        return score;
    }

    public void setBrandName(int i) {
        score = i;
    }

    public Fields getFields() {
        return field;
    }

    public void setFields(Fields i) {
        field = i;
    }
    
    public String toString(){
        return index + ":" + type;
    }

}
