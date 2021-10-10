package com.company;
import java.util.*;

class Portal
{
    LinkedList<Hospital> hosp;
    LinkedList<Citizen> ctz;
    LinkedList<Vaccine> vaccines;
    Portal()
    {
        hosp = new LinkedList<>();
        ctz = new LinkedList<>();
        vaccines = new LinkedList<>();
    }


    void addVaccine(String Name, int num_doses, int gap)
    {
        for(Vaccine vaccine: vaccines)
        {
            if(vaccine.name.equals(Name))
            {
                System.out.println("This vaccine is already registered.");
                return;
            }
        }
        Vaccine new_vac = new Vaccine(Name, num_doses, gap);
        vaccines.add(new_vac);
        System.out.println("Vaccine Name: " + Name + ", Number of Doses: " + num_doses + ", Gap Between Doses: " + gap);
    }


    boolean regHospital(String Name, int hosp_ID, int pincode)
    {
        for(Hospital hospital: hosp)
        {
            if(hospital.name.equals(Name) && hospital.pincode==pincode)
            {
                System.out.println("This Hospital is already registered.");
                return false;
            }
        }
        String id = String.valueOf(hosp_ID);
        StringBuilder temp = new StringBuilder();
        int len = id.length();
        while(6-len>0)
        {
            temp.append('0');
            len++;
        }
        temp.append(id);
        id = temp.toString();
        Hospital new_hosp = new Hospital(Name, id, pincode);
        hosp.add(new_hosp);
        System.out.println("Hospital Name: " + Name + ", PinCode: " + pincode + ", Unique ID: " + id);
        return true;
    }


    boolean regCitizen(String name, int age, String ID)
    {
        for(Citizen citizen: ctz)
        {
            if(citizen.ID.equals(ID))
            {
                System.out.println("This Citizen is already registered.");
                return false;
            }
        }
        if(age>18)
        {
            Citizen new_citizen = new Citizen(name, age, ID);
            ctz.add(new_citizen);
        }
        System.out.println("Citizen Name: " + name + ", Age: " + age + ", Unique ID:" + ID);
        if(age<=18)
            System.out.println("Only above 18 are allowed");
        return true;
    }
    void addSlot(String ID, int day, int qty, String vac_name)
    {
        if(vaccines == null)
        {
            System.out.println("No Vaccines have been registered on the website. Please Try again later");
            return ;
        }
        for(Hospital hospital: hosp)
        {
            if(ID.equals(hospital.hosp_ID))
            {
                for(Vaccine vaccine: vaccines) {
                    if (vaccine.name.equals(vac_name)) {
                        Slot new_slot = new Slot(day, vaccine, qty);
                        hospital.slot.add(new_slot);
                        System.out.println("Slot added by Hospital: " + ID + " for Day: " + day + ", Available Quantity: " + qty + " of Vaccine " + vac_name);
                        return;
                    }
                }
            }
        }
        System.out.println("Hospital ID " + ID + " not found!!!");
    }


    void bookSlot_ByArea(String patientID)
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter PinCode: ");
        int pincode = sc.nextInt();
        LinkedList<Hospital> temp_hosp = new LinkedList<>();

        for(Hospital hptl: hosp)
        {
            if(hptl.pincode == pincode)
            {
                temp_hosp.add(hptl);
                System.out.println(hptl.hosp_ID + " " + hptl.name);
            }
        }
        System.out.println("Enter hospital id: ");
        String ID = sc.next();

