

import java.util.ArrayList;
import tester.Tester;
import javalib.impworld.*;
import java.awt.Color;
import java.util.Arrays;
import java.util.Random;
import javalib.worldimages.*;

// Represents a single square of the game area



//represnts any cell in the game
interface IConstants {
  int BOARDSIZEX = 500;
  int BOARDSIZEY = 650;
}

// the diffnet types of cells taht exist
interface ICell extends IConstants {
  WorldImage drawCell(Color color,  int cellSize);


  //floods teh cells in teh floodedcells arraylist of teh specified color
  void flood(ArrayList<Cell> floodedCells, Color color);

  //sets the color of the cell
  void setColor(Color color);

  //gets the color of the cell
  Color getColor();

  //gets the flooded value of the cell
  boolean getFlooded();

  //sets the flooded value of the cell
  void setFlooded(boolean flooded);

  //adds the cell to the flooded cells arraylist
  void floodCell(ArrayList<Cell> floodedCells);

  //checks if the cell is the same color as the other cell
  boolean sameColor(Color other);

}

// Represents a single square of the game area
class Cell implements ICell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  boolean visitedCell;


  // the four adjacent cells to this one
  ICell left;
  ICell top;
  ICell right;
  ICell bottom;

  // Constructor for Cell
  Cell(int x, int y, Color color) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = false;
    this.left = new MtCell();
    this.top =  new MtCell();
    this.right =  new MtCell();
    this.bottom =  new MtCell();
    this.visitedCell = false;
  }


  Cell(int x, int y, Color color, boolean flooded) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = false;
    this.left =  new MtCell();
    this.top = new MtCell();
    this.right =  new MtCell();
    this.bottom =  new MtCell();
    this.visitedCell = false;
  }

  // EFFECT: changes the right of the cell to the given cell
  void setRight(Cell cell) {
    this.right = cell;
    cell.left = this;
  }

  // EFFECT: changes the Bottom of the cell to the given cell
  void setBottom(Cell cell) {
    this.bottom = cell;
    cell.top = this;
  }


  //floods all the nessicary cells on the board
  public void flood(ArrayList<Cell> floodedCells, Color color) {
    if (this.color.equals(color) && !this.flooded) {
      floodedCells.add(this);
      this.flooded = true;
      this.floodConnections(floodedCells);
    }
  }

  //floods all the cells of teh same color that are connected to recelty flooded cell
  void floodConnections(ArrayList<Cell> floodedCells) {
    this.left.flood(floodedCells, this.color);
    this.right.flood(floodedCells, this.color);
    this.top.flood(floodedCells, this.color);
    this.bottom.flood(floodedCells, this.color);
  }

  //gets the color of the cell
  public Color getColor() {
    return this.color;
  }

  //gets the flooded value of the cell
  public boolean getFlooded() {
    return this.flooded;
  }


  //sets the flooded value of the cell
  public void setFlooded(boolean flooded) {
    this.flooded = flooded;
  }


  //adds the cell to the flooded cells arraylist
  public void floodCell(ArrayList<Cell> floodedCells) {
    floodedCells.add(this);
    this.flooded = true;
  }

  //checks if the cell is the same color as the other cell
  public boolean sameColor(Color other) {
    return this.color.equals(other);
  }


  //sets the color of the cell
  public void setColor(Color color) {
    this.color = color;
  }

  //draws the cell
  public WorldImage drawCell(Color color, int cellSize) {
    return new RectangleImage(cellSize, cellSize, OutlineMode.SOLID, color);
  }


}

//represnsta an empty Cell class
class MtCell implements ICell {

  Color color;

  MtCell() {
    this.color = Color.BLACK;
  }

  // EFFECT: Floods this cell and all adjacent cells of the same color
  public WorldImage drawCell(Color color, int cellSize) {
    return new RectangleImage(cellSize, cellSize, OutlineMode.SOLID, color);
  }

