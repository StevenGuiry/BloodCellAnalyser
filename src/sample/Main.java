package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Blood Cell Analyser");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {

//        DisjointSet disjointSet = new DisjointSet(15);
//
//        for (int id =0; id<disjointSet.size();id++){
//            System.out.println("The root of "+id+" is "+disjointSet.find(id));
//            System.out.println("==============================");
//        }
//        disjointSet.unify(3,4);
//        System.out.println("After Union");
//        for (int id =0; id<disjointSet.size();id++) {
//            System.out.println("The root of "+id+" is "+disjointSet.find(id));
//        }
//
//
//
//            int[] dset ={10,11,11,2,8,13,2,7,8,9,11,11,7,8};
//
//        System.out.println("Before ==============================");
//        for (int id=0;id<dset.length;id++)
//            System.out.println("The root of "+id+" is "+DisjointSet.find(dset,id)+" (element value: " +dset[id]+")");
//        DisjointSet.union(dset,3,4);
//        System.out.println("After ===============================");
//
//        for (int id=0;id<dset.length;id++)
//            System.out.println("The root of "+id+" is "+DisjointSet.find(dset,id)+ " (element value: "+dset[id]+")");
        launch(args);
    }
}
