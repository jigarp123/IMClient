package Main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class MessageCellController extends ListCell<Message>
{
	@Override
	protected void updateItem(Message m, boolean e)
	{
		super.updateItem(m, e);
		if (m == null || e)
		{
			setText(null);
			setGraphic(null);
		}
		else
		{
			VBox v = new VBox();
			HBox layout = new HBox(5);
			Text text = new Text(m.getMessage());
			text.setFill(Color.WHITE);
			Text t = new Text(m.getName());
			if (m.getName().length() * 7 > 100)
			{
				t.setWrappingWidth(100);
			}
			else
			{
				t.prefWidth(m.getName().length() * 7);
			}
			t.setFill(Color.rgb(84, 149, 255, 1));
			if (m.getMessage().length() * 7 > 300)
			{
				text.setWrappingWidth(300);
			}
			else
			{
				text.prefWidth(m.getName().length() * 7);
			}
			//Image i = new Image(getClass().getClassLoader().getResource("Images/defaultUserIcon.png").toString(), 20, 20, true, true);
			//ImageView iv = new ImageView();
			//iv.setImage(i);
			v.setPadding(new Insets(0, 0, 0, 0));
			v.getChildren().addAll(t);
			if(m.getMine())
			{
				text.setTextAlignment(TextAlignment.RIGHT);
				layout.setAlignment(Pos.CENTER_RIGHT);
				v.setAlignment(Pos.BOTTOM_RIGHT);
				layout.getChildren().addAll(text);
			}
			else
			{
				text.setTextAlignment(TextAlignment.LEFT);
				layout.setAlignment(Pos.CENTER_LEFT);
				v.setAlignment(Pos.BOTTOM_LEFT);
				layout.getChildren().addAll(v, text);
			}
			layout.setPadding(new Insets(5, 5, 5, 5));
			setGraphic(layout);
		}
	}
}