  //floods teh cells in teh floodedcells arraylist of teh specified color
  public void flood(ArrayList<Cell> floodedCells, Color color) {

    //should do nothing becasue its an empty cell
  }

  //sets the color of the cell
  public void setColor(Color color) {

    //should do nothing becasue its an empty cell
  }

  //gets the color of the cell
  public Color getColor() {
    return Color.WHITE;
  }

  //gets the flooded value of the cell
  public boolean getFlooded() {
    return false;
  }


  //sets the flooded value of the cell
  public void setFlooded(boolean flooded) {

    //should do nothing becasue its an empty cell
  }


  //adds the cell to the flooded cells arraylist
  public void floodCell(ArrayList<Cell> floodedCells) {

    //should do nothing becasue its an empty cell
  }


  //checks if the cell is the same color as the other cell
  public boolean sameColor(Color other) {
    return false;
  }


}


// Represents the entire game
class FloodItWorld extends World implements IConstants {
  int maxClicks;
  // All the cells of the game
  ArrayList<Cell> board;
  int cellSize;
  int size;
  int numColors;
  Random rand = new Random();
  Cell clickedOnCell;
  int index;
  int userClicks;
  ArrayList<Cell> floodedCells;



  // Constructor for FloodItWorld
  FloodItWorld(int size, int numColors, Random rand) {
    this.size = size;
    this.numColors = numColors;
    this.board = new ArrayList<Cell>();
    this.cellSize = BOARDSIZEX / size;
    this.index = 0;
    this.userClicks = 0;
    this.maxClicks = 0;

    this.floodedCells = new ArrayList<Cell>();

    if (numColors > 8) {
      throw new IllegalArgumentException("Too many colors");
    }
    if (numColors < 1) {
      throw new IllegalArgumentException("Too few colors");
    }

    makeBoard();

    if (size >= 10) {
      maxClicks = size + numColors + 5;
    }

    else if (size < 5) {
      maxClicks = size + numColors - 2;
    }

    else {
      maxClicks = size + numColors - 1;
    }

    this.clickedOnCell = new Cell(-1, -1, Color.WHITE);
  }

  // Constructor for FloodItWorld
  FloodItWorld(int size, int numColors) {
    this(size, numColors, new Random());
  }

  // makes the gameboard
  public ArrayList<Cell> makeBoard() {
    this.board = new ArrayList<Cell>();

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        int randColor = rand.nextInt(numColors);
        Cell cell = new Cell(i, j, getRandomColor(randColor));
        this.board.add(cell);
      }
    }

    for (int i = 0; i < this.board.size(); i++) {
      Cell cell = this.board.get(i);
      if (cell.x < this.size - 1) {
        cell.setRight(this.board.get(i + 1));
      }

      if (i < this.board.size() - this.size - 1) {
        cell.setBottom(this.board.get(i + this.size));
      }
    }
    this.floodedCells.add(this.board.get(0));
    this.board.get(0).flooded = true;
    this.board.get(0).floodConnections(floodedCells);
    return this.board;
  }


  // gets a random color
  public Color getRandomColor(int numColors) {
    ArrayList<Color> colorArrayList = new ArrayList<Color>(Arrays.asList(
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN,
            Color.MAGENTA, Color.ORANGE, Color.PINK));
    return colorArrayList.get(numColors);

  }


  // EFFECT: produces the visual representation of the game
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(BOARDSIZEX, BOARDSIZEY);
    for (Cell cell : this.board) {
      WorldImage cellImage =
              new RectangleImage(cellSize, cellSize, OutlineMode.SOLID, cell.color);
      scene.placeImageXY(cellImage, cell.x * cellSize + cellSize / 2,
              cell.y * cellSize + cellSize / 2);

    }
    scene.placeImageXY(new TextImage(userClicks +
            " / " + maxClicks,35, FontStyle.REGULAR, Color.BLACK), 250,  550);

    if (userClicks >= maxClicks) {
      scene.placeImageXY(new TextImage("You Lose", 50, FontStyle.BOLD, Color.BLACK),
              250,  250);
    }
    if (this.floodedCells.size() == this.board.size()) {
      scene.placeImageXY(new TextImage("You Win", 50, FontStyle.BOLD, Color.BLACK),
              250,  250);
    }

    return scene;
  }






  // EFFECT: updates the game
  public void onMousePressed(Posn pos) {
    // get the cell clicked
    for (Cell cell : this.board) {
      if (pos.x > cell.x * this.cellSize
              && pos.x < cell.x * this.cellSize + this.cellSize
              && pos.y > cell.y * this.cellSize
              && pos.y < cell.y * this.cellSize + this.cellSize) {

        this.clickedOnCell = cell;
      }
    }
    if (this.clickedOnCell.x == -1) {
      return;
    }
    userClicks++;
    // change the color of all current flooded cells
    updateFloodedCells();

  }


  // EFFECT: updates the flooded cells
  public void updateFloodedCells() {
    Color updatedColor = this.clickedOnCell.color;
    for (Cell cell : this.board) {
      if (cell.flooded) {
        cell.color = updatedColor;
      }
    }
    for (Cell cell : this.board) {
      if (cell.flooded) {
        cell.floodConnections(this.floodedCells);
      }
    }


  }




  // EFFECT: resets the game
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      userClicks = 0;
      this.board = this.makeBoard();

    }
  }



}





