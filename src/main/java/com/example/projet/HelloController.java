package com.example.projet;

import com.example.projet.filter.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.projet.filter.DatabaseManager.getConnection;

public class HelloController {

    @FXML private ImageView backgroundImage;
    @FXML private ImageView loadedImage;
    @FXML private Button rotateButton;
    @FXML private Button symmetryButton;
    @FXML private Button horizontalsymmetryButton;
    @FXML private Button verticalSymmetryButton;
    @FXML private Button filtreButton;
    @FXML private Button sepiaButton;
    @FXML private Button grayscaleButton;
    @FXML private Button swapRGBButton;
    @FXML private Button sobelButton;
    @FXML private Button undoButton;
    @FXML private Button redoButton;
    @FXML private TextField tagInputField;
    @FXML private Pane rootPane;
    @FXML private Button scrambleButton;
    @FXML private Button decrambleButton;

    private File currentImageFile;
    private double rotationAngle = 0;
    private boolean filtersVisible = false;

    private boolean isRedoing = false;

    private Deque<Operation> historyStack = new ArrayDeque<>();
    private Deque<Operation> redoStack = new ArrayDeque<>();


    // Liste des transformations appliquées, pour les stocker en base.
    private final List<String> transforms = new ArrayList<>();

    Connection conn;

    public HelloController() throws SQLException {
        // Création de la table si elle n'existe pas
        DatabaseManager.initializeDatabase();
        conn = DriverManager.getConnection("jdbc:derby:monDB;create=true", "user", "password");
    }

