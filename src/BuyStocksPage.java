import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

public class BuyStocksPage {

	static JFrame frame;
	
	static String user;
	
	static JTextField amount;
	
	
	public static void createStocksPage(String username) {
		user = username;
		
		frame = new JFrame("Buy/Sell Stocks");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension d = new Dimension(800, 800);
      
        frame.getContentPane().setPreferredSize(d);
        JPanel panel = new JPanel(new GridLayout(4,4,4,4));
       
		
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
}
