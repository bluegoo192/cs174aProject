import Database.DbClient;
import Database.DbQuery;
import Database.RetrievalQuery;
import Database.UpdateQuery;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

public class LogInPage {

	static JFrame frame;
	static boolean isManager;

	static JTextField username;
	static JPasswordField password;

	static String user = "";

	public static void createLogInPage(boolean manage) {

		//create stock to test stock stuff with
		/*StringBuilder create_settings = new StringBuilder("INSERT INTO Settings VALUES(1, '2017-11-29', 1, 1, 1, 1)");
		DbClient.getInstance().runQuery(new UpdateQuery(create_settings.toString()) {
			@Override
			public void onComplete(int result) {
				System.out.println("Settings created successfully");
			}
		});
		 */



		isManager = manage;

		frame = new JFrame("Log In");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension d = new Dimension(800, 800);

		frame.getContentPane().setPreferredSize(d);
		JPanel panel = new JPanel(new GridLayout(4,4,4,4));

		JLabel username_label = new JLabel("Username:");
		JLabel password_label = new JLabel("Password:");

		username = new JTextField(20);
		password = new JPasswordField(20);

		panel.add(username_label);
		panel.add(username);
		panel.add(password_label);
		panel.add(password);

		LogInPage logIn = new LogInPage();

		JButton enterButton = new JButton("Enter");
		enterButton.addActionListener(logIn.new EnterListener());
		panel.add(enterButton);

		frame.setContentPane(panel);
		frame.pack();

		frame.setVisible(true);

	}

	class EnterListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// check that username and password are good

			//check if username and password are good
			if(isManager) {
				//check manager list
				StringBuilder checkManagerList = new StringBuilder("SELECT M.ManagerID ")
						.append("FROM Manager M ")
						.append("WHERE M.ManagerID = ").append("'").append(username.getText()).append("'")
						.append(" AND M.Password = ").append("'").append(password.getText()).append("'");
				DbClient.getInstance().runQuery(new RetrievalQuery(checkManagerList.toString()) {
					@Override
					public void onComplete(ResultSet result) {
						try {
							if(!result.next()) {
								//there are no results
								JOptionPane.showMessageDialog(null, "No users match username/password set", "Error Message", 0);
								return;
							}
							LogInPage.user = LogInPage.username.getText();

							LogInPage.frame.setVisible(false);
							LogInPage.frame.dispose();

							//open manager dashboard
							ManagerDashboard.createDashboard(username.getText());


					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}else {
			//check customers list
			StringBuilder checkManagerList = new StringBuilder("SELECT C.username ")
					.append("FROM Customers C ")
					.append("WHERE C.username = ").append("'").append(username.getText()).append("'")
					.append(" AND C.password = ").append("'").append(password.getText()).append("'");
			DbClient.getInstance().runQuery(new RetrievalQuery(checkManagerList.toString()) {
				@Override
				public void onComplete(ResultSet result){
					try {
						if(!result.next()) {
							//there are no results
							JOptionPane.showMessageDialog(null, "No managers match username/password set", "Error Message", 0);
							return;
						}

						//set user thing in LogInPage to username
						LogInPage.user = LogInPage.username.getText();
						
						LogInPage.frame.setVisible(false);
						LogInPage.frame.dispose();

						//open user dashboard
						CustomerDashboard.createDashboard(username.getText());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


				}
			});
		}


		if(user.equals("")) {
			return;
		}

		//go to new page
		frame.setVisible(false);
		frame.dispose();

		if(isManager) {
			//open manager dashboard
			ManagerDashboard.createDashboard(username.getText());
		}else {
			//open user dashboard
			CustomerDashboard.createDashboard(username.getText());

		}


	}

}
}
