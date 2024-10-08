package Client.game;

import java.util.*;
import java.util.List;

import Server.GameLogic;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class Gui extends Application {

	public static final int size = 30; 
	public static final int scene_height = size * 20 + 50;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right,hero_left,hero_up,hero_down;
	public static Image shoot_up,shoot_down,shoot_right,shoot_left,shoot_vertical,shoot_horizontal;
	public static Image shoot_up_hit,shoot_down_hit,shoot_right_hit,shoot_left_hit;
	public static Image random_PowerUp;


	private static Label[][] fields;
	private static TextArea scoreList;
	


	
	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		try {
			
			
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));

			Text mazeLabel = new Text("Maze:");
			mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	
			Text scoreLabel = new Text("Score:");
			scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			scoreList = new TextArea();
			
			GridPane boardGrid = new GridPane();

			image_wall  = new Image(getClass().getResourceAsStream("Image/wall4.png"),size,size,false,false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"),size,size,false,false);

			hero_right  = new Image(getClass().getResourceAsStream("Image/heroRight.png"),size,size,false,false);
			hero_left   = new Image(getClass().getResourceAsStream("Image/heroLeft.png"),size,size,false,false);
			hero_up     = new Image(getClass().getResourceAsStream("Image/heroUp.png"),size,size,false,false);
			hero_down   = new Image(getClass().getResourceAsStream("Image/heroDown.png"),size,size,false,false);

			shoot_up          = new Image(getClass().getResourceAsStream("Image/fireUp.png"),size,size,false,false);
			shoot_down        = new Image(getClass().getResourceAsStream("Image/fireDown.png"),size,size,false,false);
			shoot_right       = new Image(getClass().getResourceAsStream("Image/fireRight.png"),size,size,false,false);
			shoot_left        = new Image(getClass().getResourceAsStream("Image/fireLeft.png"),size,size,false,false);
			shoot_vertical    = new Image(getClass().getResourceAsStream("Image/fireVertical.png"),size,size,false,false);
			shoot_horizontal  = new Image(getClass().getResourceAsStream("Image/fireHorizontal.png"),size,size,false,false);

			shoot_up_hit      = new Image(getClass().getResourceAsStream("Image/fireWallNorth.png"),size,size,false,false);
			shoot_down_hit    = new Image(getClass().getResourceAsStream("Image/fireWallSouth.png"),size,size,false,false);
			shoot_right_hit   = new Image(getClass().getResourceAsStream("Image/fireWallEast.png"),size,size,false,false);
			shoot_left_hit    = new Image(getClass().getResourceAsStream("Image/fireWallWest.png"),size,size,false,false);
			random_PowerUp 	  = new Image(getClass().getResourceAsStream("Image/PowerUpFloor.png"),size, size, false, false);

			fields = new Label[20][20];
			for (int j=0; j<20; j++) {
				for (int i=0; i<20; i++) {
					switch (Generel.board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':					
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default: throw new Exception("Illegal field value: "+Generel.board[j].charAt(i) );
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}
			scoreList.setEditable(false);
			
			
			grid.add(mazeLabel,  0, 0); 
			grid.add(scoreLabel, 1, 0); 
			grid.add(boardGrid,  0, 1);
			grid.add(scoreList,  1, 1);
						
			Scene scene = new Scene(grid,scene_width,scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();

			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
				case UP:    playerMoved(0,-1,"up");    break;
				case DOWN:  playerMoved(0,+1,"down");  break;
				case LEFT:  playerMoved(-1,0,"left");  break;
				case RIGHT: playerMoved(+1,0,"right"); break;
				case SPACE: playerShoot(); break;
				case ESCAPE:System.exit(0); 
				default: break;
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void removePlayerOnScreen(pair oldpos) {
		Platform.runLater(() -> {
			fields[oldpos.getX()][oldpos.getY()].setGraphic(new ImageView(image_floor));
			});
	}
	
	public static void placePlayerOnScreen(pair newpos, String direction) {
		Platform.runLater(() -> {
			int newx = newpos.getX();
			int newy = newpos.getY();
			if (direction.equals("right")) {
				fields[newx][newy].setGraphic(new ImageView(hero_right));
			};
			if (direction.equals("left")) {
				fields[newx][newy].setGraphic(new ImageView(hero_left));
			};
			if (direction.equals("up")) {
				fields[newx][newy].setGraphic(new ImageView(hero_up));
			};
			if (direction.equals("down")) {
				fields[newx][newy].setGraphic(new ImageView(hero_down));
			};
			});
	}

	public static void placePowerUp(pair newpos) {
		Platform.runLater(() -> {
			int newx = newpos.getX();
			int newy = newpos.getY();
			fields[newx][newy].setGraphic(new ImageView(random_PowerUp));
		});
	}

	public static void removeAllPowerUps() {
		Platform.runLater(() -> {
			for (pair pu: Controller.powerUp) {
				fields[pu.x][pu.y].setGraphic(new ImageView(image_floor));
			}
		});
	}
	
	public static void movePlayerOnScreen(pair oldpos, pair newpos, String direction)
	{
		removePlayerOnScreen(oldpos);
		placePlayerOnScreen(newpos,direction);
	}

	public void playerMoved(int delta_x, int delta_y, String direction) {
		try {
			int playerId = Controller.getPlayerId();
			System.out.println("Player " + playerId + " wants to move " + delta_x + " to horizontal and " + delta_y + " vertical, facing " + direction);
			Controller.requestPlayerMove(playerId, delta_x, delta_y, direction);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//updateScoreTable();
	}

	public static void removeAllShots() {
		Platform.runLater(() -> {
			if (!Controller.tempShotPairs.isEmpty()) {
				for (pair tempShotPair : Controller.tempShotPairs) {
					fields[tempShotPair.getX()][tempShotPair.getY()].setGraphic(new ImageView(image_floor));
				}
			}
		});
	}

	public static void shoot(pair pos, String direction, boolean starShootingActive) {
		Platform.runLater(() -> {
			System.out.println("Shooting " + direction + " from x: " + pos.getX() + ", y: " + pos.getY());
			

			int x = pos.getX(), y = pos.getY();
			int checkAt = 0;
			pair shootHere = null;

			if (direction.equals("up") || starShootingActive) {
				boolean shootingAnnulled = false;
				boolean hitWall = false;
				checkAt = y - 1;
				if (Generel.board[checkAt].charAt(x) != 'w') {
					shootHere = new pair(x, checkAt);
					Controller.tempShotPairs.add(shootHere);
					fields[x][checkAt].setGraphic(new ImageView(shoot_up));
					checkAt--;
				} else {
					hitWall = true;
					shootingAnnulled = true;
				}
				while (!hitWall && checkAt >= 1) {
					if (Generel.board[checkAt].charAt(x) == 'w') {
						hitWall = true;
					} else {
						shootHere = new pair(x, checkAt);
						Controller.tempShotPairs.add(shootHere);
						fields[x][checkAt].setGraphic(new ImageView(shoot_vertical));
						checkAt--;
					}
				}
				if (!shootingAnnulled) {
					fields[x][++checkAt].setGraphic(new ImageView(shoot_up_hit));
				}
			}
			if (direction.equals("down") || starShootingActive) {
				boolean shootingAnnulled = false;
				boolean hitWall = false;
				checkAt = y + 1;
				if (Generel.board[checkAt].charAt(x) != 'w') {
					shootHere = new pair(x, checkAt);
					Controller.tempShotPairs.add(shootHere);
					fields[x][checkAt].setGraphic(new ImageView(shoot_down));
					checkAt++;
				} else {
					hitWall = true;
					shootingAnnulled = true;
				}
				while (!hitWall && checkAt <= 19) {
					if (Generel.board[checkAt].charAt(x) == 'w') {
						hitWall = true;
					} else {
						shootHere = new pair(x, checkAt);
						Controller.tempShotPairs.add(shootHere);
						fields[x][checkAt].setGraphic(new ImageView(shoot_vertical));
						checkAt++;
					}
				}
				if (!shootingAnnulled) {
					fields[x][--checkAt].setGraphic(new ImageView(shoot_down_hit));
				}
			}
			if (direction.equals("right") || starShootingActive) {
				boolean shootingAnnulled = false;
				boolean hitWall = false;
				checkAt = x + 1;
				if (Generel.board[y].charAt(checkAt) != 'w') {
					shootHere = new pair(checkAt, y);
					Controller.tempShotPairs.add(shootHere);
					fields[checkAt][y].setGraphic(new ImageView(shoot_right));
					checkAt++;
				} else {
					hitWall = true;
					shootingAnnulled = true;
				}
				while (!hitWall && checkAt <= 19) {
					if (Generel.board[y].charAt(checkAt) == 'w') {
						hitWall = true;
					} else {
						shootHere = new pair(checkAt, y);
						Controller.tempShotPairs.add(shootHere);
						fields[checkAt][y].setGraphic(new ImageView(shoot_horizontal));
						checkAt++;
					}
				}
				if (!shootingAnnulled) {
					fields[--checkAt][y].setGraphic(new ImageView(shoot_right_hit));
				}
			}
			if (direction.equals("left") || starShootingActive) {
				boolean shootingAnnulled = false;
				boolean hitWall = false;
				checkAt = x - 1;
				if (Generel.board[y].charAt(checkAt) != 'w') {
					shootHere = new pair(checkAt, y);
					Controller.tempShotPairs.add(shootHere);
					fields[checkAt][y].setGraphic(new ImageView(shoot_left));
					checkAt--;
				} else {
					hitWall = true;
					shootingAnnulled = true;
				}
				while (!hitWall && checkAt >= 1) {
					if (Generel.board[y].charAt(checkAt) == 'w') {
						hitWall = true;
					} else {
						shootHere = new pair(checkAt, y);
						Controller.tempShotPairs.add(shootHere);
						fields[checkAt][y].setGraphic(new ImageView(shoot_horizontal));
						checkAt--;
					}
				}
				if (!shootingAnnulled) {
					fields[++checkAt][y].setGraphic(new ImageView(shoot_left_hit));
				}
			}
		});
	}

	public static void playerShoot() {
		try {
			int playerId = Controller.getPlayerId();
			Controller.requestPlayerShoot(playerId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void updateScoreTable()
	{
		Platform.runLater(() -> {
			ArrayList<String> playerPoints = Controller.getPlayerPoints();
			StringBuilder sb = new StringBuilder();
			sb.append("Players and points\n\n");
			for (int i = 0; i < playerPoints.size(); i++) {
				sb.append(playerPoints.get(i));
				if (i < playerPoints.size() - 1) {
					sb.append("\n");
				}
			}
			scoreList.setText(sb.toString());
			});
	}



	
}