// Examples and tests for the FloodIt game
class ExamplesFloodIt implements IConstants {

  Cell red = new Cell(1, 1, Color.RED);
  Cell blue = new Cell(2, 3, Color.BLUE);
  Cell green = new Cell(0, 0, Color.GREEN);
  Cell yellow = new Cell(3, 0, Color.YELLOW);
  Cell cyan = new Cell(2, 2, Color.CYAN);
  Cell magenta = new Cell(0, 3, Color.MAGENTA);
  Cell orange = new Cell(0, 5, Color.ORANGE);
  Cell pink = new Cell(22, 0, Color.PINK);
  Random rand = new Random();
  FloodItWorld world1 = new FloodItWorld(2, 2, new Random(1));
  FloodItWorld world2 = new FloodItWorld(3, 3, new Random(2));

  // Tests for FloodItWorld
  void testFloodItWorld(Tester t) {
    FloodItWorld f = new FloodItWorld(10, 7, new Random());
    t.checkExpect(f.board.size(), 100);
    t.checkExpect(f.numColors, 7);
    t.checkExpect(f.size, 10);
    f.bigBang(this.BOARDSIZEX, this.BOARDSIZEY, 1.0);
    t.checkExpect(f.board.get(0).left, new MtCell());
    t.checkExpect(f.board.get(0).top, new MtCell());
    t.checkExpect(f.board.get(0).right, f.board.get(1));
    t.checkExpect(f.board.get(0).bottom, f.board.get(10));
    t.checkExpect(f.board.get(99).left, f.board.get(98));
    t.checkExpect(f.board.get(99).top, f.board.get(89));
    t.checkExpect(f.board.get(99).right, new MtCell());
    t.checkExpect(f.board.get(99).bottom, new MtCell());
    t.checkExpect(f.board.get(0).flooded, true);
    t.checkExpect(f.board.get(0).color, Color.RED);

  }

  //test for exceptions
  void testExeptions(Tester t) {
    t.checkConstructorException(new IllegalArgumentException("Too many colors"),
            "FloodItWorld", 3, 9, new Random(1));
    t.checkConstructorException(new IllegalArgumentException("Too few colors"),
            "FloodItWorld", 5, 0, new Random(2));
    t.checkConstructorException(new IllegalArgumentException("Too many colors"),
            "FloodItWorld", 4, 9);
    t.checkConstructorException(new IllegalArgumentException("Too few colors"),
            "FloodItWorld", 7, 0);
  }




