package org.fizz_buzz;

import org.fizz_buzz.model.Repository;
import org.fizz_buzz.model.SQLiteRepository;

public class Main {
    public static void main(String[] args) {
        Repository repository = new SQLiteRepository();
        try{
            repository.putUser();
            System.out.println(repository.getUsers());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}