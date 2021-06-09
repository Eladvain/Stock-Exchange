package manager;

import GenratedCode.RizpaStockExchangeDescriptor;
import components.commerce.RitzpaStockManager;
import components.commerce.Stock;
import components.commerce.Transaction.TransactionData;
import components.commerce.Transaction.TransactionsCommands;
import components.dataTransferObject.JaxbXmlToObject;
import components.dataTransferObject.ObjectSerialization;
import display.*;
import components.commerce.Transaction.TransactionData.buyOrSel;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

//C:\Users\אלעד\source\3rd year semester B\ג'אווה\projects\exersice1\engine\src\resources\ex1-small.xml
public class Menu {
    MenuUi menuUi;
    private final static String JAXB_XML = "GenratedCode";
    private RitzpaStockManager manager;
    private int level;
    private Scanner scanner;

    public Menu() {
        menuUi = new MenuUi();
        level = 1;
        scanner = new Scanner(System.in);
    }

    //the main function which called from the main
    public void StartApp() {
        System.out.println("Welcome to Ritzpa Stock Exchange App ");
        try {
            while (true) {
                switch (level) {
                    case 1:
                        int userChoice = menuUi.menuLevel1A();
                        switch (userChoice) {
                            case 1:
                                menuLevel1LoadXml();
                                break;
                            case 2:
                                System.out.println("Reloading System state...");
                                Thread.sleep(1500);
                                reloadSystemState();
                                break;
                        }

                        break;
                    case 2:
                        menuLevel2();
                    default:
                        level = 2;
                }
            }
        } catch (IOException ex) {

        } catch (ClassNotFoundException exp) {

        } catch (InterruptedException in) {

        }

    }

    public void menuLevel1LoadXml() {
        try {
            System.out.println("Please type the full path of the xml file you would like to load: ");
            String path = scanner.nextLine();
            Path stringAsPath = Paths.get(path);
            String Type = Files.probeContentType(stringAsPath);
            if (!Type.equals(JaxbXmlToObject.XML_TYPE)) {
                System.out.println("Error-the file type must be xml");
                return;
            }
            System.out.println("Loading new xml file...");
            Thread.sleep(1500);
            StringBuilder errorDescription = new StringBuilder();
            RizpaStockExchangeDescriptor rtz = JaxbXmlToObject.JaxbXml2Object(path);
            if (JaxbXmlToObject.isXmlValid(rtz, errorDescription)) {
                manager = RitzpaStockManager.createFromGenCode(rtz);
                if (manager != null) {
                    level = 2;
                }
            } else {
                System.out.println(errorDescription.toString());
            }
        } catch (FileNotFoundException | NullPointerException fnfex) {
            System.out.println("Error-File is not Exist in the requested path");
        } catch (UnmarshalException unme) {
            System.out.println("Error-The File Must be xml type ");
        }catch (JAXBException jaxbex)
        {
            System.out.println("Error-File is not Exist in the requested path");
        }
        catch (Exception ex) {
            System.out.println("Error in loading xml file");
        }
    }

    public void menuLevel2() {
        try {
            int userChoice = menuUi.menuLevel2Display();
            switch (userChoice) {
                case 1:
                    menuLevel1LoadXml();
                    break;
                case 2:
                    System.out.println(manager.getStocks().toString());
                    break;
                case 3:
                    printAllStocksCase3();
                    break;
                case 4:
                    menuForCase4();
                    break;
                case 5:
                    System.out.println(manager.printAllTransactionsDetails());
                    break;
                case 6:
                    saveCurrentSystemState();
                    break;
                case 7:
                    System.out.println("bye bye ...");
                    Thread.sleep(1500);
                    System.exit(0);
                default:
            }
        } catch (Exception ex) {
            System.out.println("Error in menuLevel2");
        }
    }

    public void menuForCase4() {
        Integer validLimit = null , validAmount, userChoice = 0;
        String validSymbol;
        buyOrSel typeAsEnum;
        TransactionsCommands typeCommand;
        do {
            typeCommand = chooseCommandType();
            if (typeCommand == null)
                return;
            validSymbol = isStockSymbolExist();
            if (validSymbol == null)
                return;
            typeAsEnum = chooseToBuyOrToSell();
            if (typeAsEnum == null)
                return;
            if (typeCommand == TransactionsCommands.LMT) {
                validLimit = isPositiveNumber("Please type the limit");
                if (validLimit == null)
                    return;
            }
            validAmount = isPositiveNumber("please type the amount of stocks you wouldLike to have");
            if (validAmount == null)
                return;
            TransactionData trans = new TransactionData(validSymbol, typeAsEnum, validAmount, validLimit);
            printCurrentApplyTransaction(trans, typeCommand, validAmount);
            return;
        } while (true);
    }

