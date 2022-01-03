package load;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Read {
    public static void skipToNextLine(String fileName) {

    }

    public static void readNextWord(String fileName) {
        File file = new File("res/teamfiles/" + fileName + ".txt");
        Scanner myReader = null;

        try {
            myReader = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND");
            e.printStackTrace();
        }

        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();

            System.out.println(data);
        }

    }
}
