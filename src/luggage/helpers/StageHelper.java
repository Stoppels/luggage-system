/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 ITopia IS102-5
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package luggage.helpers;

import luggage.AppConfig;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import luggage.Debug;
import luggage.controllers.BaseController;

/**
 * StageHelper
 *
 * Makes creating and updating stages & scenes easier
 *
 * Package: luggage.helpers
 * @author ITopia IS102-5
 */
public class StageHelper {

    /**
     *
     */
    public static BaseController callbackController;

    /**
     *
     * @param sNewStage
     * @param oCurrentClass
     */
    public static void addPopup(String sNewStage, BaseController oCurrentClass) {
        StageHelper.addPopup(sNewStage, oCurrentClass, true, false);
    }

    /**
     *
     * @param sNewStage
     * @param oCurrentClass
     * @param bMaximized
     * @param bLocked
     */
    public static void addPopup(String sNewStage, BaseController oCurrentClass, boolean bMaximized, boolean bLocked) {
        try {
            long startTime = System.nanoTime();

            FXMLLoader primaryLoader = new FXMLLoader(oCurrentClass.getClass().getResource("/luggage/views/" + sNewStage + ".fxml"));
            Parent root = (Parent) primaryLoader.load();

            BaseController baseController = (BaseController) primaryLoader.getController();
            callbackController = oCurrentClass;

            Scene newScene = new Scene(root);
            newScene.getStylesheets().add("/resources/stylesheets/header.css");

            Stage oNewStage = new Stage();
            oNewStage.setScene(newScene);
            oNewStage.getIcons().add(new Image("/resources/images/logo_red.png"));
            oNewStage.setTitle(AppConfig.ApplicationName + " " + sNewStage);
            oNewStage.setMinHeight(AppConfig.MinHeight);
            oNewStage.setMinWidth(AppConfig.MinWidth);
            oNewStage.initModality(Modality.APPLICATION_MODAL);

            if (bLocked) {
                oNewStage.setMaxHeight(AppConfig.MinHeight);
                oNewStage.setMaxWidth(AppConfig.MinWidth);
            }

            oNewStage.setMaximized(bMaximized);

            oNewStage.showAndWait();

            long endTime = System.nanoTime();
            long microseconds = ((endTime - startTime) / 1000);
            Debug.print("Add stage: " + sNewStage + " took " + microseconds + " microseconds.");
        } catch (IOException ex) {
            Logger.getLogger(StageHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param sNewStage
     * @param oCurrentClass
     */
    public static void addStage(String sNewStage, BaseController oCurrentClass) {
        StageHelper.addStage(sNewStage, oCurrentClass, true, false);
    }

    /**
     *
     * @param sNewStage
     * @param oCurrentClass
     * @param bMaximized
     * @param bLocked
     */
    public static void addStage(String sNewStage, BaseController oCurrentClass, boolean bMaximized, boolean bLocked) {
        try {
            long startTime = System.nanoTime();

            FXMLLoader primaryLoader = new FXMLLoader(oCurrentClass.getClass().getResource("/luggage/views/" + sNewStage + ".fxml"));
            Parent root = (Parent) primaryLoader.load();

            BaseController baseController = (BaseController) primaryLoader.getController();
            callbackController = oCurrentClass;

            Scene newScene = new Scene(root);
            newScene.getStylesheets().add("/resources/stylesheets/header.css");

            Stage oNewStage = new Stage();
            oNewStage.setScene(newScene);
            oNewStage.getIcons().add(new Image("/resources/images/logo_red.png"));
            oNewStage.setTitle(AppConfig.ApplicationName + " " + sNewStage);
            oNewStage.setMinHeight(AppConfig.MinHeight);
            oNewStage.setMinWidth(AppConfig.MinWidth);
            oNewStage.initModality(Modality.APPLICATION_MODAL);

            if (bLocked) {
                oNewStage.setMaxHeight(AppConfig.MinHeight);
                oNewStage.setMaxWidth(AppConfig.MinWidth);
            }

            oNewStage.setMaximized(bMaximized);

            oNewStage.show();

            long endTime = System.nanoTime();
            long microseconds = ((endTime - startTime) / 1000);
            Debug.print("Add stage: " + sNewStage + " took " + microseconds + " microseconds.");
        } catch (IOException ex) {
            Logger.getLogger(StageHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param oCurrentStage
     * @param sNewStage
     * @param oCurrentClass
     */
    public static void replaceStage(Stage oCurrentStage, String sNewStage, BaseController oCurrentClass) {
        StageHelper.addStage(sNewStage, oCurrentClass);
        oCurrentStage.close();
    }

    /**
     *
     * @param oCurrentStage
     */
    public static void closeStage(Stage oCurrentStage) {
        oCurrentStage.close();
    }

}
