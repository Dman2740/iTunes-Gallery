package cs1302.gallery;

import javafx.application.Application;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import com.google.gson.*;
import java.net.URL;
import java.net.URLEncoder;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;


/**
 * This GalleryApp class will allow the user to interact with the itunes
 * API and search any term in the query and have a grid show images
 * related to that term
 *
 *
 * @author Dario Ayala
 */
public class GalleryApp extends Application {


    public GridPane gallery=new GridPane();
    ArrayList<String> galleryUrls=new ArrayList<String>();
    ArrayList<String> extraUrls=new ArrayList<String>();
    public Label errorLabel=new Label("Not Enough Images Can Be Displayed");
    ProgressBar pBar=new ProgressBar(0.0);

    // I am instantiating the grid where the images will be displayed
    // as well as the label for errors and ArrayLists containing
    // urls
    
     /**
     * This start method is going to start displaying the grid
     * as well as contain the different actions each button
     * will do according to the user
     * @param stage the stage is where the user will ineract
     * with the gui
     */

    @Override
    public void start(Stage stage) {
        MenuBar menuBar=new MenuBar();
        Menu menuFile=new Menu("File");
        Menu help=new Menu("Help");
        MenuItem about=new MenuItem("About");
        menuBar.getMenus().addAll(menuFile,help);
        MenuItem exit=new MenuItem("Exit");
        // I am creating the toolbar at the top of the gallery


        // If the user presses the about button
        // then they will get a drop down option to
        // click on help and it will show a picture of me
        // and information about myself
        
         about.setOnAction(new EventHandler<ActionEvent>(){
                public void handle(ActionEvent event)
                {
                    Label info=new Label("Name:Dario Ayala ||" +
                                         "Email:darioayala98@gmail.com ||" +
                                          "Version Number 1");
                    Image mePic=new Image("https://scontent-at13-1.cdinstagram"+
                                       ".com/vp/ea7d598835fa93061c9a63b"+
                                       "30819a51e/5C676370/t51.2885-15"+
                                       "/e35/12081088_1635500193370831_"+
                                       "388969314_n.jpg");
                    ImageView viewMe=new ImageView(mePic);
                    StackPane stack=new StackPane();
                    stack.setAlignment(info,Pos.TOP_CENTER);
                    stack.setAlignment(viewMe,Pos.BOTTOM_CENTER);
                    stack.getChildren().addAll(info,viewMe);

                    Scene newScene=new Scene(stack,700,700);

                    Stage newStage=new Stage();
                    newStage.setTitle("About Me");
                    newStage.setScene(newScene);

                    newStage.initModality(Modality.APPLICATION_MODAL);
                     newStage.setWidth(700);
                    newStage.setHeight(700);
                    newStage.show();

                }//handle
            });//eventhandler



        // If the user presses the exit button the system will
        // exit
        exit.setOnAction(new EventHandler<ActionEvent>(){
                public void handle(ActionEvent t){
                    System.exit(0);
                }//handle
            });//eventhandler



        menuFile.getItems().addAll(exit);
        help.getItems().addAll(about);
        // these are populating the file dropdown and the
        // help dropdown
        
        
        Button pauseButton =new Button("Play");
        Button updateButton=new Button("Update Images");
        Label textLabel=new Label("Search");
        TextField textField=new TextField("Rock");
        HBox hb=new HBox(pauseButton,new Separator(),textLabel,textField,updateButton);
        hb.setSpacing(20);
        hb.setPadding(new Insets(30));

        // Creating the Play and Pause Buttons
        // as well as adding the spacing and insets

        Label progressBar=new Label("Progress:");
        Label copyright=new Label("All Images are directly from iTunes");
        HBox bottomBar= new HBox(progressBar,pBar,copyright);
        bottomBar.setPadding(new Insets(30));
        bottomBar.setSpacing(20);


        // This is the bottom of the library containing the progress bar
        // and copyright label


        // This will populate the grid with random images
        // I am using the random class in order to shuffle
        // the images I use

     EventHandler<ActionEvent> handle= e -> {
            Random rand=new Random();
            int j =rand.nextInt(5);
            int i=rand.nextInt(4);
            int index=rand.nextInt(galleryUrls.size());
            Image image=new Image(galleryUrls.get(index));
            ImageView view=new ImageView(image);
            gallery.add(view,j,i);
        };//eventHandler

        KeyFrame keyFrame=new KeyFrame(Duration.seconds(2),handle);
        Timeline timeLine=new Timeline();
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.getKeyFrames().add(keyFrame);
        // This istiantiates the delay of two seconds in order
        // for the images to cycle


        // Once the pause button equals pause then the timeline
        // and delay will stop but if the pause button is pause
        // which means the user has pressed it the timeline
        // and delay of images will be on
        //
        
         pauseButton.setOnAction(e -> {
                if(pauseButton.getText().equals("Pause"))
                    {
                        pauseButton.setText("Play");
                        timeLine.pause();
                    }//if
                else
                    {
                        pauseButton.setText("Pause");
                        timeLine.play();
                    }//else
            });//setOnAction

        // Once the update button is pressed I am creating a
        // urlList that will contain urls of the images found from
        // iTunes and if the user enters in a term it will
        // populate the grid with images and album covers
        // of whatever the user enters

        updateButton.setOnAction(new EventHandler<ActionEvent>(){
                ArrayList<String> urlList=new ArrayList<String>();
                 @Override
                public void handle(ActionEvent e)
                {
                    if((textField.getText() != null))
                        {
                            try
                                {
                                    pBar.setProgress(0.0);
                                    String userQuery=textField.getText();
                                    String qUrl="https://itunes.apple.com/search?term=" + URLEncoder.encode(userQuery,"UTF-8");
                                    URL url=new URL(qUrl);
                                    InputStreamReader reader=new InputStreamReader(url.openStream());
                                    JsonParser jp=new JsonParser();
                                    JsonElement je=jp.parse(reader);
                                    JsonObject root=je.getAsJsonObject();
                                    JsonArray results=root.getAsJsonArray("results");
                                    int numResults=results.size();
                                    //Taking all of the urls that are found based on the terms provided
                                    //and creating an integer of all the images
                                      for(int i=0;i<numResults;i++)
                                        {
                                            JsonObject result=results.get(i).getAsJsonObject();
                                            JsonElement artworkUrl100=result.get("artworkUrl100");
                                            if(artworkUrl100 != null)
                                                {
                                                    String artUrl=artworkUrl100.getAsString();
                                                    urlList.add(artUrl);
                                                }//if
                                        }//for
                                    showImg(urlList);
                                }//try
                            catch(UnsupportedEncodingException t)
                                {
                                    System.out.print("UnsupportedEncodingException");
                                }//catch
                            catch(MalformedURLException t)
                                {
                                    System.out.print("MalformedURlException");
                                }//catch
                            catch(IOException t)
                                {
                                    System.out.print("IOException");
                                }//catch
                        }//if
                }//handle
            });//actionHandler

        updateButton.fire();//makes the default query
        
        VBox grid=new VBox();
        hb.setAlignment(Pos.CENTER);
        gallery.setAlignment(Pos.CENTER);
        grid.getChildren().addAll(menuBar,hb);
        grid.getChildren().add(gallery);
        grid.getChildren().add(bottomBar);
        Scene scene=new Scene(grid);
        stage.setTitle("iTunes");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        textField.getText();
        //This is adding all of the buttons and
        //boxes that I created with the menuBar
        //and I am putting them all on the grid

    } //start
    
