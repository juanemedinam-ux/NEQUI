
module co.edu.uniquindio.edu.poo.nequi {

    requires javafx.controls;
    requires javafx.fxml;

    // PERMITIR EJECUTAR LA APLICACION
    exports co.edu.uniquindio.edu.poo.nequi;

    exports co.edu.uniquindio.edu.poo.nequi.app
            to javafx.graphics;

    // PERMITIR QUE FXML ACCEDA A LOS VIEWCONTROLLERS
    opens co.edu.uniquindio.edu.poo.nequi.viewcontrollers
            to javafx.fxml;

}
