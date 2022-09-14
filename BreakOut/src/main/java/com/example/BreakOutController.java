package com.example;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BreakOutController implements Initializable {

    @FXML private AnchorPane scene; //coordinate based scene area
    @FXML private Circle circle;
    @FXML private Rectangle paddle;

    private ArrayList<Rectangle> bricks = new ArrayList<>();
    Robot robot = new Robot();

    double maxSpeed = 4;
    double deltaX = -maxSpeed/2; //x move distance
    double deltaY = -maxSpeed/2; //y move distance

    private int paddleStartSize = 75;


    //1 Frame evey 10 milliseconds.
    // 1000 milliseconds in a second. That's 100 fps. The frequency that the game will update/check.
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            circle.setLayoutX(circle.getLayoutX() + deltaX);
            circle.setLayoutY(circle.getLayoutY() + deltaY);
            checkCollisionScene(scene);

            movePaddle();
            checkCollisionPaddle(paddle);
            paddle.setWidth(paddleStartSize);

            if(!bricks.isEmpty()){
                bricks.removeIf(brick -> checkCollisionBricks(brick));
            } else {
                timeline.stop();
            }
        }
    }));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createBricks();

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setRate(1); //1 is normal speed. 2 is double, etc. -1 is reverse.
        timeline.play();
    }

    public void createBricks(){
        int width = 550;
        int height = 100;
        int brickHeight = 20;
        int brickWidth = 40;

        for (int y = 10; y < height; y = y + brickHeight+2) {
            for (int x = 10; x < width ; x = x + brickWidth+2) {
                Rectangle rectangle = new Rectangle(x,y,brickWidth,brickHeight);
                rectangle.setFill(Color.RED);
                scene.getChildren().add(rectangle);
                bricks.add(rectangle);
            }
        }
    }

    public void checkCollisionScene(Node node){
        Bounds bounds = node.getBoundsInLocal();
        boolean rightBorder = circle.getLayoutX() >= (bounds.getMaxX() - circle.getRadius());
        boolean leftBorder = circle.getLayoutX() <= (bounds.getMinX() + circle.getRadius());
        boolean bottomBorder = circle.getLayoutY() >= (bounds.getMaxY() - circle.getRadius());
        boolean topBorder = circle.getLayoutY() <= (bounds.getMinY() + circle.getRadius());

        if (rightBorder || leftBorder) {
            deltaX *= -1;
        }
        if (topBorder) {
            deltaY *= -1;
        }
        if(bottomBorder){
            timeline.stop();
        }
    }

    public boolean checkCollisionBricks(Rectangle brick){

        if(circle.getBoundsInParent().intersects(brick.getBoundsInParent())){
            boolean rightBorder = circle.getLayoutX() >= ((brick.getX() + brick.getWidth()));
            boolean leftBorder = circle.getLayoutX() <= (brick.getX());
            boolean bottomBorder = circle.getLayoutY() >= ((brick.getY() + brick.getHeight()));
            boolean topBorder = circle.getLayoutY() <= (brick.getY());

            if (rightBorder || leftBorder) {
                deltaX *= -1;
            }else if (bottomBorder || topBorder) {
                deltaY *= -1;
            }
            scene.getChildren().remove(brick);
            return true;
        }
        return false;
    }

    public void movePaddle(){
        Bounds bounds = scene.localToScreen(scene.getBoundsInLocal());
        double sceneXPos = bounds.getMinX();

        double xPos = robot.getMouseX();
        double paddleWidth = paddle.getWidth();

        if(xPos >= sceneXPos + (paddleWidth/2) && xPos <= (sceneXPos + scene.getWidth()) - (paddleWidth/2)){
            paddle.setLayoutX(xPos - sceneXPos - (paddleWidth/2));
        } else if (xPos < sceneXPos + (paddleWidth/2)){
            paddle.setLayoutX(0);
        } else if (xPos > (sceneXPos + scene.getWidth()) - (paddleWidth/2)){
            paddle.setLayoutX(scene.getWidth() - paddleWidth);
        }
    }

    public void checkCollisionPaddle(Rectangle paddle){
        if(circle.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
            double ballPos = circle.getLayoutX() - paddle.getLayoutX()-(paddle.getWidth()/2);
            double ballPercent = (maxSpeed/2)/(paddle.getWidth()/2)*Math.abs(ballPos);

            if(ballPos<0){
                deltaX = -ballPercent;
                deltaY = -(maxSpeed - Math.abs(deltaX));
            }else{
                deltaX = ballPercent;
                deltaY = -(maxSpeed - deltaX);
            }
        }

    }


}