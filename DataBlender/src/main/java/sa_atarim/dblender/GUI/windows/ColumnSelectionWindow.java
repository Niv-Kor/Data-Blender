package sa_atarim.dblender.GUI.windows;
import java.awt.Color;
import java.awt.Dimension;
import javaNK.util.GUI.swing.containers.Window;
import sa_atarim.dblender.Constants;

public class ColumnSelectionWindow extends Window
{
	private static final long serialVersionUID = -7382314825689526910L;
	private static final Dimension DIM = new Dimension(450, 500);
	
	public ColumnSelectionWindow() {
		super(Constants.PROGRAM_TITLE);
	}

	@Override
	public Dimension getDimension() { return DIM; }

	@Override
	public Color getColor() { return Constants.WINDOW_COLOR; }

	@Override
	protected boolean DisplayRightAway() { return true; }
}