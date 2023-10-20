package com.ada.ada_meethem.modelo;

import java.util.ArrayList;
import java.util.List;

public class Plan {
    private String title;
    private Group gruop;
    private Person creator;
    private List<Person> enlisted;
    private boolean open;

    public Plan(String title, Group gruop, Person creator) {
        this.title = title;
        this.gruop = gruop;
        this.creator = creator;
        this.enlisted = new ArrayList<Person>();
        this.open = true;
    }


    public Plan(String title, Group gruop, Person creator, List<Person> enlisted) {
        this.title = title;
        this.gruop = gruop;
        this.creator = creator;
        this.enlisted = enlisted;
        this.open = true;
    }

    public void addToPlan(Person person) {
        if(!enlisted.contains(person) && open) enlisted.add(person);
    }

    public void removeFromPlan(Person person) {
        if(enlisted.contains(person)) enlisted.remove(person);
    }

    public void closePlan(Person person) {
        if(person.equals(creator)) this.open = false;
    }

    public void openPlan(Person person) {
        if(person.equals(creator)) this.open = true;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGruop(Group gruop) {
        this.gruop = gruop;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }

    public void setEnlisted(List<Person> enlisted) {
        this.enlisted = enlisted;
    }

    public String getTitle() {
        return title;
    }

    public Group getGruop() {
        return gruop;
    }

    public Person getCreator() {
        return creator;
    }

    public List<Person> getEnlisted() {
        return new ArrayList<>(enlisted);
    }

    public List<Person> _getEnlisted() {
        return enlisted;
    }
}
