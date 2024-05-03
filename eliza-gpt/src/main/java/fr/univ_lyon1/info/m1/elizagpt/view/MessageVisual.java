package fr.univ_lyon1.info.m1.elizagpt.view;

import fr.univ_lyon1.info.m1.elizagpt.controller.MainController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Represents the visual representation of a message, including text and image
 * components.
 * This class is a singleton.
 * It is associated with a MainController.
 */
public final class MessageVisual {
    private static MessageVisual instance;
    private VBox messageVisual;
    private final Image trashImage;
    private ImageView imageView;
    private final MainController controller;

    public static final String BASE_STYLE = "-fx-padding: 8px; "
            + "-fx-margin: 5px; "
            + "-fx-background-radius: 5px;";
    public static final String USER_STYLE = "-fx-background-color: #A0E0A0; " + BASE_STYLE;
    public static final String ELIZA_STYLE = "-fx-background-color: #A0A0E0; " + BASE_STYLE;

    /**
     * Private constructor.
     *
     * @param controller The MainController associated with the instance.
     */
    private MessageVisual(final MainController controller) {
        this.messageVisual = new VBox();
        this.imageView = null;
        this.trashImage = new Image("file:src/main/resources/trash.png");
        this.controller = controller;
    }

    /**
     * Gets the singleton instance of MessageVisual associated with the provided
     * MainController.
     *
     * @param controller The MainController associated with the instance.
     * @return The MessageVisual instance.
     */
    public static synchronized MessageVisual getInstance(final MainController controller) {
        if (instance == null) {
            instance = new MessageVisual(controller);
        }
        return instance;
    }

    /**
     * Gets the VBox representing the visual display of a message.
     *
     * @return The message visual VBox.
     */
    public VBox getMessageVisual() {
        return messageVisual;
    }

    /**
     * Sets the message visual VBox.
     *
     * @param messageVisual The VBox representing the visual display of a message.
     */
    public void setMessageVisual(final VBox messageVisual) {
        this.messageVisual = messageVisual;
    }

    /**
     * Creates and displays the visual representation of a text message in the
     * dialog.
     *
     * @param message An array of String parts representing the components of the
     *                text message.
     */
    public void createTxtMessageVisual(final String[] message) {
        // Label for the time
        Label timeLabel = new Label(message[2]);
        timeLabel.setStyle("-fx-text-fill: grey; -fx-font-size: 10px;");

        // HBox for the message
        HBox outerHBox = new HBox();

        // Style and content of the message
        HBox innerHBox = new HBox(5.0);
        innerHBox.setStyle(message[1].equals("user") ? USER_STYLE : ELIZA_STYLE);
        Label messageLabel = new Label(message[3]);
        messageLabel.setWrapText(true);
        innerHBox.getChildren().add(messageLabel);

        outerHBox.getChildren().add(innerHBox);

        ImageView trashView = new ImageView(trashImage);
        trashView.setFitHeight(25);
        trashView.setFitWidth(25);
        Button deleteButton = new Button();
        deleteButton.setGraphic(trashView);
        deleteButton.setOnAction(event -> controller.deleteMessageViews(message[0]));

        VBox messageContainer = new VBox(2);
        messageContainer.getChildren().add(timeLabel);
        messageContainer.setAlignment(message[1].equals("user")
                ? Pos.CENTER_RIGHT
                : Pos.CENTER_LEFT);

        HBox container = new HBox(5);
        messageContainer.setId(message[0]);
        container.getChildren().addAll(outerHBox, deleteButton);
        container.setAlignment(message[1].equals("user") ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        messageContainer.getChildren().add(container);
        messageVisual = messageContainer;

    }

    /**
     * Creates and displays the visual representation of an image message in the
     * dialog.
     *
     * @param message An array of String parts representing the components of the
     *                image message.
     */
    public void createImageMessageVisual(final String[] message) {
        // Label for the time
        Label timeLabel = new Label(message[2]);
        timeLabel.setStyle("-fx-text-fill: grey; -fx-font-size: 10px;");

        // Creates an ImageView for the image
        Image image = new Image("file:" + message[3]);
        this.imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);

        // HBox for the message
        HBox outerHBox = new HBox();

        // Style and content of the message
        HBox innerHBox = new HBox(5.0);
        innerHBox.setStyle(message[1].equals("user") ? USER_STYLE : ELIZA_STYLE);
        innerHBox.getChildren().add(imageView);

        outerHBox.getChildren().add(innerHBox);

        ImageView trashView = new ImageView(trashImage);
        trashView.setFitHeight(25);
        trashView.setFitWidth(25);
        Button deleteButton = new Button();
        deleteButton.setGraphic(trashView);
        deleteButton.setOnAction(event -> controller.deleteMessageViews(message[0]));

        VBox messageContainer = new VBox(2);
        messageContainer.getChildren().add(timeLabel);
        messageContainer.setAlignment(message[1].equals("user")
                ? Pos.CENTER_RIGHT
                : Pos.CENTER_LEFT);

        HBox container = new HBox(5);
        messageContainer.setId(message[0]);
        container.getChildren().addAll(outerHBox, deleteButton);
        container.setAlignment(message[1].equals("user") ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        messageContainer.getChildren().add(container);
        messageVisual = messageContainer;
    }

    /**
     * Creates and displays the visual representation of a combined message
     * with text and image in the dialog.
     *
     * @param message    An array of String parts representing the components of the
     *                   combined message.
     * @param messageUrl The URL of the image associated with the combined message.
     */
    public void createCombinedMessageVisual(final String[] message, final String messageUrl) {
        // Label for the time
        Label timeLabel = new Label(message[2]);
        timeLabel.setStyle("-fx-text-fill: grey; -fx-font-size: 10px;");

        // Creates an ImageView for the image
        Image image = new Image("file:" + messageUrl);
        this.imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);

        // HBox for the message
        HBox outerHBox = new HBox();

        // Style and content of the message
        HBox innerHBox = new HBox(5.0);
        innerHBox.setStyle(message[1].equals("user") ? USER_STYLE : ELIZA_STYLE);
        Label messageLabel = new Label(message[3]);
        messageLabel.setWrapText(true);
        innerHBox.getChildren().add(messageLabel);
        innerHBox.getChildren().add(imageView);

        outerHBox.getChildren().add(innerHBox);

        ImageView trashView = new ImageView(trashImage);
        trashView.setFitHeight(25);
        trashView.setFitWidth(25);
        Button deleteButton = new Button();
        deleteButton.setGraphic(trashView);
        deleteButton.setOnAction(event -> controller.deleteMessageViews(message[0]));

        VBox messageContainer = new VBox(2);
        messageContainer.getChildren().add(timeLabel);
        messageContainer.setAlignment(message[1].equals("user")
                ? Pos.CENTER_RIGHT
                : Pos.CENTER_LEFT);

        HBox container = new HBox(5);
        messageContainer.setId(message[0]);
        container.getChildren().addAll(outerHBox, deleteButton);
        container.setAlignment(message[1].equals("user") ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        messageContainer.getChildren().add(container);
        messageVisual = messageContainer;
    }

}
