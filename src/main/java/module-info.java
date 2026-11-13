module co.edu.uniquindio.edu.poo.nequi {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.uniquindio.edu.poo.nequi to javafx.fxml;
    exports co.edu.uniquindio.edu.poo.nequi;
}