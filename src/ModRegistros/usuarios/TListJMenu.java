/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModRegistros.usuarios;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author ANDRES
 */
public class TListJMenu extends AbstractListModel{
    
    List<Object> list;
    List<Object> filteredList;
    String lastFilter = "";
    
    public TListJMenu(){
        list = new ArrayList<Object>();
        filteredList = new ArrayList<Object>();
    }
    
    public void addElement(Object element){
        list.add(element);
        filter(lastFilter);
    }
    
    public void removeElement(int index){
        list.remove(index);
        filter(lastFilter);
    }
    
    public void removeAll(){
        list.clear();
        filter(lastFilter);
    }
    
    public int getSize(){
        return filteredList.size();
    }
    
    public Object getElementAt(int index){
        Object returnValue;
        if(index < filteredList.size()){
            returnValue = filteredList.get(index);
        }else{
            returnValue = null;
        }
        return returnValue;
    }
    
    void filter(String search){
        filteredList.clear();
        for(Object element: list){
            if(element.toString().indexOf(search, 0) != -1){
                filteredList.add(element);
            }
        }
        fireContentsChanged(this, 0, getSize());
    }
}
