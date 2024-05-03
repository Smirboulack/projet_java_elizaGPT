package fr.univ_lyon1.info.m1.elizagpt.view;

import java.io.File;

import fr.univ_lyon1.info.m1.elizagpt.controller.MainController;
import fr.univ_lyon1.info.m1.elizagpt.controller.searchStrategy.SearchStrategy;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Main class of the View (GUI) of the application.
 */
public class MainView {
    private VBox dialog;
    private final VBox root;
    private TextField text = null;
    private TextField searchText = null;
    private Label searchTextLabel = null;
    private ComboBox<SearchStrategy> strategySelect;
    private MainController controller;
    private final ImageView imagePreview;
    private File selectedImageFile;
    private Button themeToggleButton;
    private ImageView toggleImageView;
    private MessageVisual messageVisual;

    /**
     * Main class of the View (GUI) of the application.
     */
    public MainView(final Stage stage, final double width, final double height) {
        stage.setTitle("Eliza GPT");
        root = new VBox(10);
        root.setStyle("-fx-background-color: #FFFFFF;");
        this.strategySelect = new ComboBox<>();
        final Pane search = createSearchWidget();
        root.getChildren().addAll(search);

        this.selectedImageFile = null;

        this.dialog = new VBox(10);
        dialog.setStyle("-fx-background-color: #FFFFFF;");

        ScrollPane dialogScroll = new ScrollPane();
        dialogScroll.setContent(dialog);
        dialogScroll.vvalueProperty().bind(dialog.heightProperty());

        root.getChildren().add(dialogScroll);
        dialogScroll.setFitToWidth(true);

        final Pane input = createInputWidget();
        root.getChildren().add(input);

        this.imagePreview = new ImageView();
        imagePreview.setFitHeight(100);
        imagePreview.setFitWidth(100);
        imagePreview.setPreserveRatio(true);
        root.getChildren().add(imagePreview);

        // Everything's ready: add it to the scene and display it
        final Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        text.requestFocus();
        stage.show();
    }

    /**
     * Sets the MainController associated with the MainView.
     *
     * @param controller The MainController associated with the MainView.
     */
    public void setController(final MainController controller) {
        this.controller = controller;

    }

    /**
     * Sets the message visual.
     */
    public void setMessageVisual() {
        this.messageVisual = MessageVisual.getInstance(controller);
    }

    /**
     * Displays a text message visual in the dialog using the provided message
     * parts.
     *
     * @param parts An array of String parts representing the components of the text
     *              message.
     */
    public void displayTxtMessage(final String[] parts) {
        MessageVisual messageVisual = MessageVisual.getInstance(controller);
        messageVisual.createTxtMessageVisual(parts);
        this.dialog.getChildren().add(messageVisual.getMessageVisual());
    }

    /**
     * Displays an image message visual in the dialog using the provided message
     * parts.
     *
     * @param parts An array of String parts representing the components of the
     *              image message.
     */
    public void displayImageMessage(final String[] parts) {
        MessageVisual messageVisual = MessageVisual.getInstance(controller);
        messageVisual.createImageMessageVisual(parts);
        this.dialog.getChildren().add(messageVisual.getMessageVisual());
    }

    /**
     * Displays a combined message visual.
     *
     * @param parts      An array of String parts representing the components
     *                   of the combined message.
     * @param messageUrl The URL of the image associated with the combined message.
     */
    public void displayCombinedMessage(final String[] parts, final String messageUrl) {
        MessageVisual messageVisual = MessageVisual.getInstance(controller);
        messageVisual.createCombinedMessageVisual(parts, messageUrl);
        this.dialog.getChildren().add(messageVisual.getMessageVisual());
    }

