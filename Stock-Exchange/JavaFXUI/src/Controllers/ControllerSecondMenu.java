package Controllers;
import components.dataTransferObject.ObjectSerialization;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import UserManager.User;
import components.commerce.RitzpaStockManager;
import components.commerce.Stock;
import components.commerce.Transaction.TransactionData;
import components.commerce.Transaction.TransactionsCommands;
import components.dataTransferObject.DataAsStringExecuted;
import components.dataTransferObject.DataAsStrings;
import components.dataTransferObject.UserDetailToTableAsStrings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ControllerSecondMenu {
    Integer counterOfChart=1;
    int amount =0;
    public RitzpaStockManager manager;
    //-------------------------------------------------
    @FXML
    private Tab TabStockGraph;

    @FXML
    private LineChart<?, ?> ChartGraphStock;

    @FXML
    private Polygon poligonsecondMenu;
    @FXML
    private Polygon poligonsecondMenu2;
//-------------------------------------------------------------------
@FXML
private Button ButtomSaveSystemSate;
    @FXML
    void buttonSaveSystemStateClicked(ActionEvent event) {
        try {
            ObjectSerialization.saveCurrentSystemState(manager);
        } catch (IOException ex) {
            System.out.println("The Reload has failed");
        } catch (Exception ex) {
            System.out.println("Error accrued during reloading");
        }
    }
    //--------------------------------------------------------------
    @FXML
    void AnimationStartButton(ActionEvent event) {
    if(isAnimationWork)
    {
        if(pt!=null)
            pt.stop();
        buttonAnimationStart.setText("start animation");
        isAnimationWork=false;
    }
    else
    {
        buttonAnimationStart.setText("stop animation");
        isAnimationWork=true;
    }
    }
    @FXML
    private Button buttonAnimationStart;
//------------------------------------------------------------------

    @FXML
    private ChoiceBox<String> ChoicBoxTransactionUser;
    @FXML
    private TabPane TabPaneAdminOrUSers;
public Tab lastUserTab;
    @FXML
    private Tab TabAdmin;
    @FXML
    private ChoiceBox<String> ChooseBoxUser;
    //-----------------------------------------------------
    @FXML
    private TableView<UserDetailToTableAsStrings> TableViewUser;

    @FXML
    private TableColumn<UserDetailToTableAsStrings, String> UserColumnSymbol;

    @FXML
    private TableColumn<UserDetailToTableAsStrings, String> UserColumnAmunt;

    @FXML
    private TableColumn<UserDetailToTableAsStrings, String> UserColumnStockRate;

    //------------------------------------------------------
    @FXML
    private TableView<DataAsStrings> TableViewBuying;

    private TableColumn<DataAsStrings, String> columnTimeTableBuy ;


    private TableColumn<DataAsStrings, String> columnLMTMKTTableBuy;


    private TableColumn<DataAsStrings, String>columnAmountTableBuy;


    private TableColumn<DataAsStrings, String>columnPriceTableBuy;


    private TableColumn<DataAsStrings, String>columnInitializeTableBuy;

//-------------------------------------------------------------------------------
    @FXML
    private TableView<DataAsStrings> TableViewSelling;

    @FXML
    private TableColumn<DataAsStrings, String> ColumnSellingTime;

    @FXML
    private TableColumn<DataAsStrings, String> ColumnSellingMKTLMT;

    @FXML
    private TableColumn<DataAsStrings, String> ColumnSellingAmount;

    @FXML
    private TableColumn<DataAsStrings, String> ColumnSellingPrice;

    @FXML
    private TableColumn<DataAsStrings, String> ColumnSellingInitalize;
    //------------------------------------------------------------------------
    @FXML
    private TableView<DataAsStringExecuted> TableViewExecuted;

    private TableColumn<DataAsStringExecuted, String> TableViewExecutedTime;


    private TableColumn<DataAsStringExecuted, String> TableViewExecutedLmtMKT;


    private TableColumn<DataAsStringExecuted, String> TableViewExecutedAmount;


    private TableColumn<DataAsStringExecuted, String> TableViewExecutedPrice;

    private TableColumn<DataAsStringExecuted, String> SellerColumnTable;


    private TableColumn<DataAsStringExecuted,String> BuyerColumnTable;

    //-------------------------------------------------------------------

    @FXML
    private ChoiceBox<String> ChoicePickerStockName;

    @FXML
    private ChoiceBox<String> ChoicePickerStockToDisplay;

    @FXML
    private Tab TabUser;

    @FXML
    private ChoiceBox<String> ChoicePickerTransactionType;

    @FXML
    private ChoiceBox<String> ChoicePickerCommerceType;

    @FXML
    private TextField TextFillAmountOfStocks;

    @FXML
    private TextField TextFillEateLimit;
    @FXML
    private Button ButtonExecute;
    @FXML
    private GridPane gridPAneSecond;
    public ParallelTransition pt;
    public boolean isAnimationWork=true;
    public  void  setAllComponents()
    {
        setTableForUser();
        setSellingTable();
        setExecutedTable();
        columnTimeTableBuy = new TableColumn<>();
        columnTimeTableBuy.setCellValueFactory(new PropertyValueFactory<DataAsStrings, String>("Name"));
        columnTimeTableBuy.setMinWidth(150);
        columnTimeTableBuy.setText("Time");

        columnAmountTableBuy = new TableColumn<>();
        columnAmountTableBuy.setCellValueFactory(new PropertyValueFactory<DataAsStrings, String>("Amount"));
        columnAmountTableBuy.setMinWidth(150);
        columnAmountTableBuy.setText("Amount");

        columnInitializeTableBuy = new TableColumn<>();
        columnInitializeTableBuy.setCellValueFactory(new PropertyValueFactory<DataAsStrings, String>("Initialize"));
        columnInitializeTableBuy.setMinWidth(150);
        columnInitializeTableBuy.setText("Initialize name");
        columnLMTMKTTableBuy = new TableColumn<>();
        columnLMTMKTTableBuy.setCellValueFactory(new PropertyValueFactory<DataAsStrings, String>("LMTOTMKT"));
        columnLMTMKTTableBuy.setMinWidth(150);
        columnLMTMKTTableBuy.setText("LMT/ MKT");
        columnPriceTableBuy = new TableColumn<>();
        columnPriceTableBuy.setCellValueFactory(new PropertyValueFactory<DataAsStrings, String>("Price"));
        columnPriceTableBuy.setMinWidth(150);
        columnPriceTableBuy.setText("Price");

        TableViewBuying.getColumns().addAll(columnTimeTableBuy,columnLMTMKTTableBuy,columnAmountTableBuy,columnPriceTableBuy,columnInitializeTableBuy);
        ChoicePickerCommerceType.getItems().add("LMT");
        ChoicePickerCommerceType.getItems().add("MKT");
        ChoicePickerTransactionType.getItems().add("Buy Stocks");
        ChoicePickerTransactionType.getItems().add("Sell Stocks");
        for (Map.Entry<String, Stock> entry :manager.getStocks().getStocks().entrySet())
        {
                        ChoicePickerStockToDisplay.getItems().add(entry.getKey());
        }
        ChoicePickerStockToDisplay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String str = ChoicePickerStockToDisplay.getValue();
              Stock stock =  manager.getStocks().getStocks().get( ChoicePickerStockToDisplay.getValue());
              setAllTables(stock);
            }
        });
        ChoicePickerTransactionType.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setTransactionDetailsByUserAndType();
            }
        });
        ChoicBoxTransactionUser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setTransactionDetailsByUserAndType();
            }
        });

        for ( User user : manager.users.getListOfUsers())
        {
            ChooseBoxUser.getItems().add(user.getName());
            ChoicBoxTransactionUser.getItems().add(user.getName());
        }
        ChooseBoxUser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(ChooseBoxUser.getValue()!=null) {
                    String str = ChooseBoxUser.getValue();
                    User user = manager.getUserFromName(str);
                    setTableInCaseOfUSer(user);
                }
            }
        });
        ChoicePickerCommerceType.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(ChoicePickerCommerceType.getValue()=="MKT")
                {
                    TextFillEateLimit.setVisible(false);
                }
                else
                {
                    TextFillEateLimit.setVisible(true);
                }
            }
        });
        setChart();
    }

    @FXML
    void ButtonExecutePress(ActionEvent event) {

        if(isAnimationWork) {
            FillTransition ft = new FillTransition(Duration.seconds(1), javafx.scene.paint.Color.RED, Color.GREEN);
            ft.setCycleCount(5);
            ft.setAutoReverse(true);


            RotateTransition rt = new RotateTransition(Duration.seconds(1));
            rt.setByAngle(360);
            rt.setCycleCount(5);
            ft.setAutoReverse(true);

            pt = new ParallelTransition(poligonsecondMenu, rt, ft);
            pt.play();
        }
        executeTransaction();
        resetTransactionData();
        setChart();

    }
    public void setSellingTable()
    {
        ColumnSellingTime.setCellValueFactory(new PropertyValueFactory<DataAsStrings,String>("Name"));
        ColumnSellingTime.setMinWidth(150);

        ColumnSellingAmount.setCellValueFactory(new PropertyValueFactory<DataAsStrings,String>("Amount"));
        ColumnSellingAmount.setMinWidth(150);

        ColumnSellingInitalize.setCellValueFactory(new PropertyValueFactory<DataAsStrings,String>("Initialize"));
        ColumnSellingInitalize.setMinWidth(150);

        ColumnSellingMKTLMT.setCellValueFactory(new PropertyValueFactory<DataAsStrings,String>("LMTOTMKT"));
        ColumnSellingMKTLMT.setMinWidth(150);

        ColumnSellingPrice.setCellValueFactory(new PropertyValueFactory<DataAsStrings,String>("Price"));
        ColumnSellingPrice.setMinWidth(150);

    }
    void setExecutedTable()
    {
        TableViewExecutedAmount =new TableColumn<>();
        TableViewExecutedAmount.setCellValueFactory(new PropertyValueFactory<DataAsStringExecuted,String>("Amount"));
        TableViewExecutedAmount.setMinWidth(123);
        TableViewExecutedAmount.setText("Amount");

        TableViewExecutedPrice =new TableColumn<>();
        TableViewExecutedPrice.setCellValueFactory(new PropertyValueFactory<DataAsStringExecuted,String>("Price"));
        TableViewExecutedPrice.setMinWidth(123);
        TableViewExecutedPrice.setText("Price");

        TableViewExecutedTime = new TableColumn<>();
        TableViewExecutedTime.setCellValueFactory(new PropertyValueFactory<DataAsStringExecuted,String>("Name"));
        TableViewExecutedTime.setMinWidth(123);
        TableViewExecutedTime.setText("Time");

        TableViewExecutedLmtMKT = new TableColumn<>();
        TableViewExecutedLmtMKT.setCellValueFactory(new PropertyValueFactory<DataAsStringExecuted,String>("LMTOTMKT"));
        TableViewExecutedLmtMKT.setMinWidth(123);
        TableViewExecutedLmtMKT.setText("LMT / MKT");

        SellerColumnTable =new TableColumn<>();
        SellerColumnTable.setCellValueFactory(new PropertyValueFactory<DataAsStringExecuted,String>("sellerUser"));
        SellerColumnTable.setMinWidth(120);
        SellerColumnTable.setText("Seller name");

        BuyerColumnTable =  new TableColumn<>();
        BuyerColumnTable.setCellValueFactory(new PropertyValueFactory<DataAsStringExecuted,String>("buyerUser"));
        BuyerColumnTable.setMinWidth(123);
        BuyerColumnTable.setText("Buyer name");

        TableViewExecuted.getColumns().addAll(
                TableViewExecutedTime,TableViewExecutedLmtMKT ,TableViewExecutedAmount,TableViewExecutedPrice,
                BuyerColumnTable ,SellerColumnTable);
    }
    void setTableForUser()
    {

        UserColumnSymbol.setCellValueFactory(new PropertyValueFactory<UserDetailToTableAsStrings,String>("Symbol"));
        UserColumnSymbol.setMinWidth(180);
        UserColumnAmunt.setCellValueFactory(new PropertyValueFactory<UserDetailToTableAsStrings,String>("Amount"));
        UserColumnAmunt.setMinWidth(180);
        UserColumnStockRate.setCellValueFactory(new PropertyValueFactory<UserDetailToTableAsStrings,String>("StockRate"));
        UserColumnStockRate.setMinWidth(180);
    }
    public void setAllTables(Stock stock)
    {
        //---------------------------------------------------
        for(int i=0; i<TableViewBuying.getItems().size();i++)
        {
            TableViewBuying.getItems().clear();
        }
        for(int i=0;i<TableViewSelling.getItems().size();i++)
        {
            TableViewSelling.getItems().clear();
        }
        for (int i=0; i<TableViewExecuted.getItems().size();i++)
        {
            TableViewExecuted.getItems().clear();
        }
        for(TransactionData trans : stock.getHoldingBuyingTrans())
        {
            TableViewBuying.getItems().add(new DataAsStrings(trans,trans.UserNameBuyer));
        }
        for (TransactionData trans : stock.getHoldingSalesTrans())
        {
            TableViewSelling.getItems().add(new DataAsStrings(trans,trans.UserNameSeller));
        }
        for(TransactionData trans: stock.getExecutedTrans())
        {
            TableViewExecuted.getItems().add(new DataAsStringExecuted(trans,trans.UserNameSeller,trans.UserNameBuyer));
        }

    }
    private void setTableInCaseOfUSer(User user)
    {
        if(user!=null) {
            for (int i = 0; i < TableViewUser.getItems().size(); i++) {
                TableViewUser.getItems().clear();
            }
            for (UserDetailToTableAsStrings userDetail : UserDetailToTableAsStrings.getUserDetailsFromUser(user, manager)) {
                TableViewUser.getItems().add(userDetail);
            }
        }
    }
    private void setTransactionDetailsByUserAndType()
    {
        if(ChoicBoxTransactionUser.getValue()!=null && ChoicePickerTransactionType.getValue()!=null) {
            TransactionData.buyOrSel TransType = ((ChoicePickerTransactionType.getValue().toString() == "Sell Stocks") ? TransactionData.buyOrSel.sell : TransactionData.buyOrSel.buy);
            String userName = ChoicBoxTransactionUser.getValue();
            ChoicePickerStockName.getItems().clear();
            ChoicePickerStockName.setVisible(true);
            ChoicePickerCommerceType.setVisible(true);
            TextFillAmountOfStocks.setVisible(true);
            TextFillEateLimit.setVisible(true);
            for (String str : manager.getStocksFromUserByTransactionType(userName, TransType)) {
                ChoicePickerStockName.getItems().add(str);
            }
        }
    }
    private void resetTransactionData()
    {
        ChoicePickerStockName.setVisible(false);
        ChoicePickerCommerceType.setVisible(false);
        TextFillAmountOfStocks.setVisible(false);
        TextFillEateLimit.setVisible(false);
        ChoicBoxTransactionUser.getSelectionModel().clearSelection();
        ChoicePickerCommerceType.getSelectionModel().clearSelection();
        ChoicePickerTransactionType.getSelectionModel().clearSelection();
        TextFillAmountOfStocks.setText("");
        TextFillEateLimit.setText("");
    }
    private void executeTransaction()
    {
        try {
            Integer price=null;
            TransactionData trans;
            String name = ChoicBoxTransactionUser.getValue();
            String stockName = ChoicePickerStockName.getValue().toString();
            TransactionData.buyOrSel TransType = ((ChoicePickerTransactionType.getValue().toString() == "Sell Stocks") ? TransactionData.buyOrSel.sell : TransactionData.buyOrSel.buy);
            int Amount = Integer.parseInt(TextFillAmountOfStocks.getText());
            TransactionsCommands commands = ChoicePickerCommerceType.getValue() == "LMT" ? TransactionsCommands.LMT : TransactionsCommands.MKT;
            if(commands==TransactionsCommands.LMT)
                price = Integer.parseInt(TextFillEateLimit.getText());
            StringBuilder stringBuilder = new StringBuilder();
            if (manager.checkValidityOfNewTransactionCommand(name, TransType, Amount, stockName, stringBuilder, price)) {
                if (TransType == TransactionData.buyOrSel.buy) {
                    String Seller = "";
                    trans = new TransactionData(stockName, TransType, Amount, price, commands, name, Seller);
                } else {
                    String Buyer = "";
                    trans = new TransactionData(stockName, TransType, Amount, price, commands, Buyer, name);
                    User user = manager.getUserFromName(name);
                    user.getItemFromSymbol(stockName).setAmountOfSellingStocks(Amount);
                }
                List<TransactionData> transAsList = manager.applyTransaction(trans, commands);
               counterOfChart = manager.addNewPriceToStockList(transAsList, counterOfChart);

                if (ChoicePickerStockToDisplay.getValue() != null)
                    setAllTables(manager.getStocks().getStocks().get(ChoicePickerStockToDisplay.getValue()));
                User.setUserItems(transAsList,manager);
                User user = manager.getUserFromName(ChooseBoxUser.getValue());
                setTableInCaseOfUSer(user);


            } else {
                errorPopUp("Transaction Error", stringBuilder.toString());
            }
        }
        catch (Exception ex)
        {
            errorPopUp("invalid Transaction","You must enter all of the requested fields");
        }

    }
    public static void errorPopUp(String header , String body)
    {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(body);
        errorAlert.showAndWait();
    }
    public void setChart()
    {
        ChartGraphStock.getData().clear();
        for (Map.Entry<String, Stock> entry :manager.getStocks().getStocks().entrySet()) {
            XYChart.Series series = new XYChart.Series();
             for (int i=0;i<entry.getValue().stockPriceList.size();i++) {
                 series.getData().add(new XYChart.Data(entry.getValue().xChartLocation.get(i), entry.getValue().stockPriceList.get(i)));

             }
            series.setName(entry.getKey());
            ChartGraphStock.getData().addAll(series);
        }
    }

}