  //test for draw cell
  void testDrawCell(Tester t) {
    t.checkExpect(red.drawCell(Color.RED, 10),
            new RectangleImage(10, 10, OutlineMode.SOLID, Color.RED));
    t.checkExpect(blue.drawCell(Color.BLUE, 10),
            new RectangleImage(10,10, OutlineMode.SOLID, Color.BLUE));
    t.checkExpect(green.drawCell(Color.GREEN, 10),
            new RectangleImage(10, 10, OutlineMode.SOLID, Color.GREEN));
    t.checkExpect(yellow.drawCell(Color.YELLOW, 10),
            new RectangleImage(10, 10, OutlineMode.SOLID, Color.YELLOW));
    t.checkExpect(cyan.drawCell(Color.CYAN, 10),
            new RectangleImage(10, 10, OutlineMode.SOLID, Color.CYAN));
    t.checkExpect(magenta.drawCell(Color.MAGENTA, 10),
            new RectangleImage(10, 10, OutlineMode.SOLID, Color.MAGENTA));
    t.checkExpect(orange.drawCell(Color.ORANGE, 10),
            new RectangleImage(10, 10, OutlineMode.SOLID, Color.ORANGE));
    t.checkExpect(pink.drawCell(Color.PINK, 15),
            new RectangleImage(15, 15, OutlineMode.SOLID, Color.PINK));
  }


  //test for getRandomColor
  void testGetRandomColor(Tester t) {
    FloodItWorld f = new FloodItWorld(10, 7, new Random());
    t.checkExpect(f.getRandomColor(0), Color.RED);
    t.checkExpect(f.getRandomColor(1), Color.GREEN);
    t.checkExpect(f.getRandomColor(2), Color.BLUE);
    t.checkExpect(f.getRandomColor(3), Color.YELLOW);
    t.checkExpect(f.getRandomColor(4), Color.CYAN);
    t.checkExpect(f.getRandomColor(5), Color.MAGENTA);
    t.checkExpect(f.getRandomColor(6), Color.ORANGE);
    t.checkExpect(f.getRandomColor(7), Color.PINK);
  }


  //test for make scene
  void testMakeScene(Tester t) {

    WorldScene scene1 = new WorldScene(500, 650);
    WorldScene scene2 = new WorldScene(500, 650);


    //world1.bigBang(500, 650, 1.0);
    scene1.placeImageXY(new RectangleImage(250, 250, OutlineMode.SOLID, Color.GREEN),
            125, 125);
    scene1.placeImageXY(new RectangleImage(250, 250, OutlineMode.SOLID, Color.RED),
            125, 375);
    scene1.placeImageXY(new RectangleImage(250, 250, OutlineMode.SOLID, Color.RED),
            375, 125);
    scene1.placeImageXY(new RectangleImage(250, 250, OutlineMode.SOLID, Color.RED),
            375, 375);
    t.checkExpect(world1.makeScene(), scene1);

    ////world2.bigBang(500, 650, 1.0);
    scene2.placeImageXY(new RectangleImage(166, 166, OutlineMode.SOLID, Color.GREEN),
            83, 83);
    scene2.placeImageXY(new RectangleImage(166, 166, OutlineMode.SOLID, Color.RED),
            83, 249);
    scene2.placeImageXY(new RectangleImage(166, 166, OutlineMode.SOLID, Color.BLUE),
            83, 415);
    scene2.placeImageXY(new RectangleImage(166, 166, OutlineMode.SOLID, Color.GREEN),
            249, 83);
    scene2.placeImageXY(new RectangleImage(166, 166, OutlineMode.SOLID, Color.RED),
            249, 249);
    scene2.placeImageXY(new RectangleImage(166, 166, OutlineMode.SOLID, Color.RED),
            249, 415);
    scene2.placeImageXY(new RectangleImage(166, 166, OutlineMode.SOLID, Color.RED),
            415, 83);
    scene2.placeImageXY(new RectangleImage(166, 166, OutlineMode.SOLID, Color.RED),
            415, 249);
    scene2.placeImageXY(new RectangleImage(166, 166, OutlineMode.SOLID, Color.GREEN),
            415, 415);
    t.checkExpect(world2.makeScene(), scene2);



  }

