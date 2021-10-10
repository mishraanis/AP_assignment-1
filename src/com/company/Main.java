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


    void regCitizen(String name, int age, String ID)
    {
        for(Citizen citizen: ctz)
        {
            if(citizen.ID.equals(ID))
            {
                System.out.println("This Citizen is already registered.");
                return;
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
    }


    void bookSlot_ByArea(String patientID)
    {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter PinCode: ");
        String pncd = sc.next();
        while(pncd.length() != 6)
        {
            System.out.println("Enter a valid PinCode!!");
            System.out.print("PinCode: ");
            pncd = sc.next();
        }
        int pincode = Integer.parseInt(pncd);
        LinkedList<Hospital> temp_hosp = new LinkedList<>();

        for(Hospital hptl: hosp)
        {
            if(hptl.pincode == pincode)
            {
                temp_hosp.add(hptl);
                System.out.println(hptl.hosp_ID + " " + hptl.name);
            }
        }
        System.out.print("Enter hospital id: ");
        String ID = sc.next();
        if(!isHospitalRegistered(ID))
        {
            System.out.println("Hospital ID " + ID + " not found!!!");
            return ;
        }
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
        System.out.println("Invalid PinCode");
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
        if(!isHospitalRegistered(ID))
        {
            System.out.println("Hospital ID " + ID + " not found!!!");
            return ;
        }
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
        System.out.println("Invalid Vaccine Name");
    }


    void display_slots(String ID)
    {
        for(Hospital hptl: hosp)
        {
            if(hptl.hosp_ID.equals(ID))
            {
                if(hptl.slot == null)
                {
                    System.out.println("No slots available");
                }
                else
                {
                    for(Slot slot: hptl.slot)
                    {
                        System.out.println("Day: " + slot.day + " Available" + " Qty:" + slot.quantity + " Vaccine: " + slot.vac.name);
                    }
                }
                return ;
            }
        }
        System.out.println("Hospital with ID " + ID + " not found!!!");
    }


    void vaccination_status(String patientID)
    {
        for(Citizen cit: ctz)
        {
            if(cit.ID.equals(patientID))
            {
                if(cit.vac_status == 0)
                {
                    System.out.println("Citizen REGISTERED");
                    return ;
                }
                else if(cit.vac_status < cit.vac.num_doses)
                    System.out.println("PARTIALLY VACCINATED");
                else
                    System.out.println("FULLY VACCINATED");
                System.out.println("Vaccine Given: " + cit.vac.name);
                System.out.println("Number of Doses given: " + cit.vac_status);
                if(cit.vac_status < cit.vac.num_doses)
                    System.out.println("Next Dose due date: " + cit.due_date);
                return ;
            }
        }
        System.out.println("Citizen with ID " + patientID + " not found!!!");
    }


    boolean isHospitalRegistered(String hosp_ID)
    {
        for(Hospital hptl: hosp)
        {
            if (hptl.hosp_ID.equals(hosp_ID))
                return true;
        }
        return false;
    }


    boolean isCitizenRegistered(String citizen_ID)
    {
        for(Citizen cit: ctz)
        {
            if (cit.ID.equals(citizen_ID))
                return true;
        }
        return false;
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

    static int num_hospitals = 1;

    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        Portal portal = new Portal();
        System.out.println("CoWin Portal initialized....");
        System.out.println("---------------------------------");
        System.out.println("""
                1. Add Vaccine
                2. Register Hospital
                3. Register Citizen
                4. Add Slot for Vaccination
                5. Book Slot for Vaccination
                6. List all slots for a hospital
                7. Check Vaccination Status
                8. Exit""");
        System.out.println("---------------------------------");
        System.out.println("Choose an option : ");
        int choice = sc.nextInt();
        while(choice != 8)
        {
            switch(choice)
            {
                case 1:
                    System.out.print("Vaccine Name: ");
                    String vaccine_name = sc.next();
                    System.out.print("Number of Doses: ");
                    int num_doses = sc.nextInt();
                    int gap=0;
                    if(num_doses > 1)
                    {
                        System.out.print("Gap Between Doses: ");
                        gap = sc.nextInt();
                    }
                    portal.addVaccine(vaccine_name, num_doses, gap);
                    break;

                case 2:
                    System.out.print("Hospital Name: ");
                    String hosp_name = sc.next();
                    System.out.print("PinCode: ");
                    String pincode = sc.next();
                    while (pincode.length() != 6) {
                        System.out.println("Enter a valid PinCode!!");
                        System.out.print("PinCode: ");
                        pincode = sc.next();
                    }
                    int pncd = Integer.parseInt(pincode);
                    if (portal.regHospital(hosp_name, num_hospitals, pncd))
                        num_hospitals++;
                    break;

                case 3:
                    System.out.print("Citizen Name: ");
                    String citizen_name = sc.next();
                    System.out.print("Age: ");
                    int age = sc.nextInt();
                    System.out.print("Unique ID(12 digits): ");
                    String cit_ID = sc.next();
                    while (cit_ID.length() != 12) {
                        System.out.println("Wrong Input. Try Again.");
                        System.out.print("Unique ID(12 digits): ");
                        cit_ID = sc.next();
                    }
                    portal.regCitizen(citizen_name, age, cit_ID);
                    break;

                case 4:
                    if(portal.vaccines.size()==0)
                    {
                        System.out.println("No Vaccines are registered yet. Try again Later!");
                        break;
                    }
                    System.out.print("Enter Hospital ID: ");
                    String hosp_ID = sc.next();
                    if(!portal.isHospitalRegistered(hosp_ID))
                    {
                        System.out.println("Hospital ID " + hosp_ID + " not found!!!");
                        break;
                    }
                    System.out.print("Enter number of Slots to be added: ");
                    int num_slots = sc.nextInt();
                    for (int i = 0; i < num_slots; i++) {
                        System.out.print("Enter Day Number: ");
                        int day = sc.nextInt();
                        System.out.print("Enter Quantity: ");
                        int qty = sc.nextInt();
                        System.out.println("Select Vaccine");
                        for (int j = 0; j < portal.vaccines.size(); j++) {
                            System.out.println(j + ". " + portal.vaccines.get(j).name);
                        }
                        int vac_idx = sc.nextInt();
                        portal.addSlot(hosp_ID, day, qty, portal.vaccines.get(vac_idx).name);
                    }
                    break;

                case 5:
                    System.out.print("Enter patient Unique ID(12 digits): ");
                    String patient_ID = sc.next();
                    while (patient_ID.length() != 12) {
                        System.out.println("Wrong Input. Try Again.");
                        System.out.print("Enter patient Unique ID(12 digits): ");
                        patient_ID = sc.next();
                    }
                    if(!portal.isCitizenRegistered(patient_ID))
                    {
                        System.out.println("Patient ID " + patient_ID + " not found!!!");
                        break;
                    }
                    System.out.println("""
                            1. Search by area
                            2. Search by Vaccine
                            3. Exit""");
                    System.out.print("Enter option: ");
                    int search_ch = sc.nextInt();
                    switch (search_ch) {
                        case 1:
                            portal.bookSlot_ByArea(patient_ID);
                            break;
                        case 2:
                            portal.bookSlot_ByVaccine(patient_ID);
                            break;
                        case 3:
                            break;
                    }
                    break;

                case 6:
                    System.out.print("Enter Hospital Id: ");
                    hosp_ID = sc.next();
                    if(!portal.isHospitalRegistered(hosp_ID))
                    {
                        System.out.println("Hospital ID " + hosp_ID + " not found!!!");
                        break;
                    }
                    portal.display_slots(hosp_ID);
                    break;

                case 7:
                    System.out.print("Enter Patient ID: ");
                    cit_ID = sc.next();
                    while (cit_ID.length() != 12) {
                        System.out.println("Wrong Input. Try Again.");
                        System.out.print("Enter Citizen Id: ");
                        cit_ID = sc.next();
                    }
                    if(!portal.isCitizenRegistered(cit_ID))
                    {
                        System.out.println("Citizen ID " + cit_ID + " not found!!!");
                        break;
                    }
                    portal.vaccination_status(cit_ID);
                    break;

                default:
                    System.out.println("Wrong option chosen. Choose Again");
            }
            System.out.println();
            System.out.println("---------------------------------");
            System.out.println("""
                1. Add Vaccine
                2. Register Hospital
                3. Register Citizen
                4. Add Slot for Vaccination
                5. Book Slot for Vaccination
                6. List all slots for a hospital
                7. Check Vaccination Status
                8. Exit""");
            System.out.println("---------------------------------");
            System.out.print("Choose an option :");
            choice = sc.nextInt();
        }
    }
}
