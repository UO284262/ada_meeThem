package com.ada.ada_meethem.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Plan implements Serializable {

    private String imageUrl;
    private String title;
    private Group group;
    private Person creator;
    private List<Person> enlisted;
    private boolean open;
    private int maxPeople;

    private String planId;

    public Plan(String title, Group group, Person creator, int maxPeople, String imageUrl) {
        this.title = title;
        this.group = group;
        this.creator = creator;
        this.enlisted = new ArrayList<>();
        this.open = true;
        this.maxPeople = maxPeople;
        this.imageUrl = imageUrl;
        this.planId = UUID.randomUUID().toString();
    }


    public Plan(String title, Group group, Person creator, int maxPeople, String imageUrl, List<Person> enlisted) {
        this.title = title;
        this.group = group;
        this.creator = creator;
        this.enlisted = enlisted;
        this.open = true;
        this.maxPeople = maxPeople;
        this.imageUrl = imageUrl;
        this.planId = UUID.randomUUID().toString();
    }

    public void addToPlan(Person person) {
        if(!enlisted.contains(person) && open && enlisted.size() <= maxPeople)
            enlisted.add(person);
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

    public void setGroup(Group group) {
        this.group = group;
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

    public Group getGroup() {
        return group;
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

    public int getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPlanId() {
        return this.planId;
    }

    public void setPlanId(String id) {
        this.planId = id;
    }
}
