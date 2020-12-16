package levelGenerators.BosikDuffGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.*;

import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioTimer;

public class LevelGenerator implements MarioLevelGenerator{
    private static int chunkSampleWidth = 25;
    private String directory = "levels/original/";
    private final int SAMPLE_SIZE = 10;
    private final int MEDIUM_SCORE = 50; //30
    private final int HARD_SCORE = 80; //70
    private String lastChunkDifficulty = "";
    Random rand = new Random();
    private HashMap<String, ArrayList<String>> chunks = new HashMap<String, ArrayList<String>>();
    private HashMap<String, ArrayList<String>> markovTable = new HashMap<String, ArrayList<String>>();
    private ArrayList<String> connectors = new ArrayList<>();

    public LevelGenerator() {
        this("levels/original/");
    }

    public LevelGenerator(String directory) {
        this(directory, chunkSampleWidth);
    }

    public LevelGenerator(String directory, int width){
        chunkSampleWidth = width;
        this.directory = directory;
        chunks.put("easy", new ArrayList<String>());
        chunks.put("medium", new ArrayList<String>());
        chunks.put("hard", new ArrayList<String>());
        chunks.put("start", new ArrayList<String>());
        chunks.put("finish", new ArrayList<String>());

        markovTable.put("easy", new ArrayList<String>());
        markovTable.put("medium", new ArrayList<String>());
        markovTable.put("hard", new ArrayList<String>());
        markovTable.put("start", new ArrayList<String>());
        markovTable.put("finish", new ArrayList<String>());

        connectors.add(
                "---------\n" +
                "---------\n" +
                "---------\n" +
                "--C------\n" +
                "---------\n" +
                "----@----\n" +
                "-------S-\n" +
                "------S--\n" +
                "-----S---\n" +
                "-@---@---\n" +
                "---S-----\n" +
                "---------\n" +
                "--#SS###-\n" +
                "-##SS----\n" +
                "XXXXX----\n" +
                "XXXXX----");

        connectors.add(
                "----\n" +
                "----\n" +
                "----\n" +
                "----\n" +
                "----\n" +
                "----\n" +
                "----\n" +
                "----\n" +
                "---@\n" +
                "-@--\n" +
                "---X\n" +
                "-X--\n" +
                "----\n" +
                "---X\n" +
                "-XXX\n" +
                "-XXX");
        connectors.add(
                "-----\n" +
                "-----\n" +
                "-----\n" +
                "-----\n" +
                "-----\n" +
                "-----\n" +
                "---X-\n" +
                "-----\n" +
                "X----\n" +
                "-----\n" +
                "X--TT\n" +
                "---TT\n" +
                "--#TT\n" +
                "-##TT\n" +
                "XXXXX\n" +
                "XXXXX");
    }

    private void getChunks() throws IOException{
        String chunk = "";
        File[] listOfFiles = new File(directory).listFiles();   

        for (int i = 0; i < SAMPLE_SIZE; i++){
            List<String> lines = Files.readAllLines(listOfFiles[rand.nextInt(listOfFiles.length)].toPath());
           for (int k = 0; k < lines.get(0).length(); k+=chunkSampleWidth){
                for (int j = 0; j < lines.size(); j++){
                    if(k+chunkSampleWidth > lines.get(0).length())
                        chunk += lines.get(j).substring(k) + "\n";
                    else
                        chunk += lines.get(j).substring(k, k+chunkSampleWidth) + "\n"; 
                }
                determineDifficulty(chunk);
                chunk = "";
           }
        }

        printMap(markovTable);
    }

    private void printMap(HashMap<String, ArrayList<String>> nchunks){
        System.out.println("EASY");
        for(String s: nchunks.get("easy")){
            System.out.println(s);
        }
        System.out.println("MEDIUM");
        for(String s: nchunks.get("medium")){
            System.out.println(s);
        }
        System.out.println("HARD");
        for(String s: nchunks.get("hard")){
            System.out.println(s);
        }
        System.out.println("START");
        for(String s: nchunks.get("start")){
            System.out.println(s);
        }
        System.out.println("FINISH");
        for(String s: nchunks.get("finish")){
            System.out.println(s);
        }
    }


