import model.BitBoardFactory;
import model.Board;
import model.constants.Spot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static model.constants.Spot.*;

/**
 * Created by Shahab Shekari on 12/23/14.
 */
public class Chess {

    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static String command;

    public static void main(String[] args) {
        BitBoardFactory.prepareDatabase();
        Board board = new Board();

        System.out.println(board);
        System.out.print("> ");

        try {
            while (!(command = br.readLine()).equals("exit"))
            {
                String origin = command.split(" ")[0].substring(0, 1).toUpperCase() + command.split(" ")[0].substring(1),
                        target = command.split(" ")[1].substring(0, 1).toUpperCase() + command.split(" ")[1].substring(1);
                boolean moveStatus = board.move(Spot.valueOf(origin), Spot.valueOf(target));
                if (moveStatus)
                    System.out.println(board);
                else
                    System.out.println("Invalid Move.");
                System.out.print("> ");
            }
        } catch(IOException e)
        {
            e.printStackTrace();
        }

    }

}