  void testFlood(Tester t) {
    FloodItWorld f = new FloodItWorld(10, 7, new Random());
    ArrayList<Cell> floodedCells = new ArrayList<Cell>(Arrays.asList(f.board.get(0),
            f.board.get(1)));
    t.checkExpect(f.board.get(0).flooded, true);
    t.checkExpect(f.board.get(0).color, Color.RED);
    t.checkExpect(f.board.get(1).flooded,true);
    t.checkExpect(f.board.get(1).color, Color.MAGENTA);
    t.checkExpect(f.board.get(2).flooded,false);
    t.checkExpect(f.board.get(3).flooded,false);
    f.board.get(0).flood(floodedCells, f.board.get(0).color);
    t.checkExpect(f.board.get(0).flooded, true);
    t.checkExpect(f.board.get(0).color, Color.RED);
    t.checkExpect(f.board.get(1).flooded,true);
    t.checkExpect(f.board.get(1).color, Color.RED);


  }

  void testFloodConnections(Tester t) {
    FloodItWorld f = new FloodItWorld(10, 7, new Random());
    ArrayList<Cell> floodedCells = new ArrayList<Cell>(Arrays.asList(f.board.get(0),
            f.board.get(1)));
    t.checkExpect(f.board.get(0).flooded, true);
    t.checkExpect(f.board.get(0).color, Color.RED);
    t.checkExpect(f.board.get(1).flooded,true);
    t.checkExpect(f.board.get(1).color, Color.MAGENTA);
    t.checkExpect(f.board.get(2).flooded,false);
    t.checkExpect(f.board.get(3).flooded,false);
    f.board.get(0).floodConnections(floodedCells);
    t.checkExpect(f.board.get(0).flooded, true);
    t.checkExpect(f.board.get(0).color, Color.RED);
    t.checkExpect(f.board.get(1).flooded,true);
    t.checkExpect(f.board.get(1).color, Color.RED);
    t.checkExpect(f.board.get(2).flooded,true);
    t.checkExpect(f.board.get(3).flooded,true);
  }


  boolean testGetColor(Tester t) {
    Cell cell1 = new Cell(0, 0, Color.black);
    Cell cell2 = new Cell(0, 0, Color.red);
    Cell cell3 = new Cell(0, 0, Color.blue);
    Cell cell4 = new Cell(0, 0, Color.pink);

    return t.checkExpect(cell1.getColor(), Color.black)
            && t.checkExpect(cell2.getColor(), Color.red)
            && t.checkExpect(cell3.getColor(), Color.blue)
            && t.checkExpect(cell4.getColor(), Color.pink);
  }

  boolean testGetFlooded(Tester t) {
    Cell cell1 = new Cell(0, 0, Color.black, true);
    Cell cell2 = new Cell(0, 0, Color.red, false);
    Cell cell3 = new Cell(0, 0, Color.blue, false);
    Cell cell4 = new Cell(0, 0, Color.blue, true);

    return t.checkExpect(cell1.getFlooded(), true)
            && t.checkExpect(cell2.getFlooded(), false)
            && t.checkExpect(cell3.getFlooded(), false)
            && t.checkExpect(cell4.getFlooded(), true);
  }

  boolean testSetFlooded(Tester t) {
    Cell cell1 = new Cell(0, 0, Color.red);
    cell1.setFlooded(true);
    Cell cell2 = new Cell(0, 0, Color.black);
    cell2.setFlooded(false);
    Cell cell3 = new Cell(0, 0, Color.blue);
    cell3.setFlooded(true);

    return t.checkExpect(cell1.getFlooded(), true)
            && t.checkExpect(cell2.getFlooded(), false)
            && t.checkExpect(cell3.getFlooded(), true);
  }

