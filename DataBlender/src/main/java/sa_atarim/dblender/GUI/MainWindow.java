package sa_atarim.dblender.GUI;
import java.awt.Color;
import java.awt.Dimension;
import javaNK.util.GUI.swing.containers.Window;

public class MainWindow extends Window
{
	private static final String TITLE = "S.A Atarim - Data Blender";
	private static final Dimension DIM = new Dimension(400, 300);
	private static final Color COLOR = null;
	
	public MainWindow() {
		super(TITLE);
	}

	@Override
	public Dimension getDimension() { return DIM; }

	@Override
	public Color getColor() { return COLOR; }

	@Override
	protected boolean DisplayRightAway() { return true; }
}