    /**
     * Create the search widget.
     */
    private Pane createSearchWidget() {
        final HBox firstLine = new HBox(10);

        searchText = new TextField();
        searchText.setOnAction(e -> controller.searchText(searchText.getText()));

        // Configuration du ComboBox
        strategySelect.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        controller.changeSearchStrategy(newValue);
                    }
                });

        firstLine.getChildren().addAll(searchText, strategySelect);

        final Button send = new Button("Search");
        send.setOnAction(e -> controller.searchText(searchText.getText()));
        searchTextLabel = new Label();
        final Button undo = new Button("Undo search");
        undo.setOnAction(e -> controller.undoSearch());
        final HBox secondLine = new HBox(10);
        secondLine.setAlignment(Pos.BASELINE_LEFT);
        secondLine.getChildren().addAll(send, searchTextLabel, undo);

        final VBox input = new VBox(5);
        input.getChildren().addAll(firstLine, secondLine);
        return input;
    }

    /**
     * Initializes the ComboBox with the list of SearchStrategy items.
     * Associates the ComboBox with the provided ObservableList, sets up the
     * converter
     * for displaying strategy names, and selects the first strategy by default.
     *
     * @param listeObservable The ObservableList containing
     *                        SearchStrategy items to populate the ComboBox.
     */
    public void initializeComboBox(final ObservableList<SearchStrategy> listeObservable) {
        this.strategySelect.setItems(listeObservable);
        this.strategySelect.setConverter(new StringConverter<SearchStrategy>() {

            @Override
            public String toString(final SearchStrategy object) {
                return object.getClass().getSimpleName();
            }

            @Override
            public SearchStrategy fromString(final String string) {
                return null;
            }
        });
        this.getComboBox().getSelectionModel().selectFirst();
    }

    /**
     * Create the input widget.
     */
    private Pane createInputWidget() {
        final HBox input = new HBox(10); // Espace entre les composants

        // Création du bouton de sélection de fichier avec une icône
        Image fileIcon = new Image("file:src/main/resources/image_logo.png");
        ImageView iconView = new ImageView(fileIcon);
        iconView.setFitHeight(20);
        iconView.setFitWidth(20);

        final Button fileButton = new Button("", iconView);
        fileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Images", "*.*"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                    new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                    new FileChooser.ExtensionFilter("SVG", "*.svg")

            );
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                selectedImageFile = file;
                Image image = new Image(file.toURI().toString());
                imagePreview.setImage(image);
            }
        });

        text = new TextField();
        text.setOnAction(e -> {
            controller.processUserInput(text.getText(), selectedImageFile);
            this.text.setText("");
            selectedImageFile = null;
            imagePreview.setImage(null);
        });

        final Button send = new Button("Send");
        send.setOnAction(e -> {
            controller.processUserInput(text.getText(), selectedImageFile);
            text.setText("");
            selectedImageFile = null;
            imagePreview.setImage(null);
        });

        input.getChildren().addAll(text, send, fileButton);
        input.getChildren().add(darkModBox());
        return input;
    }

    /**
     * Create the dark mode button.
     */
    private HBox darkModBox() {
        // Chargement des images
        Image moonImage = new Image("file:src/main/resources/moon.png");
        Image sunImage = new Image("file:src/main/resources/sun.png");

        // Créer le bouton avec l'image de la lune
        toggleImageView = new ImageView(moonImage);
        toggleImageView.setFitHeight(20);
        toggleImageView.setFitWidth(20);

        this.themeToggleButton = new Button("", toggleImageView);
        themeToggleButton.setText("Dark");

        themeToggleButton.setOnAction(event -> {
            // Changer le thème et l'image
            if (themeToggleButton.getText().equals("Dark")) {
                toggleImageView.setImage(sunImage);
                controller.changeViewsTheme("Dark");
            } else {
                toggleImageView.setImage(moonImage);
                controller.changeViewsTheme("Light");
            }
        });

        // Ajouter et retourne le bouton en haut à droite
        return new HBox(themeToggleButton);
    }

    /**
     * Used to modify items from dark theme to white theme.
     */
    public void whiteTheme() {
        this.themeToggleButton.setText("Dark");
        this.toggleImageView.setImage(new Image("file:src/main/resources/moon.png"));
        this.root.setStyle(null);
        this.dialog.setStyle(null);
    }

    /**
     * .
     * Used to modify items from white theme to dark theme
     */
    public void darkTheme() {
        this.themeToggleButton.setText("Light");
        this.toggleImageView.setImage(new Image("file:src/main/resources/sun.png"));
        this.root.setStyle("-fx-background-color: #000000;");
        this.dialog.setStyle("-fx-background-color: #323232;");
    }

    /**
     * Return the search text field.
     * 
     * @return the search text field.
     */
    public TextField getSearchText() {
        return searchText;
    }

    /**
     * Update the text of the search label.
     * 
     * @param text
     */
    public void updateSearchLabel(final String text) {
        searchTextLabel.setText(text);
    }

    /**
     * Return the search text label.
     * 
     * @return searchTextLabel
     */
    public Label getSearchTextLabel() {
        return searchTextLabel;
    }

    /*
     * return the text of the search text field.
     */
    public String getInputText() {
        return text.getText();
    }

    /**
     * clear the search text field.
     */
    public void clearSearchText() {
        searchText.setText("");
    }

    /**
     * clear the input text field.
     */
    public void clearInputText() {
        text.setText("");
    }

    /**
     * Return the ComboBox.
     * 
     * @return the ComboBox.
     */
    public ComboBox<SearchStrategy> getComboBox() {
        return strategySelect;
    }

    /**
     * Set the ComboBox.
     * 
     */
    public void setComboBox(final ComboBox<SearchStrategy> comboBox) {
        this.strategySelect = comboBox;
    }

    /**
     * Return the dialog.
     * 
     * @return the dialog.
     */
    public VBox getDialog() {
        return dialog;
    }

    /**
     * Set the dialog.
     * 
     * @param dialog
     */
    public void setDialog(final VBox dialog) {
        this.dialog = dialog;
    }

    /**
     * Return the root.
     * 
     * @return the root.
     */
    public VBox getRoot() {
        return root;
    }

    /**
     * Return the theme toggle button.
     * 
     * @return themeToggleButton
     */
    public Button getThemeToggleButton() {
        return themeToggleButton;
    }

    /**
     * Set the theme toggle button.
     * 
     * @param themeToggleButton
     */
    public void setThemeToggleButton(final Button themeToggleButton) {
        this.themeToggleButton = themeToggleButton;
    }

    /**
     * Return the toggle image view.
     * 
     * @return toggleImageView
     */
    public ImageView getToggleImageView() {
        return toggleImageView;
    }

    /**
     * Set the toggle image view.
     * 
     * @param toggleImageView
     */
    public void setToggleImageView(final ImageView toggleImageView) {
        this.toggleImageView = toggleImageView;
    }
}