  boolean testSetColor(Tester t) {
    Cell cell1 = new Cell(0, 0, Color.blue);
    cell1.setColor(Color.yellow);
    Cell cell2 = new Cell(0, 0, Color.black);
    cell2.setColor(Color.blue);
    Cell cell3 = new Cell(0, 0, Color.yellow);
    cell3.setColor(Color.black);

    return t.checkExpect(cell1.getColor(), Color.yellow)
            && t.checkExpect(cell2.getColor(), Color.blue)
            && t.checkExpect(cell3.getColor(), Color.black);
  }


  boolean testSetRight(Tester t) {
    Cell cell1 = new Cell(0, 0, Color.black);
    Cell rightCell1 = new Cell(0, 0, Color.blue);
    cell1.setRight(rightCell1);
    Cell cell2 = new Cell(0, 0, Color.black);
    Cell rightCell2 = new Cell(0, 0, Color.yellow);
    cell1.setRight(rightCell2);
    Cell cell3 = new Cell(0, 0, Color.black);
    Cell rightCell3 = new Cell(0, 0, Color.red);
    cell1.setRight(rightCell3);

    return t.checkExpect(cell1.right.equals(rightCell1))
            && t.checkExpect(cell2.right.equals(rightCell2))
            && t.checkExpect(cell3.right.equals(rightCell3));
  }

  void testSetBottom(Tester t) {
    Cell cell1 = new Cell(0, 0, Color.black);
    Cell bottomCell1 = new Cell(0, 0, Color.blue);
    cell1.setBottom(bottomCell1);
    Cell cell2 = new Cell(0, 0, Color.black);
    Cell bottomCell2 = new Cell(0, 0, Color.yellow);
    cell1.setBottom(bottomCell2);
    Cell cell3 = new Cell(0, 0, Color.black);
    Cell bottomCell3 = new Cell(0, 0, Color.red);
    cell1.setBottom(bottomCell3);

    return t.checkExpect(cell1.bottom.equals(bottomCell1))
            && t.checkExpect(cell2.bottom.equals(bottomCell2))
            && t.checkExpect(cell3.bottom.equals(bottomCell3));
  }


  boolean testSameColor(Tester t) {
    Cell cell1 = new Cell(0, 0, Color.black);
    Cell cell2 = new Cell(0, 0, Color.red);
    Cell cell3 = new Cell(0, 0, Color.blue);

    return t.checkExpect(cell1.sameColor(Color.black), true)
            && t.checkExpect(cell2.sameColor(Color.red))
            && t.checkExpect(cell3.sameColor(Color.blue));
  }


  void testOnMousePressed(Tester t) {
    FloodItWorld f = new FloodItWorld(10, 7, new Random());
    ArrayList<Cell> floodedCells = new ArrayList<Cell>(Arrays.asList(f.board.get(0),
            f.board.get(1)));
    f.onMousePressed(new Posn(2,2), "clicked");
    t.checkExpect(f.board.get(0).flooded, true);
    t.checkExpect(f.board.get(0).color, Color.GREEN);
    t.checkExpect(f.board.get(1).flooded, true);
    t.checkExpect(f.board.get(1).color, Color.GREEN);
    t.checkExpect(f.board.get(2).flooded, false);
    t.checkExpect(f.board.get(2).color, Color.BLUE);

    f.updateFloodedCells();

    t.checkExpect(f.board.get(0).flooded, true);
    t.checkExpect(f.board.get(0).color, Color.GREEN);
    t.checkExpect(f.board.get(1).flooded,true);
    t.checkExpect(f.board.get(1).color, Color.GREEN);
    t.checkExpect(f.board.get(2).flooded,true);
    t.checkExpect(f.board.get(3).flooded,true);


  }

