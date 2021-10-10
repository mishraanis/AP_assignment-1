package com.company;
import java.util.*;

class Portal
{
    private final LinkedList<Hospital> hosp;
    private final LinkedList<Citizen> ctz;
    private final LinkedList<Vaccine> vaccines;
    Portal()
    {
        hosp = new LinkedList<>();
        ctz = new LinkedList<>();
        vaccines = new LinkedList<>();
    }
    LinkedList<Vaccine> getVaccines()
    {
        return vaccines;
    }
    void addVaccine(String Name, int num_doses, int gap)
    {
        for(Vaccine vaccine: vaccines)
        {
            if(vaccine.getName().equals(Name))
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
            if(hospital.getName().equals(Name) && hospital.getPincode()==pincode)
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
            if(citizen.getID().equals(ID))
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
        if(vaccines.size() == 0)
        {
            System.out.println("No Vaccines have been registered on the website. Please Try again later");
            return ;
        }
        for(Hospital hospital: hosp)
        {
            if(ID.equals(hospital.getHosp_ID()))
            {
                for(Vaccine vaccine: vaccines) {
                    if (vaccine.getName().equals(vac_name)) {
                        Slot new_slot = new Slot(day, vaccine, qty);
                        hospital.getSlot().add(new_slot);
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
            if(hptl.getPincode() == pincode)
            {
                temp_hosp.add(hptl);
                System.out.println(hptl.getHosp_ID() + " " + hptl.getName());
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
            if(cit.getID().equals(patientID))
            {
                for(Hospital hptl: temp_hosp)
                {
                    if(hptl.getHosp_ID().equals(ID))
                    {
                        if(hptl.getSlot() == null)
                        {
                            System.out.println("No slots available");
                            return ;
                        }
                        else
                        {
                            int i=0;
                            boolean found = false;
                            for(Slot slot: hptl.getSlot())
                            {
                                if(cit.getDue_date()==-1 || slot.getDay() >= cit.getDue_date())
                                {
                                    System.out.println(i + "-> Day: " + slot.getDay() + " Available" + " Qty:" + slot.getQuantity() + " Vaccine: " + slot.vac.getName());
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
                            Slot exact_slot = hptl.getSlot().get(idx);

                            int n = exact_slot.getQuantity();
                            exact_slot.setQuantity(--n);
                            if(exact_slot.getQuantity()==0)
                            {
                                hptl.getSlot().remove(idx);
                            }

                            System.out.println(cit.getName() + " vaccinated with " + exact_slot.vac.getName());
                            int vac_stat = cit.getVac_status();
                            cit.setVac_status(++vac_stat);
                            if(cit.vac == null)
                            {
                                cit.vac = exact_slot.vac;
                                cit.setDue_date(exact_slot.getDay() + exact_slot.vac.getGap());
                            }
                            else
                            {
                                if(cit.getVac_status() < exact_slot.vac.getNum_doses())
                                {
                                    cit.setDue_date(exact_slot.getDay() + exact_slot.vac.getGap());
                                }
                                else if(exact_slot.vac.getNum_doses() == cit.getVac_status())
                                {
                                    cit.vac = exact_slot.vac;
                                    cit.setDue_date(0);
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
            for(Slot slot: hptl.getSlot())
            {
                if(slot.vac.getName().equals(vac_name))
                {
                    temp_hosp.add(hptl);
                    System.out.println(hptl.getHosp_ID() + " " + hptl.getName());
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
            if(cit.getID().equals(patientID))
            {
                for(Hospital hptl: temp_hosp)
                {
                    if(hptl.getHosp_ID().equals(ID))
                    {
                        if(hptl.getSlot()==null)
                        {
                            System.out.println("No slots available");
                            return ;
                        }
                        else
                        {
                            int i=0;
                            boolean found = false;
                            for(Slot slot: hptl.getSlot())
                            {
                                if(cit.getDue_date() == -1 || slot.getDay() >= cit.getDue_date())
                                {
                                    System.out.println(i + "-> Day: " + slot.getDay() + " Available" + " Qty:" + slot.getQuantity() + " Vaccine: " + slot.vac.getName());
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
                            Slot exact_slot = hptl.getSlot().get(idx);

                            int qnty = exact_slot.getQuantity();
                            exact_slot.setQuantity(--qnty);
                            if(exact_slot.getQuantity()==0)
                            {
                                hptl.getSlot().remove(idx);
                            }

                            System.out.println(cit.getName() + " vaccinated with " + exact_slot.vac.getName());
                            int vac_stats = cit.getVac_status();
                            cit.setVac_status(++vac_stats);
                            if(cit.vac == null)
                            {
                                cit.vac = exact_slot.vac;
                                cit.setDue_date(exact_slot.getDay() + exact_slot.vac.getGap());
                            }
                            else
                            {
                                if(cit.getVac_status() < exact_slot.vac.getNum_doses())
                                {
                                    cit.setDue_date(exact_slot.getDay() + exact_slot.vac.getGap());
                                }
                                else if(exact_slot.vac.getNum_doses() == cit.getVac_status())
                                {
                                    cit.vac = exact_slot.vac;
                                    cit.setDue_date(0);
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
            if(hptl.getHosp_ID().equals(ID))
            {
                if(hptl.getSlot() == null)
                {
                    System.out.println("No slots available");
                }
                else
                {
                    for(Slot slot: hptl.getSlot())
                    {
                        System.out.println("Day: " + slot.getDay() + " Vaccine: " + slot.vac.getName() + " Available" + " Qty: " + slot.getQuantity());
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
            if(cit.getID().equals(patientID))
            {
                if(cit.getVac_status() == 0)
                {
                    System.out.println("Citizen REGISTERED");
                    return ;
                }
                else if(cit.getVac_status() < cit.vac.getNum_doses())
                    System.out.println("PARTIALLY VACCINATED");
                else
                    System.out.println("FULLY VACCINATED");
                System.out.println("Vaccine Given: " + cit.vac.getName());
                System.out.println("Number of Doses given: " + cit.getVac_status());
                if(cit.getVac_status() < cit.vac.getNum_doses())
                    System.out.println("Next Dose due date: " + cit.getDue_date());
                return ;
            }
        }
        System.out.println("Citizen with ID " + patientID + " not found!!!");
    }


    boolean isHospitalRegistered(String hosp_ID)
    {
        for(Hospital hptl: hosp)
        {
            if (hptl.getHosp_ID().equals(hosp_ID))
                return true;
        }
        return false;
    }


    boolean isCitizenRegistered(String citizen_ID)
    {
        for(Citizen cit: ctz)
        {
            if (cit.getID().equals(citizen_ID))
                return true;
        }
        return false;
    }


}

class Hospital{
    private final int pincode;
    private final String hosp_ID, name;
    private final LinkedList<Slot> slot;

    Hospital(String name, String hosp_ID, int pincode)
    {
        this.name = name;
        this.hosp_ID = hosp_ID;
        this.pincode = pincode;
        slot = new LinkedList<>();
    }
    int getPincode()
    {
        return pincode;
    }
    String getName()
    {
        return name;
    }
    String getHosp_ID()
    {
        return hosp_ID;
    }
    LinkedList<Slot> getSlot()
    {
        return slot;
    }
}

class Citizen{
    private final String name;
    private final String ID;
    private final int age;
    private int vac_status;
    private int due_date;
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
    String getName()
    {
        return name;
    }
    String getID()
    {
        return ID;
    }
    int getVac_status()
    {
        return vac_status;
    }
    void setVac_status(int n)
    {
        vac_status = n;
    }
    int getDue_date()
    {
        return due_date;
    }
    void setDue_date(int n)
    {
        due_date = n;
    }
}

class Slot{
    private final int day;
    private int quantity;
    Vaccine vac;
    Slot(int day, Vaccine vac, int quantity)
    {
        this.day = day;
        this.vac = vac;
        this.quantity = quantity;
    }
    int getDay()
    {
        return day;
    }
    int getQuantity()
    {
        return quantity;
    }
    void setQuantity(int n)
    {
        quantity = n;
    }
}

class Vaccine{
    private final String name;
    private final int num_doses;
    private final int gap;
    Vaccine(String name, int num_doses, int gap)
    {
        this.name = name;
        this.num_doses = num_doses;
        this.gap = gap;
    }
    String getName()
    {
        return name;
    }
    int getNum_doses()
    {
        return num_doses;
    }
    int getGap()
    {
        return gap;
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
                    if(portal.getVaccines().size()==0)
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
                        for (int j = 0; j < portal.getVaccines().size(); j++) {
                            System.out.println(j + ". " + portal.getVaccines().get(j).getName());
                        }
                        int vac_idx = sc.nextInt();
                        portal.addSlot(hosp_ID, day, qty, portal.getVaccines().get(vac_idx).getName());
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
