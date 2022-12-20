package com.example.supplychainneeraj;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class SupplyChain extends Application {

    public static final int width = 700, height = 600, headerBar = 50;
    Pane bodyPane = new Pane();
    Login login = new Login();
    ProductDetails productDetails = new ProductDetails();
    public TableView<Product> cartTable;
    Button globalLoginButton;
    Label customerEmailLabel = null;
    Label messageLabel;
    Button globalLogoutButton;
    Button myCartButton;

    Button addToCartButton;
    Button buyNowButton;
    ObservableList<Product> cart;

    String customerEmail = null;
    private GridPane headerBar(){
        TextField searchText = new TextField();
        Button searchButton = new Button("Search");
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String productName = searchText.getText();
                productDetails.getAllProductsByName(productName);
                // clear body put this new pane in the body
                bodyPane.getChildren().clear();
                bodyPane.getChildren().add(productDetails.getAllProductsByName(productName));
            }
        });

        globalLoginButton = new Button("Log In");
        globalLoginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                bodyPane.getChildren().clear();
                bodyPane.getChildren().add(loginPage());
            }
        });

        customerEmailLabel = new Label("Welcome User");

        globalLogoutButton = new Button("Log Out");
        globalLogoutButton.setVisible(false);
        globalLogoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                bodyPane.getChildren().add(productDetails.getAllProducts());
                globalLoginButton.setVisible(true);
                globalLogoutButton.setVisible(false);
                customerEmailLabel.setText("Welcome User");
                addToCartButton.setVisible(false);
                buyNowButton.setVisible(false);
                myCartButton.setVisible(false);
                messageLabel.setVisible(false);
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setMinSize(bodyPane.getMinWidth(), headerBar-10);
        gridPane.setVgap(5);
        gridPane.setHgap(5);
//        gridPane.setStyle("-fx-background-color: #C0C0C0");

        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(searchText, 0, 0);
        gridPane.add(searchButton, 1, 0);
        gridPane.add(globalLoginButton, 2, 0);
        gridPane.add(customerEmailLabel, 3, 0);
        gridPane.add(globalLogoutButton, 10, 0);

        return gridPane;
    }

    private GridPane loginPage() {
        Label emailLabel = new Label("Email");
        Label passwordLabel = new Label("Password");

        TextField emailTextField = new TextField();
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
               String email = emailTextField.getText();
               String password = passwordField.getText();
//               messageLabel.setText(email + " $$ " + password);
                if (login.customerLogin(email, password)){
                   customerEmail = email;
                   globalLoginButton.setVisible(false);
                    customerEmailLabel.setText("Welcome : " + customerEmail);
                   bodyPane.getChildren().clear();
                   bodyPane.getChildren().add(productDetails.getAllProducts());
                    globalLogoutButton.setVisible(true);
                    addToCartButton.setVisible(true);
                    buyNowButton.setVisible(true);
                    myCartButton.setVisible(true);
                    messageLabel.setVisible(true);
               }
               else {
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Login");
                    ButtonType type = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                    dialog.setContentText("Login failed Please try again");
                    dialog.getDialogPane().getButtonTypes().add(type);
                    dialog.showAndWait();
               }
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setMinSize(bodyPane.getMinWidth(), bodyPane.getMinHeight());
        gridPane.setVgap(5);
        gridPane.setHgap(5);
//        gridPane.setStyle("-fx-background-color: #C0C0C0");

        gridPane.setAlignment(Pos.CENTER);

        // first is x, second is y
        gridPane.add(emailLabel, 0, 0);
        gridPane.add(emailTextField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(loginButton, 0, 2);

        return gridPane;
    }

    private GridPane footerBar(){
        addToCartButton = new Button("Add To Cart");
        buyNowButton = new Button("Buy Now");
        myCartButton = new Button("My cart");
        messageLabel = new Label("");
        addToCartButton.setVisible(false);
        buyNowButton.setVisible(false);

        cart = FXCollections.observableArrayList();
        addToCartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Product selectedProduct = productDetails.getSelectedProduct();
                cart.add(selectedProduct);
                messageLabel.setText("Added to cart!");
            }
        });
        myCartButton.setVisible(false);
        myCartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //clear body
                bodyPane.getChildren().clear();
                TableColumn id = new TableColumn("Id");
                id.setCellValueFactory(new PropertyValueFactory<>("id"));
                TableColumn name = new TableColumn("Name");
                name.setCellValueFactory(new PropertyValueFactory<>("name"));
                TableColumn price = new TableColumn("Price");
                price.setCellValueFactory(new PropertyValueFactory<>("price"));

                cartTable = new TableView<>();
                cartTable.setItems(cart);
                cartTable.getColumns().addAll(id,name,price);
                cartTable.setMinSize(SupplyChain.width,SupplyChain.height);
                cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

                Pane tablePane=new Pane();
                tablePane.setMinSize(SupplyChain.width,SupplyChain.height);
                tablePane.getChildren().add(cartTable);

                bodyPane.getChildren().addAll(tablePane);

            }
        });

        buyNowButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Product selectedProduct = productDetails.getSelectedProduct();
                if (Order.placeOrder(customerEmail, selectedProduct)) {
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Order");
                    ButtonType type = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                    dialog.setContentText("Your Order is Placed");
                    dialog.getDialogPane().getButtonTypes().add(type);
                    dialog.showAndWait();
                    System.out.println("Order is Placed");
                }
                else {
                    messageLabel.setText("Order is not Placed");
                }
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setMinSize(bodyPane.getMinWidth(), headerBar-10);
        gridPane.setVgap(5);
        gridPane.setHgap(50);
//        gridPane.setStyle("-fx-background-color: #C0C0C0");

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setTranslateY(headerBar+height+5);

        gridPane.add(addToCartButton, 0, 0);
        gridPane.add(buyNowButton, 1, 0);
        gridPane.add(messageLabel, 2, 0);
        gridPane.add(myCartButton,5,0);

        return gridPane;
    }
    private Pane createContent() {
        Pane root = new Pane();
        root.setPrefSize(width, height+2*headerBar+10);

        bodyPane.setMinSize(width, height);
        bodyPane.setTranslateY(headerBar);

        bodyPane.getChildren().addAll(productDetails.getAllProducts());

        root.getChildren().addAll(headerBar(), bodyPane, footerBar());

        return root;
    }

    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(createContent());
        stage.setTitle("Supply Chain");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}