    public buyOrSel chooseToBuyOrToSell() {
        boolean validInput = false;
        buyOrSel typeAsEnum = null;
        do {
            try {
                System.out.println("1- buying 2- selling");
                int type = scanner.nextInt();
                if (type != 1 && type != 2) {
                    throw new InputMismatchException();
                } else {
                    validInput = true;
                    scanner.nextLine();
                    typeAsEnum = type == 1 ? buyOrSel.buy : buyOrSel.sell;
                }
            } catch (InputMismatchException ime) {
                scanner.nextLine();
                System.out.println("Error - supposed to print 1 / 2");
                if (isUserWantToContinue())
                    continue;
                else
                    return null;
            }
        } while (!validInput);
        return typeAsEnum;
    }

    public void printCurrentApplyTransaction(TransactionData trans, TransactionsCommands typeCommand
            , Integer amount) {
        try {
            List<TransactionData> executedTrans = manager.applyTransaction(trans, typeCommand);

            String str = "";
            int amountOfStocks = amount;
            if (executedTrans.size() != 0) {
                str += setTitles();
                for (TransactionData execTrans : executedTrans) {
                    str += execTrans.toString();
                    str += "\n";
                    amountOfStocks -= execTrans.getAmountOfStocks();
                }
                String fullOrPart = amountOfStocks == 0 ? "All stocks were purchased\n" : "Partial amount of stocks" +
                        " were purchased\n";
                System.out.println(fullOrPart + str);
            } else
                System.out.println("None of the stocks were purchased , inserted to waiting list\n");
        } catch (RuntimeException ex) {
            System.out.println("Error - the input must be int ans not a string");
        }
        finally {
            scanner.nextLine();
        }
    }

    public Integer isPositiveNumber(String strToPrint) {
        boolean validNum = false;
        Integer amount = 0;
        do {
            try {
                System.out.println(strToPrint);
                amount = scanner.nextInt();
                //scanner.nextLine();
                if (amount <= 0) {
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException ime) {
                scanner.nextLine();
                System.out.println("The limit/amount of stocks must be Positive number");
                if (!isUserWantToContinue())
                    return null;
                else
                    continue;
            }
            validNum = true;
        } while (!validNum);
        return amount;
    }

    public String isStockSymbolExist() {
        boolean validSymbol = false;
        String symbol;
        do {
            System.out.println("please type the stock symbol");
            symbol = scanner.nextLine();
            if (!manager.getStocks().getStocks().containsKey(symbol.toUpperCase())) {
                System.out.println("Error - The stock is not exist in the system");
                if (isUserWantToContinue())
                    continue;
                else
                    return null;
            }
            validSymbol = true;

        } while (!validSymbol);
        return symbol;
    }

    public TransactionsCommands chooseCommandType() {
        boolean validChoice = false;
        int userChoice = 0;
        TransactionsCommands typeCommand = null;
        do {
            try {
                System.out.println("1- for LMT 2 - MKT");
                userChoice = scanner.nextInt();
                if (userChoice != 1 && userChoice != 2) {
                    throw new InputMismatchException();
                } else {
                    validChoice = true;
                    scanner.nextLine();
                }
            } catch (InputMismatchException ime) {
                scanner.nextLine();
                System.out.println("Error - supposed to print 1 / 2");
                if (isUserWantToContinue())
                    continue;
                else
                    return typeCommand;
            }
        } while (!validChoice);
        return userChoice == 1 ? TransactionsCommands.LMT : TransactionsCommands.MKT;
    }

    public boolean isUserWantToContinue() {

        do {
            try {
                System.out.println("would you like to try again? 1- yes 2- no");
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice == 1) {
                    return true;
                } else if(choice==2)
                    return false;
                else
                {
                    System.out.println("Error- You must type 1 / 2");
                    continue;
                }
            } catch (RuntimeException ex) {
                System.out.println("Error- You must type int and not a string");
                scanner.nextLine();
                continue;
            }
        } while (true);
    }

    public void saveCurrentSystemState() {
        try {
            ObjectSerialization.saveCurrentSystemState(manager);
        } catch (IOException ex) {
            System.out.println("The Reload has failed");
        } catch (Exception ex) {
            System.out.println("Error accrued during reloading");
        }
    }

    public void reloadSystemState() throws IOException, ClassNotFoundException {
        try {
            manager = ObjectSerialization.reloadSystemState();
            if (manager == null) {
                System.out.println("The reload has failed");
            } else {
                level = 2;
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error accrued during reload");
        }

    }

    public void printAllStocksCase3() {
        String stockName = menuUi.menuLevel2C().toUpperCase();
        Stock stock = manager.getStockByKey(stockName);
        if (stock != null) {
            System.out.println(stock);
            System.out.println("All executed transaction are:");
            for (TransactionData trans : stock.getExecutedTrans())
                System.out.println(trans);
        } else {
            System.out.println("The stock you requested isn't in the system.");
        }
    }

    public String setTitles() {
        String str = "";
        str += "#";
        str += String.format("%-12s", "Total price");
        str += "|";
        str += String.format("%-7s", "price");
        str += "|";
        str += String.format("%-9s", "Quantity");
        str += "|";
        str += String.format("%-18s", "Date");
        str += "#\n";
        return str;
    }
}