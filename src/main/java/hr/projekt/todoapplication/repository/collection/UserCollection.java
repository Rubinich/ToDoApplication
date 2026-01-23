package hr.projekt.todoapplication.repository.collection;

import hr.projekt.todoapplication.model.user.User;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class UserCollection implements Serializable {
    private Set<User> users = new HashSet<>();
    public UserCollection() {
        /*
        ovo je za serijalizaciju
         */
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}