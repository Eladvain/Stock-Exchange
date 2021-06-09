package components.commerce;

import GenratedCode.RizpaStockExchangeDescriptor;
import GenratedCode.RseUsers;
import UserManager.Item;
import UserManager.User;
import UserManager.Users;
import components.commerce.Transaction.TransactionData;
import components.commerce.Transaction.TransactionsCommands;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RitzpaStockManager implements Serializable {
    public static String SystemSavingFile = "SystemSave.dat";
    public Users users;
    public RitzpaStockManager(Stocks stocks, RseUsers genRseUsers) {
        this.stocks = stocks;
        users = new Users(genRseUsers);
        for (Map.Entry<String, Stock> entry :this.stocks.getStocks().entrySet())
        {
            entry.getValue().stockPriceList.add(entry.getValue().getPrice());
            entry.getValue().xChartLocation.add(String.valueOf(0));
        }
    }

    private Stocks stocks;

    public Stocks getStocks() {
        return stocks;
    }

    public void setStocks(Stocks stocks) {
        this.stocks = stocks;
    }

    public static RitzpaStockManager createFromGenCode(RizpaStockExchangeDescriptor rtz) {
        return new RitzpaStockManager(Stocks.createStocks(rtz.getRseStocks()), rtz.getRseUsers());
    }

    public Stock getStockByKey(String symbol) {
        Map stocksMap = stocks.getStocks();
        return (Stock) stocksMap.get(symbol);
    }

    public List<TransactionData> applyTransaction(TransactionData newTrans, TransactionsCommands type) {
        List<TransactionData> executedTrans =  new ArrayList<>();
        boolean isExecuted = false, isInserted = false;
        int i = 0;
        Stock stock = stocks.getStocks().get(newTrans.getSymbolOfStock().toUpperCase());
        switch (newTrans.getTypeOfTrans()) {
            case buy:
                if(type==TransactionsCommands.LMT) {
                    isExecuted = insertToExecutedListCaseBuy(newTrans, stock, executedTrans);
                }
                else {
                   isExecuted =  commandBuyExecuationCaseMKT(newTrans,stock,executedTrans);
                }
                if (!isExecuted) {
                    if(type == TransactionsCommands.MKT)
                        newTrans.setLimitPrice(stock.getPrice());
                    insertToBuyList(newTrans, stock);
                }
                break;
            case sell:
                if(type==TransactionsCommands.LMT)
                isExecuted = insertToExecutedListCaseSeller(newTrans, stock,executedTrans);
                else
                    isExecuted = commandSellExecuationCaseMKT(newTrans,stock,executedTrans);
                if (!isExecuted) {
                    if(type == TransactionsCommands.MKT)
                        newTrans.setLimitPrice(stock.getPrice());
                    insertToSellerList(newTrans, stock);
                }
        }
        return executedTrans;
    }

    public boolean insertToExecutedListCaseSeller(TransactionData newTrans, Stock stock,List<TransactionData> executedTrans) {
        boolean isExecuted = false;
        for (int i = 0; i < stock.getHoldingBuyingTrans().size(); i++) {
            TransactionData buyingTrans = stock.getHoldingBuyingTrans().get(i);
            if (buyingTrans.getLimitPrice() >= newTrans.getLimitPrice()) {
                if (buyingTrans.getAmountOfStocks() == newTrans.getAmountOfStocks()) {
                    setIncaseOfNewSellEqualsStocksAmount(stock, newTrans, buyingTrans,executedTrans);
                    i--;
                    isExecuted = true;
                    break;
                } else if (buyingTrans.getAmountOfStocks() > newTrans.getAmountOfStocks()) {
                    setInCaseOfNewSellSmallerAmountOfStocks(stock, newTrans, buyingTrans,executedTrans);
                    isExecuted = true;
                    break;
                } else if (buyingTrans.getAmountOfStocks() < newTrans.getAmountOfStocks()) {
                    setInCaseOfNewSellBiggerStocksAmount(stock, newTrans, buyingTrans,executedTrans);
                    i--;
                }
            }
        }
        return isExecuted;
    }

    public boolean insertToExecutedListCaseBuy(TransactionData newTrans, Stock stock,List<TransactionData> executedTrans) {
        boolean isExecuted = false;
        for (int i = 0; i < stock.getHoldingSalesTrans().size(); i++) {
            TransactionData sellingTrans = stock.getHoldingSalesTrans().get(i);
            if (sellingTrans.getLimitPrice() <= newTrans.getLimitPrice()) {
                if (sellingTrans.getAmountOfStocks() > newTrans.getAmountOfStocks()) {
                    setIncaseOfNewBuyBiggerSellerStockAmount(stock, sellingTrans, newTrans,executedTrans);
                    isExecuted = true;
                    break;
                } else if (sellingTrans.getAmountOfStocks() < newTrans.getAmountOfStocks()) {
                    setInCaseOfNewBuySmallerSellerStockAmount(stock, sellingTrans, newTrans,executedTrans);
                    i--;

                } else {
                    setInCaseOfNewBuyEqualsStocksAmount(stock, sellingTrans, newTrans, executedTrans);
                    i--;
                    isExecuted = true;
                    break;
                }

            }
        }
        return isExecuted;
    }

    public void setIncaseOfNewBuyBiggerSellerStockAmount(Stock stock, TransactionData sellingTrans
            , TransactionData buyingTrans,List<TransactionData> executedTrans) {
        buyingTrans.setTypeOfTrans((TransactionData.buyOrSel.executed));
        buyingTrans.setTotalPriceTrans(sellingTrans.getLimitPrice() * buyingTrans.getAmountOfStocks());
        sellingTrans.setAmountOfStocks(sellingTrans.getAmountOfStocks() - buyingTrans.getAmountOfStocks());
        stock.setPrice(sellingTrans.getLimitPrice());
        buyingTrans.setLimitPrice(sellingTrans.getLimitPrice());
        stock.addTotalTransactionPrice(buyingTrans.getTotalPriceTrans());
        buyingTrans.setTimeStamp();
        stock.getExecutedTrans().add(buyingTrans);
        buyingTrans.UserNameSeller=sellingTrans.UserNameSeller;
        executedTrans.add(buyingTrans);
        stock.addNumberOfStocks(buyingTrans.getAmountOfStocks());
        stock.getHoldingBuyingTrans().remove(buyingTrans);
    }

    public void setInCaseOfNewBuySmallerSellerStockAmount(Stock stock, TransactionData sellingTrans,
                                                          TransactionData buyingTrans,List<TransactionData> executedTrans) {
        sellingTrans.setTypeOfTrans((TransactionData.buyOrSel.executed));
        sellingTrans.setTotalPriceTrans(sellingTrans.getLimitPrice() * sellingTrans.getAmountOfStocks());
        buyingTrans.setAmountOfStocks(buyingTrans.getAmountOfStocks() - sellingTrans.getAmountOfStocks());
        stock.setPrice(sellingTrans.getLimitPrice());
        stock.addTotalTransactionPrice(sellingTrans.getTotalPriceTrans());
        sellingTrans.setTimeStamp();
        stock.getExecutedTrans().add(sellingTrans);
        sellingTrans.UserNameBuyer = buyingTrans.UserNameBuyer;
        executedTrans.add(sellingTrans);
        stock.addNumberOfStocks(sellingTrans.getAmountOfStocks());
        stock.getHoldingSalesTrans().remove(sellingTrans);
    }

    public void setInCaseOfNewBuyEqualsStocksAmount(Stock stock, TransactionData sellingTrans,
                                                    TransactionData buyingTrans,List<TransactionData> executedTrans) {
        buyingTrans.setTypeOfTrans(TransactionData.buyOrSel.executed);
        buyingTrans.setTotalPriceTrans(buyingTrans.getAmountOfStocks() * sellingTrans.getLimitPrice());
        stock.setPrice(sellingTrans.getLimitPrice());
        buyingTrans.setLimitPrice(sellingTrans.getLimitPrice());
        stock.addTotalTransactionPrice(buyingTrans.getTotalPriceTrans());
        buyingTrans.setTimeStamp();
        stock.getExecutedTrans().add(buyingTrans);
        buyingTrans.UserNameSeller = sellingTrans.UserNameSeller;
        executedTrans.add(buyingTrans);
        stock.addNumberOfStocks(buyingTrans.getAmountOfStocks());
        stock.getHoldingSalesTrans().remove(sellingTrans);
    }


    public void setIncaseOfNewSellEqualsStocksAmount(Stock stock, TransactionData sellingTrans,
                                                     TransactionData buyingTrans,List<TransactionData> executedTrans) {
        sellingTrans.setTypeOfTrans(TransactionData.buyOrSel.executed);
        sellingTrans.setTotalPriceTrans(sellingTrans.getAmountOfStocks() * buyingTrans.getLimitPrice());
        sellingTrans.setLimitPrice(buyingTrans.getLimitPrice());
        stock.getExecutedTrans().add(sellingTrans);
        sellingTrans.setTimeStamp();
        sellingTrans.UserNameBuyer = buyingTrans.UserNameBuyer;;
        executedTrans.add(sellingTrans);
        stock.addTotalTransactionPrice(sellingTrans.getTotalPriceTrans());
        stock.addNumberOfStocks(sellingTrans.getAmountOfStocks());
        stock.setPrice(buyingTrans.getLimitPrice());
        //stock.getHoldingSalesTrans().remove(buyingTrans);
        stock.getHoldingBuyingTrans().remove(buyingTrans);
    }

    public void setInCaseOfNewSellSmallerAmountOfStocks(Stock stock, TransactionData sellingTrans,
                                                        TransactionData buyingTrans,List<TransactionData> executedTrans) {

        sellingTrans.setTypeOfTrans((TransactionData.buyOrSel.executed));
        sellingTrans.setTotalPriceTrans(buyingTrans.getLimitPrice() * sellingTrans.getAmountOfStocks());
        sellingTrans.setLimitPrice(buyingTrans.getLimitPrice());
        stock.getExecutedTrans().add(sellingTrans);
        sellingTrans.UserNameBuyer = buyingTrans.UserNameBuyer;
        executedTrans.add(sellingTrans);
        sellingTrans.setTimeStamp();
        stock.addNumberOfStocks(sellingTrans.getAmountOfStocks());
        stock.addTotalTransactionPrice(sellingTrans.getTotalPriceTrans());
        stock.setPrice(buyingTrans.getLimitPrice());
        buyingTrans.setAmountOfStocks(buyingTrans.getAmountOfStocks() - sellingTrans.getAmountOfStocks());

    }

    public void setInCaseOfNewSellBiggerStocksAmount(Stock stock, TransactionData sellingTrans
            , TransactionData buyingTrans,List<TransactionData> executedTrans) {
        buyingTrans.setTypeOfTrans((TransactionData.buyOrSel.executed));
        buyingTrans.setTotalPriceTrans(buyingTrans.getLimitPrice() * buyingTrans.getAmountOfStocks());
        stock.setPrice(buyingTrans.getLimitPrice());
        stock.addTotalTransactionPrice(buyingTrans.getTotalPriceTrans());
        stock.getExecutedTrans().add(buyingTrans);
        buyingTrans.setTimeStamp();
        buyingTrans.UserNameSeller = sellingTrans.UserNameSeller;
        executedTrans.add(buyingTrans);
        stock.addNumberOfStocks(buyingTrans.getAmountOfStocks());
        sellingTrans.setAmountOfStocks(sellingTrans.getAmountOfStocks() - buyingTrans.getAmountOfStocks());
        stock.getHoldingBuyingTrans().remove(buyingTrans);
    }

    public void insertToSellerList(TransactionData newTrans, Stock stock) {
        int i = 0;
        boolean isInserted = false;
        for (TransactionData seller : stock.getHoldingSalesTrans()) {

            if (newTrans.getLimitPrice() < seller.getLimitPrice()) {
                stock.getHoldingSalesTrans().add(i, newTrans);
                isInserted = true;
                break;
            }
            i++;
        }
        if (!isInserted) {
            stock.getHoldingSalesTrans().add(i, newTrans);
        }
    }


    public void insertToBuyList(TransactionData newTrans, Stock stock) {
        boolean isInserted = false;
        int i = 0;
        for (TransactionData buyer : stock.getHoldingBuyingTrans()) {
            if (newTrans.getLimitPrice() > buyer.getLimitPrice()) {
                stock.getHoldingBuyingTrans().add(i, newTrans);
                isInserted = true;
                break;
            }
            i++;
        }
        if (!isInserted) {
            stock.getHoldingBuyingTrans().add(i, newTrans);
        }
    }

    public String printAllTransactionsDetails() {
        String str = "";
        for (Map.Entry<String, Stock> entry : stocks.getStocks().entrySet()) {
            str += "Stock symbol: " + entry.getValue().getSymbol() + "\n";
            str += "Company name: " + entry.getValue().getCompanyName() + "\n";
            str += "Stock rate: " + entry.getValue().getPrice() + "\n";
            str += entry.getValue().getListOfTransactionAsString();
            str += "\n";
        }
        return str;
    }
    public boolean commandBuyExecuationCaseMKT(TransactionData newTrans, Stock stock , List<TransactionData> executedTrans)
    {
        boolean isExecuted=false;
        for (int i = 0; i < stock.getHoldingSalesTrans().size(); i++) {
            TransactionData sellingTrans = stock.getHoldingSalesTrans().get(i);
                if (sellingTrans.getAmountOfStocks() > newTrans.getAmountOfStocks()) {
                    newTrans.setLimitPrice(sellingTrans.getLimitPrice());
                    setIncaseOfNewBuyBiggerSellerStockAmount(stock, sellingTrans, newTrans,executedTrans);
                    isExecuted = true;
                    break;
                } else if (sellingTrans.getAmountOfStocks() < newTrans.getAmountOfStocks()) {
                    setInCaseOfNewBuySmallerSellerStockAmount(stock, sellingTrans, newTrans,executedTrans);
                    i--;

                } else {
                    newTrans.setLimitPrice(sellingTrans.getLimitPrice());
                    setInCaseOfNewBuyEqualsStocksAmount(stock, sellingTrans, newTrans, executedTrans);
                    i--;
                    isExecuted = true;
                    break;
                }
        }
        return isExecuted;
    }

    public boolean commandSellExecuationCaseMKT(TransactionData newTrans, Stock stock , List<TransactionData> executedTrans)
    {
        boolean isExecuted=false;
        for (int i = 0; i < stock.getHoldingBuyingTrans().size(); i++) {
            TransactionData buyingTrans = stock.getHoldingBuyingTrans().get(i);
            if (buyingTrans.getAmountOfStocks() > newTrans.getAmountOfStocks()) {
                newTrans.setLimitPrice(buyingTrans.getLimitPrice());
                setInCaseOfNewSellSmallerAmountOfStocks(stock, newTrans, buyingTrans,executedTrans);
                isExecuted = true;
                break;
            } else if (buyingTrans.getAmountOfStocks() < newTrans.getAmountOfStocks()) {
                setInCaseOfNewSellBiggerStocksAmount(stock,  newTrans,buyingTrans,executedTrans);
                i--;

            } else {
                newTrans.setLimitPrice(buyingTrans.getLimitPrice());
                setIncaseOfNewSellEqualsStocksAmount(stock, newTrans,buyingTrans, executedTrans);
                i--;
                isExecuted = true;
                break;
            }
        }
        return isExecuted;
    }
    public User getUserFromName(String name)
    {
        for (User itUser : users.getListOfUsers())
        {
            if(itUser.getName().equals(name))
                return itUser;
        }
        return null;
    }
    public List<String> getStocksFromUserByTransactionType(String userName ,TransactionData.buyOrSel transcationType)
    {
        List<String> listOfStocksSymbol = new ArrayList<>();

        if(transcationType == TransactionData.buyOrSel.buy) {
            for (Map.Entry<String, Stock> entry : stocks.getStocks().entrySet()) {
                listOfStocksSymbol.add(entry.getKey());
            }
        }
        else
        {
            User user =getUserFromName(userName);
          for (Item item : user.getHoldingStocks().getItemList())
          {
              listOfStocksSymbol.add(item.getSymbol());
          }
        }
        return listOfStocksSymbol;
    }
    public boolean checkValidityOfNewTransactionCommand(String userName, TransactionData.buyOrSel transType, int
            amountOfStocks,String stockSymbol, StringBuilder errorDetails, Integer limitPrice)
    {

        if( amountOfStocks<=0) {
            errorDetails.append("Amount of stocks must be greater than 0");
            return false;
        }
        if(limitPrice!=null)
            {
                if(limitPrice<=0) {
                    errorDetails.append("Price of stock must be greater than 0");
                    return false;
                }
            }
        if (transType == TransactionData.buyOrSel.buy)
                return  true;
            else {
                User user = getUserFromName(userName);
                if (user.getAmountOfStocksBySymbol(stockSymbol) >= amountOfStocks)
                    return true;
                else {
                    errorDetails.append("Error! you have " + user.getAmountOfStocksBySymbol(stockSymbol) +
                            " stocks of " + stockSymbol + ". you can not sell more than that amount");
                    return  false;
                }
            }
    }
    public Integer addNewPriceToStockList(List<TransactionData> trans,Integer Counter)
    {
        for(TransactionData newTrans :trans)
        {
            Stock stock = getStockByKey(newTrans.getSymbolOfStock());
            stock.stockPriceList.add(newTrans.getLimitPrice());
            stock.xChartLocation.add(String.valueOf(Counter));
            Counter++;
        }
        return  Counter;
    }
}