    private void determineDifficulty(String chunk){
        int score = 0;

        for (int i = 0; i < chunk.length(); i++){
            switch(chunk.charAt(i)) {
                case 'M': //Mario
                    storeChunk(chunk, "start");
                    return;
                case 'F': //Exit
                    storeChunk(chunk, "finish");
                    return;
                case 'y': //Spike
                case 'g': //Goomba
                case 'k': //Green Koopa
                case 'r': //Red Koopa
                    score += 2;
                    break;
                case 'Y': //Spike wing
                case 'G': //Goomba wing
                case 'K': //Green Koopa wing
                case 'R': //Red Koopa wing
                case '*': //Bullet Bill
                case 'B': //Bullet Bill Head
                case 'b': //Bullet Bill Body
                    score += 3;
                    break;
                case 'X': //Floor
                case '#': //Pyramid
                case '?':
                case '@': //Mushroom question block
                case 'Q':
                case '!': //Coin question block
                case '1': //Invisible 1 up block
                case '2': //Invisible coin block
                case 'S': //Normal Block
                case 'C': //Coin block
                case 'U': //Mushroom block
                case 'L': //1up block
                case 't': //Empty Pipe
                case 'T': //Flower Pipe
                case '<': //Pipe top left
                case '>': //Pipe top right
                case '[': //Pipe body left
                case ']': //Pipe body right
                case '%': //Foreground jump through
                case '|': //Background jump through
                    score += 1;
                    break;
                case 'D': //Used
                    break;
                case 'o': //Coin
                case '-': //Empty
                    break;
            }
        }
        System.out.println(score);
        if (score < MEDIUM_SCORE){
            storeChunk(chunk, "easy");
        } else if (score < HARD_SCORE){
            storeChunk(chunk, "medium");
        } else {
            storeChunk(chunk, "hard");
        }
    }
    
    private void storeChunk(String chunk, String difficulty){
        //stores chunk in appropriate structure based on difficulty
        ArrayList<String> curChunks = chunks.get(difficulty);
        curChunks.add(chunk);
        chunks.put(difficulty, curChunks);

        //updates markov table maps if the lastchunk was set
        if(!lastChunkDifficulty.equals("")){
            ArrayList<String> curDiffs = markovTable.get(lastChunkDifficulty);
            curDiffs.add(difficulty);
            markovTable.put(lastChunkDifficulty, curDiffs);
            
        }
        
        //Set last difficulty to current
        lastChunkDifficulty = difficulty;     
    }


    private String getChunk(String diff){
        //gets the next chunk based on the input difficulty
        ArrayList<String> diffChunks = chunks.get(diff);
        return diffChunks.get(rand.nextInt(diffChunks.size()));
    }

    
    private String getNextDifficulty(String difficulty){
        //Gets the next difficulty based on the input
        ArrayList<String> availableDifficulties = markovTable.get(difficulty);
        
        String nextDiff = availableDifficulties.get(rand.nextInt(availableDifficulties.size()));
        System.out.println(nextDiff);
        return nextDiff; 
    }


    private ArrayList<String> getLevelChunks(){
        ArrayList<String> level = new ArrayList<String>();

        //Add starting chunk
        ArrayList<String> startChunks = chunks.get("start");
        String sc = startChunks.get(rand.nextInt(startChunks.size()));
        level.add(sc);

        String diff = getNextDifficulty("start");
        level.add(transChunk());

        while(!diff.equals("finish")){
            level.add(getChunk(diff));
            //Add transitional Chunk
            level.add(transChunk());
            diff = getNextDifficulty(diff);
        }
        level.add(getChunk("finish"));
        return level;
    }

    private String transChunk(){
        //gets a random transition chunk
        return  connectors.get(rand.nextInt(connectors.size()));
    }
    
    private String appendLevelChunks(ArrayList<String> levelChunks){
        String wholeLevel = "";
        int levelHeight = levelChunks.get(0).split("\n").length;
        System.out.println(levelHeight);
        for(int i=0; i<levelHeight; i++) {
            String levelLine = "";
            for (String s : levelChunks) {
                String line = s.split("\n")[i];
                levelLine += line;
            }
            wholeLevel += levelLine + "\n";
        }
        return wholeLevel;
    }

    @Override
    public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
        
        model.clearMap();

        try{ 
            getChunks();
        } catch (IOException e){
            e.printStackTrace();
        }

        String lvl = appendLevelChunks(getLevelChunks());
        System.out.println(lvl);
        //Take start chunk randomly
        //Find following chunks based off of markov table

        return lvl;
    }

    @Override
    public String getGeneratorName() {
        return "BosikDuffLevelGenerator";
    }
    
}