  void testOnKeyEvent(Tester t) {
    FloodItWorld f = new FloodItWorld(10, 7, new Random());
    ArrayList<Cell> floodedCells = new ArrayList<Cell>(Arrays.asList(f.board.get(0),
            f.board.get(1)));
    t.checkExpect(f.board.get(0).color, Color.GREEN);
    t.checkExpect(f.board.get(1).flooded,false);
    t.checkExpect(f.board.get(1).color, Color.BLUE);
    t.checkExpect(f.board.get(2).flooded,false);
    t.checkExpect(f.board.get(3).flooded,false);
    f.board.get(0).floodConnections(floodedCells);
    t.checkExpect(f.board.get(0).flooded, true);
    t.checkExpect(f.board.get(0).color, Color.GREEN);
    t.checkExpect(f.board.get(1).flooded,true);
    t.checkExpect(f.board.get(1).color, Color.GREEN);
    t.checkExpect(f.board.get(2).flooded,true);
    t.checkExpect(f.board.get(3).flooded,true);
    f.onKeyEvent("r");
    t.checkExpect(f.board.get(0).color, Color.RED);
    t.checkExpect(f.board.get(1).flooded,true);
    t.checkExpect(f.board.get(1).color, Color.MAGENTA);
    t.checkExpect(f.board.get(2).flooded,false);
    t.checkExpect(f.board.get(3).flooded,false);
    f.board.get(0).floodConnections(floodedCells);
    t.checkExpect(f.board.get(0).flooded, true);
    t.checkExpect(f.board.get(0).color, Color.RED);
    t.checkExpect(f.board.get(1).flooded,true);
    t.checkExpect(f.board.get(1).color, Color.RED);
    t.checkExpect(f.board.get(2).flooded,true);
    t.checkExpect(f.board.get(3).flooded,true);
  }


  void testUpdateFloodedCells(Tester t) {
    FloodItWorld f = new FloodItWorld(10, 7, new Random());
    ArrayList<Cell> floodedCells = new ArrayList<Cell>(Arrays.asList(f.board.get(0),
            f.board.get(1)));
    t.checkExpect(f.board.get(0).color, Color.GREEN);
    t.checkExpect(f.board.get(1).flooded,false);
    t.checkExpect(f.board.get(1).color, Color.BLUE);
    t.checkExpect(f.board.get(2).flooded,false);
    t.checkExpect(f.board.get(3).flooded,false);

    f.board.get(0).floodConnections(floodedCells);

    t.checkExpect(f.board.get(0).flooded, true);
    t.checkExpect(f.board.get(0).color, Color.GREEN);
    t.checkExpect(f.board.get(1).flooded,true);
    t.checkExpect(f.board.get(1).color, Color.GREEN);
    t.checkExpect(f.board.get(2).flooded,true);
    t.checkExpect(f.board.get(3).flooded,true);

    f.updateFloodedCells();

    t.checkExpect(f.board.get(0).flooded, true);
    t.checkExpect(f.board.get(0).color, Color.GREEN);
    t.checkExpect(f.board.get(1).flooded,true);
    t.checkExpect(f.board.get(1).color, Color.GREEN);
    t.checkExpect(f.board.get(2).flooded,true);
    t.checkExpect(f.board.get(3).flooded,true);


  }

  boolean testFloodCell(Tester t) {
    Cell cell1 = new Cell(0, 0, Color.black);
    ArrayList<Cell> aList1 = new ArrayList<Cell>();
    cell1.floodCell(aList1);
    Cell cell2 = new Cell(0, 0, Color.red);
    ArrayList<Cell> aList2 = new ArrayList<Cell>();
    cell1.floodCell(aList2);

    return t.checkExpect(aList1.get(0), cell1)
            && t.checkExpect(aList2.get(0), cell2);
  }

}

