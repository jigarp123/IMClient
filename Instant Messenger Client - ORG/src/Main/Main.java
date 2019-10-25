package Main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Main extends Application
{
	private ListView listView;
	private TextArea inputField;
	private Button sendButton;
	private final String serverName = "jigarpatel.dynu.net";
	private final int serverPort = 1234;
	private Socket socket;
	private ArrayList<ListView> messageList;
	private BufferedReader input;
	private PrintWriter output;
	private String name = null;
	private Stage userWindow;
	private ListView users;
	private int index = 0;
	private String selected = "|";
	private boolean gotList = false;

	public static void main(String[] args)
	{
		launch(args);
	}

	private Stage ps;

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		index = 0;
		selected = "Everyone";
		setupGUI(primaryStage);
		connectToServer();
	}

	private void setupGUI(Stage primaryStage)
	{
		primaryStage.setTitle("Instant Messenger");
		primaryStage.setWidth(500);
		primaryStage.setResizable(false);
		primaryStage.setHeight(500);
		messageList = new ArrayList<>();messageList.add(new ListView());
		listView = new ListView();
		BorderPane layout = new BorderPane();
		layout.setCenter(listView);
		Scene scene = new Scene(layout);
		primaryStage.setScene(scene);
		scene.getStylesheets().add(this.getClass().getResource("/Styles/Style.css").toExternalForm());
		inputField = new TextArea();
		inputField.setWrapText(true);
		inputField.setPrefHeight(120);
		inputField.setPromptText("Enter Your Name");
		HBox bot = new HBox();
		sendButton = new Button("Send");
		inputField.setPrefWidth(400);
		sendButton.setPrefWidth(100);
		bot.getChildren().addAll(inputField, sendButton);
		layout.setBottom(bot);
		listView.setEditable(false);
		primaryStage.show();
		listView.setCellFactory(studentListView -> new MessageCellController());
		sendButton.setOnAction((event -> sendButtonClicked()));
		userWindow = new Stage();
		userWindow.setTitle("Users");
		userWindow.setWidth(200);
		userWindow.setHeight(500);
		userWindow.setResizable(false);
		ScrollPane p = new ScrollPane();
		p.setPrefWidth(200);
		p.setPrefHeight(500);
		p.setFitToHeight(true);
		p.setFitToWidth(true);
		Scene s = new Scene(p);
		s.getStylesheets().add(this.getClass().getResource("/Styles/StyleUsers.css").toExternalForm());
		userWindow.setScene(s);
		users = new ListView();
		users.getItems().add("Everyone");
		userWindow.setX(primaryStage.getX() + primaryStage.getWidth() - 5);
		userWindow.setY(primaryStage.getY());
		users.setPrefWidth(200);
		users.setPrefHeight(500);
		users.setPadding(new Insets(0, 5, 0, 5));
		p.setContent(users);
		ps = primaryStage;
		userWindow.show();
		primaryStage.setOnCloseRequest(event ->
		{
			userWindow.close();
			stop();
		});
		primaryStage.xProperty().addListener((obs, oldVal, newVal) ->
		{
			userWindow.setX(primaryStage.getX() + primaryStage.getWidth() - 5);
			userWindow.setY(primaryStage.getY());
		});
		primaryStage.yProperty().addListener((obs, oldVal, newVal) ->
		{
			userWindow.setX(primaryStage.getX() + primaryStage.getWidth() - 5);
			userWindow.setY(primaryStage.getY());
		});
		users.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				selected = newValue;
				index = getIndex(newValue);
				if (index == 0)
				{
					selected = "|";
				}
				listView.setItems(messageList.get(index).getItems());//maybe need to set to new
			}
		});
	}
	private int getIndex(String s)
	{
		for (int i = 1; i < users.getItems().size(); i++)
		{
			if (s.equals(users.getItems().get(i)))
			{
				return i;
			}
		}
		return 0;
	}
	private void sendButtonClicked()
	{
		String text = inputField.getText();
		if (name == null)
		{
			boolean valid = true;
			for (int i = 0; i < users.getItems().size(); i++)
			{
				if (users.getItems().get(i).equals(text.trim()))
				{
					valid = false;
					break;
				}
			}
			if (valid && !text.trim().equals("") && !text.equals(null) && !text.trim().contains("|"))
			{
				text = text.replace('\n', ' ');
				sendToServer("name|" + text.trim() + "|message");
				name = text.trim();
				ps.setTitle(ps.getTitle() + " - " + name);
				inputField.setPromptText("Enter Message");
			}
		}
		else
		{
			sendToServer("message|" + selected + "|" + text);
			listView.getItems().add(new Message(name, text, true));
			listView.scrollTo(listView.getItems().size() - 1);
		}
		inputField.clear();
	}

	private void connectToServer()
	{
		try
		{
			socket = new Socket(serverName, serverPort);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
			Runnable myRunnable = new Runnable()
			{
				public void run()
				{
					try
					{
						MyRunnable runnable = new MyRunnable();
						String text = "";
						while ((text = input.readLine()) != null)
						{
							//System.out.println(text);
							if (!gotList)
							{
								if (!text.equals("|messagelast"))
								{
									users.getItems().add(text.substring(7, text.length() - 8));
									messageList.add(new ListView());
								}
								else
								{
									gotList = true;
								}
							}
							else //already got list of names
							{
								runnable.setMessage(text);
								Platform.runLater(runnable);
							}
						}
					}
					catch (Exception e)
					{
					}
				}
			};
			Thread thread = new Thread(myRunnable);
			thread.start();
		}
		catch (Exception e)
		{
		}
	}

	private void sendToServer(String text)
	{
		//System.out.println(text);
		output.println(text);
		output.flush();
	}

	private class MyRunnable implements Runnable
	{
		private String message;

		public MyRunnable()
		{
			super();
			message = "";
		}

		@Override
		public void run()
		{
			System.out.println(message);
			if (message.startsWith("message"))
			{
				message = message.substring(8);
				String name = message.substring(0, message.indexOf("|"));
				message = message.substring(message.indexOf("|") + 1);
				if (message.startsWith("|"))//to everyone
				{
					message = message.substring(2);
					messageList.get(0).getItems().add(new Message(name, message, false));
					//if (selected.equals("|"))
						//listView.getItems().add(new Message(name, message, false));
				}
				else
				{
					String to = message.substring(0, message.indexOf("|"));
					message = message.substring(message.indexOf("|") + 1);
					int i = getIndex(name);
					messageList.get(i).getItems().add(new Message(name, message, false));
					//if (i == index)
						//listView.getItems().add(new Message(name, message, false));
				}
			}
			else if (message.startsWith("enter"))
			{
				messageList.add(new ListView());
				users.getItems().add(message.substring(6).substring(0, message.substring(6).indexOf("|")));
			}
			else if (message.startsWith("leave"))
			{
				message = message.substring(6);
				message = message.substring(0, message.indexOf("|"));
				int i = getIndex(message);
				if (i != 0)
				{
					users.getItems().remove(i);
					messageList.remove(i);
				}
			}
			else if (message.startsWith("appendL"))
			{
				//listView.getItems().add(new Message((message.substring(8, message.substring(8).indexOf("|") + 8)), message.substring(message.substring(8).indexOf("|") + 9), false));
			}
			message = "";
		}

		public void setMessage(String m)
		{
			if (m.startsWith("appendL"))
			{
				m = m.substring(8);
				m = m.substring(m.indexOf("|") + 1);
				if (m.startsWith("|"))
				{
					m = m.substring(2);
				}
				else
				{
					m = m.substring(m.indexOf("|") + 1);
				}
				message += "\n" + m;
			}
			else
			{
				message = m;
			}
		}
	}

	@Override
	public void stop()
	{
		if (name != null)
		{
			sendToServer("leave|||message");
		}
		Platform.exit();
		System.exit(0);
	}
}
