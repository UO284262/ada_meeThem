package com.ada.ada_meethem.modelo;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private String groupName;
    private List<Person> members;
    private List<Person> admins;
    private Person creator;

    public Group(String groupName, Person creator) {
        this.groupName = groupName;
        this.creator = creator;
        this.members = new ArrayList<>();
        this.admins = new ArrayList<>();
        addMember(creator);
        setAsAdmin(creator);
    }

    public Group(String groupName, Person creator, List<Person> admins, List<Person> members) {
        this.groupName = groupName;
        this.creator = creator;
        this.members = members;
        this.admins = admins;
    }

    public void addMember(Person person) {
        if(!members.contains(person)) this.members.add(person);
    }

    public void setAsAdmin(Person person) {
        if(members.contains(person) && !admins.contains(person))
            admins.add(person); members.remove(person);
    }

    public void removeFromAdmins(Person person) {
        if(!members.contains(person) && admins.contains(person))
            admins.remove(person); members.add(person);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Person> getMembers() {
        return new ArrayList<>(members);
    }

    public List<Person> _getMembers() {
        return members;
    }

    public List<Person> getAdmins() {
        return new ArrayList<>(admins);
    }

    public List<Person> _getAdmins() {
        return admins;
    }

    public Person getCreator() {
        return creator;
    }

    public void setMembers(List<Person> members) {
        this.members = members;
    }
    public void setAdmins(List<Person> admins) {
        this.admins = admins;
    }
}
