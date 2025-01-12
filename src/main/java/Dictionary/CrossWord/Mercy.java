package Dictionary.CrossWord;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;


public class Mercy {
    JFrame frame=new JFrame("Crossword Puzzle"); //creates frame
    JButton[][] grid; //names the grid of buttons

    static final String letters[] = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    String words[] = null;

    // Linked lists to hold the letters
    ArrayList  value = null;
    ArrayList  listXY = null;
    ArrayList  selectedButtons = null;

    String formedWord = "";
    int wordsFound = 0;

    private JPanel centerPanel = null;
    private JPanel bottomPanel = null;
    private JButton checkWord = null;
    private JButton clearSelection = null;
    private JButton exit=null;

    public Mercy(int width, int length, String words[])
    {
        this.words = words;
        //frame.setLayout(new GridLayout(width,length)); //set layout
        frame.setLayout(new BorderLayout());

        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(width, length));

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());

        checkWord = new JButton("Check Word");
        clearSelection = new JButton("Clear Selection");
        exit = new JButton("Exit Game");
        grid=new JButton[width][length]; //allocate the size of grid

        value = new ArrayList();
        listXY = new ArrayList();
        selectedButtons = new ArrayList();

        // Populate the grid with # to indicate empty slots
        for(int y=0; y<20; y++)
        {
            for(int x=0; x<20; x++)
            {
                final JButton button = new JButton("#");

                grid[x][y] = button;

                button.addActionListener((ActionEvent e) -> {
                    formedWord = formedWord+(button.getLabel());
                    button.setEnabled(false);
                    selectedButtons.add(button);
                });

                //frame.add(grid[x][y]); //adds button to grid
                centerPanel.add(grid[x][y]);
                frame.add(centerPanel, BorderLayout.CENTER);
                value.add("#");
                listXY.add(""+x+","+y); // put the x,y coordinate in the list
            }
        }

        checkWord.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(null, "The word: "+formedWord+" "+wordExist(formedWord));
            checkIfGameOver();
        });

        clearSelection.addActionListener((ActionEvent e) -> {
            formedWord = "";
            enableAllButtons();
            clearSelectedButtons();
            JOptionPane.showMessageDialog(null, "Selected letters cleared");
        });
        bottomPanel.add(checkWord);
        bottomPanel.add(clearSelection);
        bottomPanel.add(exit);
        frame.add(bottomPanel, BorderLayout.PAGE_END);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the default close operation
        frame.pack(); //sets appropriate size for frame
        frame.setVisible(true); //makes frame visible     
    }

    public void placeWords()
    {
        int length = words.length;

        for(int i = 0; i < length; i++)
        {
            placeWord(words[i]);
        }

        populateWithRandom();
    }

    public String wordExist(String word)
    {
        String exist = "does not exist";

        for (String word1 : words) {
            if (word1.equals(word)) {
                exist = "exists";
                formedWord = "";
                wordsFound++;
                markWordFound();
                clearSelectedButtons();
            }
        }

        return exist;
    }

    public void checkIfGameOver()
    {
        if(wordsFound == words.length)
        {
            JOptionPane.showMessageDialog(null, "GAME OVER");
        }
    }

    public void clearSelectedButtons()
    {
        selectedButtons.stream().map((_item) -> {
            //selectedButtons.remove(i);
            selectedButtons = null;
            return _item;
        }).forEachOrdered((_item) -> {
            selectedButtons = new ArrayList();
        });
    }

    public void markWordFound()
    {
        for(int i = 0; i < selectedButtons.size(); i++)
        {
            JButton button = (JButton) selectedButtons.get(i);
            button.setBackground(Color.GREEN);
        }
    }

    public void enableAllButtons()
    {
        for(int y=0; y<20; y++)
        {
            for(int x=0; x<20; x++)
            {
                grid[x][y].setEnabled(true);
            }
        }
    }

    public void placeWord(String word)
    {
        int wordLength = word.length();
        int randomX = 0;
        int randomY = 0;

        int startPointChange = 0;
        while(startPointChange < 4)
        {
            randomX = getRandomNumber(19);
            randomY = getRandomNumber(19);

            int direction = getRandomNumber(3);

            int directionsChange = 0; // Number of times tried fitting the word in different directions starting from the given start point
            while(directionsChange < 4)
            {
                if(canWordFit(wordLength, direction, randomX, randomY))
                {
                    if(!canWordOverlap(word, direction, randomX, randomY))
                    {
                        insertWord(word, direction, randomX, randomY);

                        startPointChange = 4;
                        break;
                    }

                    directionsChange++;
                }
                else
                {
                    // Change the direction and try fitting the word
                    direction = getRandomNumber(3);

                    directionsChange++;
                }
            }

            // For the ith run, and you still cannot insert a word despite changing the directions? Consider changing the start point!          
            startPointChange++;
        }
    }

    public boolean canWordFit(int wordLength, int direction, int startX, int startY)
    {
        boolean canFit = false;

        switch (direction) {
            case 1:
                canFit = (startX + wordLength) < 19;
                break;
            case 2:
                canFit = (startX - wordLength) > 0;
                break;
            case 3:
                canFit = (startY + wordLength) < 19;
                break;
            case 4:
                canFit = (startY - wordLength) > 0;
                break;
            default:
                break;
        }

        return canFit;
    }

    /*
     * This method will check if the given word will overlap with any other word in a given direction starting from the given start point
     * If the given letter in the given coordinate match an existing letter, overlook.
     */
    public boolean canWordOverlap(String word, int direction, int startX, int startY)
    {
        boolean canOverlap = false;

        switch (direction) {
            case 1:
            {
                int index = 0;
                for(int x=startX; x < (startX + word.length()); x++)
                {
                    String theValue = value.get(listXY.indexOf(""+x+","+startY)).toString();

                    if(matchValue(theValue, ""+word.charAt(index)) == false)
                    {
                        canOverlap = true;
                    }

                    System.out.println("Direction: "+direction+" - VALUE: "+theValue+" - "+word.charAt(index) + " - CanOverlap "+canOverlap);

                    index++;
                }       break;
            }
            case 2:
            {
                int index = 0;
                for(int x=startX; x > (startX - word.length()); x--)
                {
                    String theValue = value.get(listXY.indexOf(""+x+","+startY)).toString();

                    if(matchValue(theValue, ""+word.charAt(index)) == false)
                    {
                        canOverlap = true;
                    }

                    System.out.println("Direction: "+direction+" - VALUE: "+theValue+" - "+word.charAt(index) + " - CanOverlap "+canOverlap);

                    index++;
                }       break;
            }
            case 3:
            {
                int index = 0;
                for(int y=startY; y < (startY + word.length()); y++)
                {
                    String theValue = value.get(listXY.indexOf(""+startX+","+y)).toString();

                    if(matchValue(theValue, ""+word.charAt(index)) == false)
                    {
                        canOverlap = true;
                    }

                    System.out.println("Direction: "+direction+" - VALUE: "+theValue+" - "+word.charAt(index) + " - CanOverlap "+canOverlap);

                    index++;
                }       break;
            }
            case 4:
            {
                int index = 0;
                for(int y=startY; y < (startY - word.length()); y--)
                {
                    String theValue = value.get(listXY.indexOf(""+startX+","+y)).toString();

                    if(matchValue(theValue, ""+word.charAt(index)) == false)
                    {
                        canOverlap = true;
                    }

                    System.out.println("Direction: "+direction+" - VALUE: "+theValue+" - "+word.charAt(index) + " - CanOverlap "+canOverlap);

                    index++;
                }       break;
            }
            default:
                break;
        }

        return canOverlap;
    }

    public boolean matchValue(String existing, String target)
    {
        boolean matching = false;
        if(existing.equals("#") || existing.equals(target))
        {
            matching = true;
        }

        return matching;
    }

    public void insertWord(String word, int direction, int startX, int startY)
    {
        System.out.println("Direction: "+direction+" - StartX: "+startX+" - StartY: "+startY);

        switch (direction) {
            case 1:
            {
                int index = 0;
                for(int x=startX; x < (startX + word.length()); x++)
                {
                    grid[x][startY].setLabel(""+word.charAt(index)); // Rename the # place holder with the letter from the word on the UI

                    value.set(listXY.indexOf(""+x+","+startY), ""+word.charAt(index)); // Rename the # place holder with the letter from the word in the linked list

                    //grid[x][startY].setBackground(Color.red);
                    index++;
                }       break;
            }
            case 2:
            {
                int index = 0;
                for(int x=startX; x > (startX - word.length()); x--)
                {
                    grid[x][startY].setLabel(""+word.charAt(index)); // Rename the # place holder with the letter from the word on the UI

                    value.set(listXY.indexOf(""+x+","+startY), ""+word.charAt(index)); // Rename the # place holder with the letter from the word in the linked list

                    //grid[x][startY].setBackground(Color.red);
                    index++;
                }       break;
            }
            case 3:
            {
                int index = 0;
                for(int y=startY; y < (startY + word.length()); y++)
                {
                    grid[startX][y].setLabel(""+word.charAt(index)); // Rename the # place holder with the letter from the word on the UI

                    value.set(listXY.indexOf(""+startX+","+y), ""+word.charAt(index)); // Rename the # place holder with the letter from the word in the linked list

                    //grid[startX][y].setBackground(Color.red);
                    index++;
                }       break;
            }
            case 4:
            {
                int index = 0;
                for(int y=startY; y < (startY - word.length()); y--)
                {
                    grid[startX][y].setLabel(""+word.charAt(index)); // Rename the # place holder with the letter from the word on the UI

                    value.set(listXY.indexOf(""+startX+","+y), ""+word.charAt(index)); // Rename the # place holder with the letter from the word in the linked list

                    //grid[startX][y].setBackground(Color.red);
                    index++;
                }       break;
            }
            default:
                break;
        }
    }

    public void populateWithRandom()
    {
        for(int y=0; y<20; y++)
        {
            for(int x=0; x<20; x++)
            {
                if(grid[x][y].getLabel().equals("#"))
                {
                    grid[x][y].setLabel(letters[getRandomNumber(25)]);
                }
            }
        }
    }

    public int getRandomNumber(int max)
    {
        Random rand = new Random();

        return rand.nextInt(max) + 1;
    }

    public void displayGrid()
    {
        for(int y=0; y<20; y++)
        {
            for(int x=0; x<20; x++)
            {
                System.out.print(grid[x][y]);
            }
        }
    }

    public static void main(String [] args)
    {
        // Read words from the file and start the system
        String words[] = null;

        String line = "";
        File Myf = new File("C:\\CrossWord.txt");

        BufferedReader Myin;
        try
        {
            Myin = new BufferedReader(new FileReader(Myf));
            line = Myin.readLine();

            words = line.split(",");
        }
        catch (FileNotFoundException e)
        {
        }
        catch (IOException e)
        {
        }

        Mercy test = new Mercy(20,20, words);
        test.placeWords();
    }
}