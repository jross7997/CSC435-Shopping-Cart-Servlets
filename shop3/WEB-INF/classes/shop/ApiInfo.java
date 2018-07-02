/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shop;

/**
 *
 * @author Justin
 */
public class ApiInfo {
    //Information for each of the "API" objects. This was used to simplify printing the info page
    private String operator = null;
    private String URI = null;
    private String description = null;
    
    public ApiInfo(String o, String u, String d){
        operator = o;
        URI = u;
        description = d;
    }
    
    public String getOperator(){
        return operator;
    }
    public String getURI(){
        return URI;
    }
    public String getDescription(){
        return description;
    }
    
    public String toString(){
        return operator +URI + description;
    }
    
}