    @FXML
    public void initialize() {
        // image de fond responsive
        backgroundImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("bg.jpg"))));
        backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());

        // Masquer tous les contrôles jusqu'à chargement
        rotateButton.setVisible(false);
        symmetryButton.setVisible(false);
        filtreButton.setVisible(false);

        sepiaButton.setVisible(filtersVisible);
        sepiaButton.setManaged(filtersVisible);

        grayscaleButton.setVisible(filtersVisible);
        grayscaleButton.setManaged(filtersVisible);

        swapRGBButton.setVisible(filtersVisible);
        swapRGBButton.setManaged(filtersVisible);

        sobelButton.setVisible(filtersVisible);
        sobelButton.setManaged(filtersVisible);

        scrambleButton.setVisible(false);
        decrambleButton.setVisible(false);

        undoButton.setVisible(false);
        redoButton.setVisible(false);


        horizontalsymmetryButton.setVisible(filtersVisible);
        horizontalsymmetryButton.setManaged(filtersVisible);

        verticalSymmetryButton.setVisible(filtersVisible);
        verticalSymmetryButton.setManaged(filtersVisible);

        //Gestion du chiffrement et déchiffrement
        scrambleButton.setOnAction(e -> encryptImage());
        decrambleButton.setOnAction(e -> decryptImage());


    }

    // Fonction pour charger et afficher les images
    @FXML public void handleLoadImage() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png","*.jpg","*.jpeg", "*.gif")
        );
        chooser.setInitialDirectory(new File("src/main/resources"));

        //Affiche une boîte de dialogue pour que l’utilisateur choisisse un fichier
        File f = chooser.showOpenDialog(rootPane.getScene().getWindow());
        //Si l'utilisateur annule, il ne fait rien (return).
        if (f == null) return;

        currentImageFile = f;
        Image img = new Image(f.toURI().toString());
        loadedImage.setImage(img);

        // supprimer historique + transformations
        historyStack.clear();
        redoStack.clear();
        transforms.clear();
        rotationAngle = 0;
        loadedImage.setRotate(0);
        loadedImage.setScaleX(1);
        loadedImage.setScaleY(1);

        // afficher les contrôles
        rotateButton.setVisible(true);
        symmetryButton.setVisible(true);
        filtreButton.setVisible(true);
        undoButton.setVisible(true);
        redoButton.setVisible(true);
        scrambleButton.setVisible(true);
        decrambleButton.setVisible(true);
    }

    //Fonction pour crypter une image
    @FXML
    public void encryptImage() {
        if (loadedImage.getImage() == null) {
            showAlert( "Erreur", "Aucune image à chiffrer.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Chiffrer l'image");
        dialog.setHeaderText("Mot de passe de chiffrement");
        dialog.setContentText("Entrez un mot de passe :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(password -> {
            loadedImage.setImage(ImageSecurity.encrypt(loadedImage.getImage(), password));
            transforms.add("secureScramble");
            showAlert("Succès", "Image chiffrée avec succès !");

            undoButton.setVisible(false);
            redoButton.setVisible(false);
        });
    }

    //Fonction pour decrypter une image
    @FXML
    public void decryptImage() {

        if (loadedImage.getImage() == null) {
            showAlert("Erreur", "Aucune image à dechiffrer.");
            return;
        }

        // Vérifiez si l'image a été chiffrée en recherchant la transformation "secureScramble"
        if (!transforms.contains("secureScramble")) {
            showAlert("Erreur", "L'image n'est pas chiffrée.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("dechiffrer l'image");
        dialog.setHeaderText("Mot de passe de dechiffrement");
        dialog.setContentText("Entrez un mot de passe :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(password -> {
            loadedImage.setImage(ImageSecurity.decrypt(loadedImage.getImage(), password));
            showAlert( "Succès", "Image dechiffrée avec succès !");
            undoButton.setVisible(true);
            redoButton.setVisible(true);
        });
    }

    //Fonction pour rechercher une image
    @FXML
    public void handleImageSearch() {
        // Réinitialiser l'état
        historyStack.clear();
        redoStack.clear();
        transforms.clear();
        rotationAngle = 0;
        loadedImage.setRotate(0);
        loadedImage.setScaleX(1);
        loadedImage.setScaleY(1);

        String tag = tagInputField.getText().trim();
        if (tag.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un tag pour la recherche.");
            return;
        }

        String sql = "SELECT image_url, transformations FROM APP.images_tags WHERE tag=?";
        try (Connection c = getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {

            st.setString(1, tag);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) {
                    showAlert("Recherche", "Aucune image pour le tag '" + tag + "'.");
                    return;
                }

                // Rendre les boutons visibles
                rotateButton.setVisible(true);
                symmetryButton.setVisible(true);
                filtreButton.setVisible(true);
                undoButton.setVisible(true);
                redoButton.setVisible(true);
                scrambleButton.setVisible(true);
                decrambleButton.setVisible(true);

                String url = rs.getString("image_url");
                String tx = rs.getString("transformations");

                boolean isEncrypted = tx != null && tx.contains("secureScramble");

                // Corriger currentImageFile si local
                if (url.startsWith("file:/")) {
                    String path = url.replace("file:/", "");
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    currentImageFile = new File(path);
                    if (!currentImageFile.exists()) {
                        currentImageFile = null;
                    }
                } else {
                    currentImageFile = null;
                }

                // Charger l'image (potentiellement chiffrée)
                Image img = new Image(url);
                Image finalImage = img;

                // Si l'image est chiffrée, demander le mot de passe et déchiffrer
                if (isEncrypted) {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Déchiffrement requis");
                    dialog.setHeaderText("Image protégée");
                    dialog.setContentText("Entrez le mot de passe :");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        String password = result.get();
                        try {
                            finalImage = ImageSecurity.decrypt(img, password);
                            finalImage = img;
                            showAlert("Succès", "Image déchiffrée avec succès !");
                        } catch (Exception e) {
                            showAlert("Erreur", "Mot de passe incorrect ou image invalide.");
                            return;
                        }
                    } else {
                        showAlert("Annulé", "Déchiffrement annulé.");
                        return;
                    }

                    // Nettoyer les transformations à appliquer (supprimer secureScramble)
                    if (tx != null) {
                        tx = Arrays.stream(tx.split(","))
                                .filter(op -> !op.equals("secureScramble"))
                                .collect(Collectors.joining(","));
                    }
                }

                // Afficher l’image (décryptée ou normale)
                loadedImage.setImage(finalImage);

                // Appliquer les transformations restantes
                applyStoredTransforms(tx);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Erreur", "Impossible de récupérer l'image.");
        }
    }


    private void applyStoredTransforms(String txCsv) {
        if (txCsv == null || txCsv.isBlank()) return;
        for (String op : txCsv.split(",")) {
            switch (op) {
                case "rotate": handleRotateImage();      break;
                case "symetryH":  handleHorizontalSymmetry(); break;
                case "symetryV":  handleVerticalSymmetry();   break;
                case "sepia":  handleSepia();              break;
                case "grayscale": handleGrayscale();       break;
                case "swapRGB": handleSwapRGB();           break;
                case "sobel":  handleSobel();              break;

                // Ne surtout pas réappliquer un chiffrage déjà géré manuellement
                case "secureScramble":
                    System.out.println("secureScramble ignoré (déjà déchiffré manuellement)");
                    break;

                default:
                    System.out.println("Transformation inconnue ignorée : " + op);
            }
        }
    }

    private ImageViewState captureViewState() {
        return new ImageViewState(
                loadedImage.getRotate(),
                loadedImage.getScaleX(),
                loadedImage.getScaleY()
        );
    }

    private void restoreViewState(ImageViewState state) {
        loadedImage.setRotate(state.getRotationAngle());
        loadedImage.setScaleX(state.getSymetryScaleX());
        loadedImage.setScaleY(state.getSymetryScaleY());

        //  On met à jour la variable rotationAngle !
        rotationAngle = state.getRotationAngle();
    }


    @FXML
    public void handleRotateImage() {
        if (!isRedoing) {
            historyStack.push(new Operation(Operation.Type.TRANSFORM, null, captureViewState(), "rotate"));
            redoStack.clear();
        }

        rotationAngle = (rotationAngle + 45) % 360;
        loadedImage.setRotate(rotationAngle);
        transforms.add("rotate");
    }


    @FXML
    public void handleHorizontalSymmetry() {
        if (!isRedoing) {
            historyStack.push(new Operation(Operation.Type.TRANSFORM, null, captureViewState(), "symetryH"));
            redoStack.clear();
        }

        loadedImage.setScaleX(loadedImage.getScaleX() == 1 ? -1 : 1);
        transforms.add("symetryH");
    }


    @FXML
    public void handleVerticalSymmetry() {
        if (!isRedoing) {
            historyStack.push(new Operation(Operation.Type.TRANSFORM, null, captureViewState(), "symetryV"));
            redoStack.clear();
        }
        loadedImage.setScaleY(loadedImage.getScaleY() == 1 ? -1 : 1);
        transforms.add("symetryV");
    }

    @FXML
    public void handleSepia() {
        Image before = loadedImage.getImage();
        // is redoing si on ne met pas retablir car en haut c'est false
        if (!isRedoing) {
            historyStack.push(new Operation(Operation.Type.FILTER, before, null, "sepia"));
            redoStack.clear();
        }
        loadedImage.setImage(new SepiaFilter().apply(before));
        transforms.add("sepia");
    }

    @FXML
    public void handleGrayscale() {
        Image before = loadedImage.getImage();
        if (!isRedoing) {
            historyStack.push(new Operation(Operation.Type.FILTER, before, null, "grayscale"));
            redoStack.clear();
        }
        loadedImage.setImage(new GrayScaleFilter().apply(before));
        transforms.add("grayscale");
    }

    @FXML
    //grace a before on enregistre l'image (nom) et les transformations qu'on appliquera alors de la recherche de l'image
    public void handleSwapRGB() {
        Image before = loadedImage.getImage();
        if (!isRedoing) {
            historyStack.push(new Operation(Operation.Type.FILTER, before, null, "swapRGB"));
            redoStack.clear();
        }

        loadedImage.setImage(new SwapRGBFilter().apply(before));
        transforms.add("swapRGB");
    }

    @FXML
    public void handleSobel() {
        Image before = loadedImage.getImage();
        if (!isRedoing) {
            historyStack.push(new Operation(Operation.Type.FILTER, before, null, "sobel"));
            redoStack.clear();
        }

        loadedImage.setImage(new SobelFilter().apply(before));
        transforms.add("sobel");
    }

    @FXML
    public void handleUndo() {
        if (historyStack.isEmpty()) return;

        Operation op = historyStack.pop();
        redoStack.push(op);
        transforms.remove(transforms.size() - 1);

        if (op.getType() == Operation.Type.FILTER && op.getImageBefore() != null) {
            loadedImage.setImage(op.getImageBefore());
        } else if (op.getType() == Operation.Type.TRANSFORM && op.getViewStateBefore() != null) {
            restoreViewState(op.getViewStateBefore());
        }
    }


    @FXML
    public void handleRedo() {
        if (redoStack.isEmpty()) return;

        Operation op = redoStack.pop();
        isRedoing = true; //stoppe l'enregistrement d'historique temporairement

        switch (op.getTransformationName()) {
            case "rotate": handleRotateImage(); break;
            case "symetryH": handleHorizontalSymmetry(); break;
            case "symetryV": handleVerticalSymmetry(); break;
            case "sepia": handleSepia(); break;
            case "grayscale": handleGrayscale(); break;
            case "swapRGB": handleSwapRGB(); break;
            case "sobel": handleSobel(); break;
        }

        historyStack.push(op); // on ré-enregistre l'opération refaite
        isRedoing = false;
    }


    @FXML public void handleSymmetry() {
        filtersVisible = !filtersVisible;
        horizontalsymmetryButton.setVisible(filtersVisible);
        horizontalsymmetryButton.setManaged(filtersVisible);
        verticalSymmetryButton.setVisible(filtersVisible);
        verticalSymmetryButton.setManaged(filtersVisible);
    }

    @FXML public void handleFiltre() {
        filtersVisible = !filtersVisible;
        sepiaButton.setVisible(filtersVisible);
        sepiaButton.setManaged(filtersVisible);
        grayscaleButton.setVisible(filtersVisible);
        grayscaleButton.setManaged(filtersVisible);
        swapRGBButton.setVisible(filtersVisible);
        swapRGBButton.setManaged(filtersVisible);
        sobelButton.setVisible(filtersVisible);
        sobelButton.setManaged(filtersVisible);
    }

    // -------------------gestion des tags / enregistrement ---------------------

    @FXML public void nouveauTag() {
        tagInputField.clear();
        tagInputField.setDisable(false);
        tagInputField.setStyle("");
    }

    @FXML
    public void handleAddTag() {
        String tag = tagInputField.getText().trim();
        if (tag.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un tag.");
            return;
        }
        if (currentImageFile == null) {
            showAlert("Erreur", "Aucune image chargée.");
            return;
        }
        String name = currentImageFile.getName();
        String url  = currentImageFile.toURI().toString();
        String txCsv = String.join(",", transforms);

        // Vérifier si l'image est chiffrée
        boolean isEncrypted = transforms.contains("secureScramble");
        if (isEncrypted) {
            // Ajouter une note dans les transformations pour indiquer que l'image est chiffrée
            txCsv += ",secureScramble";
        }

        // Vérifier si le tag existe déjà dans la base de données
        if (tagExists(tag)) {
            showAlert("Erreur", "Ce tag existe déjà pour une autre image.");
            return;  // Empêche l'enregistrement du tag s'il existe déjà
        }

        // Enregistrement
        String sql = "INSERT INTO APP.images_tags(image_name,image_url,tag,transformations) VALUES(?,?,?,?)";
        try (Connection c = getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, name);
            st.setString(2, url);
            st.setString(3, tag);
            st.setString(4, txCsv);
            st.executeUpdate();
            showAlert("Succès", "Image et transformations enregistrées.");
            // verrouiller le tag
            tagInputField.setDisable(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Erreur", "Impossible d’enregistrer : " + ex.getMessage());
        }
    }

    @FXML
    public void handleDeleteTag() {
        String tag = tagInputField.getText().trim();
        if (tag.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un tag à supprimer.");
            return;
        }

        // Vérifier que le tag existe
        if (!tagExists(tag)) {
            showAlert("Erreur", "Ce tag n'existe pas dans la base.");
            return;
        }

        // Demande de confirmation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le tag ?");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer le tag : '" + tag + "' ?");
       // Affiche la boîte de dialogue et attend que l'utilisateur clique sur un bouton (comme OK ou Annuler)
        Optional<javafx.scene.control.ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            String sql = "DELETE FROM APP.images_tags WHERE tag = ?";
            try (Connection c = getConnection();
                 PreparedStatement st = c.prepareStatement(sql)) {
                st.setString(1, tag);
                int deleted = st.executeUpdate();
                if (deleted > 0) {
                    showAlert("Succès", "Le tag a été supprimé avec succès.");
                    tagInputField.clear();
                    tagInputField.setDisable(false);
                } else {
                    showAlert("Erreur", "Impossible de supprimer ce tag.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Une erreur est survenue lors de la suppression : " + ex.getMessage());
            }
        }
    }


    // Fonction pour vérifier si un tag existe déjà dans la base de données
    private boolean tagExists(String tag) {
        boolean exists = false;
        String checkTagQuery = "SELECT COUNT(*) FROM APP.images_tags WHERE tag = ?";

        try (PreparedStatement stmt = conn.prepareStatement(checkTagQuery)) {
            stmt.setString(1, tag);
            try (ResultSet rs = stmt.executeQuery()) {
                //si on trouve au moins une colonne
                if (rs.next() && rs.getInt(1) > 0) {
                    exists = true;  // Le tag existe déjà
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la vérification du tag.");
        }
        return exists;

    }


    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}