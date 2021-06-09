package display;

import java.util.Scanner;

import com.sun.xml.internal.ws.api.model.ExceptionType;
import manager.*;

import javax.xml.bind.UnmarshalException;

public class MenuUi {
    private Scanner scanner = new Scanner(System.in);

    public int menuLevel1A() {
        String path = null;
        while (true) {
            try {
                System.out.println("|---------------------------------------------------|");
                System.out.println("|Press 1 - To load new Commerce  data from xml     :|");
                System.out.println("|Press 2 - To Reload System data                   :|");
                System.out.println("|---------------------------------------------------|");
                int choice = scanner.nextInt();
                if (choice < 1 || choice > 2) {
                    System.out.println("Error -  you must type 1 / 2");
                } else {
                    return choice;
                }
            } catch (RuntimeException ex) {
                System.out.println("Error you supposed type a number");
                scanner.nextLine();
            } catch (Exception ex) {
                System.out.println("Error accrued ");
            }
        }
    }

    public int menuLevel2Display() {
        System.out.println("|---------------------------------------------------|");
        System.out.println("|Press 1 - To load Commerce data from xml          :|");
        System.out.println("|Press 2 - Display all stocks data                 :|");
        System.out.println("|Press 3 - Display single stock data               :|");
        System.out.println("|Press 4 - To Execute trading action               :|");
        System.out.println("|Press 5 - Display List of commands to execute     :|");
        System.out.println("|Press 6 - In Order To save the current system state|");
        System.out.println("|Press 7 - To Exit                                 :|");
        System.out.println("|---------------------------------------------------|");
        int userChoice = -1;
        try {
            userChoice = scanner.nextInt();
            scanner.nextLine();
            if (userChoice <= 7 && userChoice > 0) {
                return userChoice;
            } else {
                System.out.println("You must type a number between 1-7");
            }
        } catch (RuntimeException ex) {
            System.out.println("Please type Number between 1-7");
            scanner.nextLine();
        } finally {
            return userChoice;
        }
    }

    public String menuLevel2C() {
        System.out.println("Please type the stock name you would like to display his data:");
        String stockName = scanner.nextLine();
        return stockName;
    }

}