        for(Citizen cit: ctz)
        {
            if(cit.ID.equals(patientID))
            {
                for(Hospital hptl: temp_hosp)
                {
                    if(hptl.hosp_ID.equals(ID))
                    {
                        if(hptl.slot==null)
                        {
                            System.out.println("No slots available");
                            return ;
                        }
                        else
                        {
                            int i=0;
                            boolean found = false;
                            for(Slot slot: hptl.slot)
                            {
                                if(cit.due_date==-1 || slot.day >= cit.due_date)
                                {
                                    System.out.println(i + "-> Day: " + slot.day + " Available" + " Qty:" + slot.quantity + " Vaccine: " + slot.vac.name);
                                    found = true;
                                }
                                i++;
                            }
                            if(!found)
                            {
                                System.out.println("No slots available");
                                return ;
                            }
                            System.out.print("Choose Slot: ");
                            int idx = sc.nextInt();
                            Slot exact_slot = hptl.slot.get(idx);

                            --exact_slot.quantity;
                            if(exact_slot.quantity==0)
                            {
                                hptl.slot.remove(idx);
                            }

                            System.out.println(cit.name + " vaccinated with " + exact_slot.vac.name);
                            cit.vac_status++;
                            if(cit.vac == null)
                            {
                                cit.vac = exact_slot.vac;
                                cit.due_date = exact_slot.day + exact_slot.vac.gap;
                            }
                            else
                            {
                                if(cit.vac_status < exact_slot.vac.num_doses)
                                {
                                    cit.due_date = exact_slot.day + exact_slot.vac.gap;
                                }
                                else if(exact_slot.vac.num_doses == cit.vac_status)
                                {
                                    cit.vac = exact_slot.vac;
                                    cit.due_date = 0;
                                }
                            }
                            return ;
                        }
                    }
                }
            }
        }
        System.out.println("Citizen with ID " + patientID + " not found!!!");
    }


    void bookSlot_ByVaccine(String patientID)
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Vaccine name: ");
        String vac_name = sc.next();
        LinkedList<Hospital> temp_hosp = new LinkedList<>();

        for(Hospital hptl: hosp)
        {
            for(Slot slot: hptl.slot)
            {
                if(slot.vac.name.equals(vac_name))
                {
                    temp_hosp.add(hptl);
                    System.out.println(hptl.hosp_ID + " " + hptl.name);
                }
            }
        }

        System.out.println("Enter hospital id: ");
        String ID = sc.next();

        for(Citizen cit: ctz)
        {
            if(cit.ID.equals(patientID))
            {
                for(Hospital hptl: temp_hosp)
                {
                    if(hptl.hosp_ID.equals(ID))
                    {
                        if(hptl.slot==null)
                        {
                            System.out.println("No slots available");
                            return ;
                        }
                        else
                        {
                            int i=0;
                            boolean found = false;
                            for(Slot slot: hptl.slot)
                            {
                                if(cit.due_date == -1 || slot.day >= cit.due_date)
                                {
                                    System.out.println(i + "-> Day: " + slot.day + " Available" + " Qty:" + slot.quantity + " Vaccine: " + slot.vac.name);
                                    found = true;
                                }
                                i++;
                            }
                            if(!found)
                            {
                                System.out.println("No slots available");
                                return ;
                            }
                            System.out.print("Choose Slot: ");
                            int idx = sc.nextInt();
                            Slot exact_slot = hptl.slot.get(idx);

                            --exact_slot.quantity;
                            if(exact_slot.quantity==0)
                            {
                                hptl.slot.remove(idx);
                            }

                            System.out.println(cit.name + " vaccinated with " + exact_slot.vac.name);
                            cit.vac_status++;
                            if(cit.vac == null)
                            {
                                cit.vac = exact_slot.vac;
                                cit.due_date = exact_slot.day + exact_slot.vac.gap;
                            }
                            else
                            {
                                if(cit.vac_status < exact_slot.vac.num_doses)
                                {
                                    cit.due_date = exact_slot.day + exact_slot.vac.gap;
                                }
                                else if(exact_slot.vac.num_doses == cit.vac_status)
                                {
                                    cit.vac = exact_slot.vac;
                                    cit.due_date = 0;
                                }
                            }
                            return ;
                        }
                    }
                }
            }
        }
        System.out.println("Citizen with ID " + patientID + " not found!!!");
    }


}

class Hospital{
    int pincode;
    String hosp_ID, name;
    LinkedList<Slot> slot;

    Hospital(String name, String hosp_ID, int pincode)
    {
        this.name = name;
        this.hosp_ID = hosp_ID;
        this.pincode = pincode;
        slot = new LinkedList<>();
    }

}

class Citizen{
    String name, ID;
    int age, vac_status, due_date;
    Vaccine vac;
    Citizen(String name, int age, String ID)
    {
        this.name = name;
        this.age = age;
        this.ID = ID;
        this.vac_status = 0;
        vac = null;
        due_date = -1;
    }

}

class Slot{
    int day, quantity;
    Vaccine vac;
    Slot(int day, Vaccine vac, int quantity)
    {
        this.day = day;
        this.vac = vac;
        this.quantity = quantity;
    }
}

class Vaccine{
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