     /**
     * cleanList will remove all of the duplicates that
     * are found in the urls from the images
     * @param list this will be the urls collected
     * from itunes
     * @return list will be the urls without
     * duplicate images and it will be unique
     */
     
       public ArrayList deleteDuplicates(ArrayList<String> list)
    {

        if(list.size()>20)
            {
                for(int i=20;i<list.size();i++)
                    {
                        extraUrls.add(list.get(i));
                    }//for
            }//if
        for(int i=0;i<list.size();i++)
            {
                galleryUrls.add(list.get(i));
            }//for
        for(int i=0;i<list.size();i++)
            {
                for(int j=i+1;j<list.size();j++)
                    {
                        if(list.get(i).equals(list.get(j)))
                            {
                                list.remove(j);
                            }//if
                    }//for
            }//for
        return list;
    }//deleteDuplicates
 /**
   * showImg will populate the grid with images
   * collected from the itunes library and change
   * the progress bar to full once all the grid is
   * full
   * @param list is the arraylist of urls that are
   * collected from itunes
   */

    public void showImg(ArrayList<String> list)
    {
        galleryUrls.clear();
        deleteDuplicates(list);
        if(list.size() > 20)
            {
                gallery.getChildren().clear();
                int index=0;
                for(int a=0;a<5;a++)
                    {
                        for(int b=0;b<4;b++)
                            {
                                index++;
                                Image image=new Image(list.get(index));
                                ImageView view=new ImageView(image);
                                view.setFitWidth(100);
                                view.setFitHeight(100);
                                gallery.add(view,a,b);
                                pBar.setProgress(1.0);
                            }//for
                    }//for
            }//if
        else
            {
                gallery.getChildren().clear();
                gallery.getChildren().addAll(errorLabel);
            }//else
        //      System.out.println("The size of the list is:" + list.size());
        //      System.out.println("The size of the grid Urls is:" + gridUrls.size());
        list.clear();
    }//showImg
    
    /**
     * The main method will run the application
     * and run the galleryapp which contains the
     * itunes gallery and gui
     * @param args
     */

    public static void main(String[] args)
    {
        try {
            Application.launch(args);
        }//try
        catch (UnsupportedOperationException e)
            {
                System.out.println(e);
                System.err.println("If this is a DISPLAY problem, then your X server co\
nnection");
                System.err.println("has likely timed out. This can generally be fixed b\
y logging");
                System.err.println("out and logging back in.");
                System.exit(1);
            } // catch
    } // main
}




        
        
        
        
        

                                    

                
                
           
           
           

        

        


        
        
        
        
        
        
        
        
        





