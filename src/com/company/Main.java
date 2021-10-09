package com.company;
import java.util.*;

public class Portal{
    LinkedList<Hospital> hosp = new LinkedList<Hospital>();
    LinkedList<Citizen> ctz = new LinkedList<Citizen>();
}

public class Hospital{
    static int hosp_ID = 0;
    String area;
    LinkedList<Slot> slot;
    Hospital(String area)
    {
        this.area = area;
        slot = new LinkedList<Slot>();
    }

}

public class Citizen{
    String name;
    int age, ID, vac_status;
    Citizen(String name, int age, int ID)
    {
        this.name = name;
        this.age = age;
        this.ID = ID;
        this.vac_status = 0;
    }
}

public class Slot{
    int day, quantity;
    Vaccine vac;
    Slot(int day, Vaccine vac, int quantity)
    {
        this.day = day;
        this.vac = vac;
        this.quantity = quantity;
    }
}

public class Vaccine{
    String name;
    int num_doses, gap;
    Vaccine(String name, int num_doses, int gap)
    {
        this.name = name;
        this.num_doses = num_doses;
        this.gap = gap;
    }
}
public class Main {

    public static void main(String[] args) {
	// write your code here
        System.out.println("Hello World");
        System.out.println("Hello all of the World");
    }
}
