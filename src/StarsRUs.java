import javax.swing.*;

/**
 * Created by Arthur on 11/12/17.
 */
public class StarsRUs {

    public static void main(String[] args) {
        JFrame frame = new JFrame("FrameDemo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        //4. Size the frame.
        frame.pack();
        DbClient.getInstance();
        //5. Show it.
        frame.setVisible(true);
    }
}
