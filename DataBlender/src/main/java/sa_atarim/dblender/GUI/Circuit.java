package sa_atarim.dblender.GUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import sa_atarim.dblender.GUI.column_selection.ColumnsList;
import sa_atarim.dblender.GUI.column_selection.ListEntry.EntryIcon;

public class Circuit
{
	private static final int CONNECTION_SIZE = 10;
	private static final Color OFF_CIRCUIT_COLOR = new Color(0, 195, 255);
	private static final Color ON_CIRCUIT_COLOR = new Color(104, 235, 175);
	private static final Color FLOW_COLOR = new Color(211, 255, 164);
	
	private DropArea dropArea1, dropArea2;
	private ColumnsList combinedList;
	private Component component;
	private boolean flow1, flow2, flow3;
	
	/**
	 * @param component - The Swing component that contains the drawn circuit
	 * @param drop1 - The left drop area
	 * @param drop2 - The right drop area
	 * @param combinedList - The combines list at the bottom
	 */
	public Circuit(Component component, DropArea drop1, DropArea drop2, ColumnsList combinedList) {
		this.component = component;
		this.dropArea1 = drop1;
		this.dropArea2 = drop2;
		this.combinedList = combinedList;
		this.flow1 = false;
		this.flow2 = false;
		this.flow3 = false;
	}
	
	/**
	 * Refresh the circuit in case a wire should have been turning on but haven't.
	 */
	public void refresh() {
		component.validate();
		component.revalidate();
		component.repaint();
	}
	
	/**
	 * Paint the circuit's wires on the component.
	 * 
	 * @param g - The Graphics object to paint with
	 */
	public void paintCircuit(Graphics g) {
		//drop area 1 to blender
		g.setColor(getFlow1Color());
		
		g.drawLine(130, 165, 130, 145); //vertical
		g.drawLine(130, 145, 245, 145); //horizontal
		
		//drop area 1 to list 1
		g.drawLine(231, 189, 241, 189); //horizontal
		g.drawLine(241, 189, 241, 233); //vertical
		g.drawLine(241, 233, 130, 233); //horizontal
		g.drawLine(130, 233, 130, 255); //vertical
		
		if (flow1) { //flow color
			g.setColor(FLOW_COLOR);
			
			//drop area 1 to blender
			g.drawLine(129, 175, 129, 143); //vertical left
			g.drawLine(128, 175, 128, 143); //vertical left
			g.drawLine(131, 175, 131, 146); //vertical right
			g.drawLine(132, 175, 132, 146); //vertical right
			
			g.drawLine(130, 144, 245, 144); //horizontal top
			g.drawLine(130, 143, 245, 143); //horizontal top
			g.drawLine(131, 146, 245, 146); //horizontal bottom
			g.drawLine(131, 147, 245, 147); //horizontal bottom
			
			//drop area 1 to list 1
			g.drawLine(226, 188, 243, 188); //horizontal top
			g.drawLine(226, 187, 243, 187); //horizontal top
			g.drawLine(226, 190, 240, 190); //horizontal bottom
			g.drawLine(226, 191, 240, 191); //horizontal bottom
			
			g.drawLine(240, 190, 240, 232); //vertical left
			g.drawLine(239, 190, 239, 232); //vertical left
			g.drawLine(242, 187, 242, 235); //vertical right
			g.drawLine(243, 187, 243, 235); //vertical right
			
			g.drawLine(240, 232, 128, 232); //horizontal top
			g.drawLine(240, 231, 128, 231); //horizontal top
			g.drawLine(241, 234, 131, 234); //horizontal bottom
			g.drawLine(241, 235, 131, 235); //horizontal bottom
			
			g.drawLine(129, 233, 129, 260); //vertical left
			g.drawLine(128, 233, 128, 260); //vertical left
			g.drawLine(131, 234, 131, 260); //vertical right
			g.drawLine(132, 234, 132, 260); //vertical right
		}
		
		//connections
		paintCircuitConnection(g, 130, 170, flow1);
		paintCircuitConnection(g, 226, 189, flow1);
		paintCircuitConnection(g, 130, 260, flow1);
		
		//drop area 2 to blender
		g.setColor(getFlow2Color());
		
		g.drawLine(420, 165, 420, 145); //vertical
		g.drawLine(420, 145, 305, 145); //horizontal
		
		//drop area 2 to list 2
		g.drawLine(318, 189, 309, 189); //horizontal
		g.drawLine(309, 189, 309, 233); //vertical
		g.drawLine(309, 233, 419, 233); //horizontal
		g.drawLine(419, 233, 419, 255); //vertical
		
		if (flow2) { //flow color
			g.setColor(FLOW_COLOR);
			
			//drop area 2 to blender
			g.drawLine(419, 175, 419, 146); //vertical left
			g.drawLine(418, 175, 418, 146); //vertical left
			g.drawLine(421, 175, 421, 143); //vertical right
			g.drawLine(422, 175, 422, 143); //vertical right
			
			g.drawLine(422, 144, 305, 144); //horizontal top
			g.drawLine(422, 143, 305, 143); //horizontal top
			g.drawLine(419, 146, 305, 146); //horizontal bottom
			g.drawLine(419, 147, 305, 147); //horizontal bottom
			
			//drop area 2 to list 2
			g.drawLine(324, 188, 308, 188); //horizontal top
			g.drawLine(324, 187, 308, 187); //horizontal top
			g.drawLine(324, 190, 311, 190); //horizontal bottom
			g.drawLine(324, 191, 311, 191); //horizontal bottom
			
			g.drawLine(308, 189, 308, 235); //vertical left
			g.drawLine(307, 189, 307, 235); //vertical left
			g.drawLine(310, 190, 310, 232); //vertical right
			g.drawLine(311, 190, 311, 232); //vertical right
			
			g.drawLine(311, 232, 422, 232); //horizontal top
			g.drawLine(311, 231, 422, 231); //horizontal top
			g.drawLine(310, 234, 418, 234); //horizontal bottom
			g.drawLine(310, 235, 418, 235); //horizontal bottom
			
			g.drawLine(418, 234, 418, 260); //vertical left
			g.drawLine(417, 234, 417, 260); //vertical left
			g.drawLine(420, 231, 420, 260); //vertical right
			g.drawLine(421, 231, 421, 260); //vertical right
		}
		
		//connections
		paintCircuitConnection(g, 419, 170, flow2);
		paintCircuitConnection(g, 323, 189, flow2);
		paintCircuitConnection(g, 419, 260, flow2);
		
		//list 1 to combined list
		g.setColor(getFlow3Color());
		
		g.drawLine(42, 340, 25, 340); //horizontal
		g.drawLine(25, 340, 25, 555); //vertical
		g.drawLine(25, 555, 42, 555); //horizontal
		
		//list 2 to combined list
		g.drawLine(507, 340, 525, 340); //horizontal
		g.drawLine(525, 340, 525, 485); //vertical
		g.drawLine(525, 485, 507, 485); //horizontal
		
		if (flow3) { //flow color
			g.setColor(FLOW_COLOR);
			
			//list 1 to combined list
			g.drawLine(47, 339, 23, 339); //horizontal top
			g.drawLine(47, 338, 23, 338); //horizontal top
			g.drawLine(47, 341, 26, 341); //horizontal bottom
			g.drawLine(47, 342, 26, 342); //horizontal bottom
			
			g.drawLine(24, 338, 24, 557); //vertical left
			g.drawLine(23, 338, 23, 557); //vertical left
			g.drawLine(26, 341, 26, 554); //vertical right
			g.drawLine(27, 341, 27, 554); //vertical right
			
			g.drawLine(26, 554, 47, 554); //horizontal top
			g.drawLine(26, 553, 47, 543); //horizontal top
			g.drawLine(23, 556, 47, 556); //horizontal bottom
			g.drawLine(23, 557, 47, 557); //horizontal bottom
			
			//list 2 to combined list
			g.drawLine(502, 339, 527, 339); //horizontal top
			g.drawLine(502, 338, 527, 338); //horizontal top
			g.drawLine(502, 341, 524, 341); //horizontal bottom
			g.drawLine(502, 342, 524, 342); //horizontal bottom
			
			g.drawLine(524, 341, 524, 484); //vertical left
			g.drawLine(523, 341, 523, 484); //vertical left
			g.drawLine(526, 338, 526, 487); //vertical right
			g.drawLine(527, 338, 527, 487); //vertical right
			
			g.drawLine(524, 484, 502, 484); //horizontal top
			g.drawLine(524, 484, 502, 483); //horizontal top
			g.drawLine(527, 486, 502, 486); //horizontal bottom
			g.drawLine(527, 487, 502, 487); //horizontal bottom
		}
		
		//connections
		paintCircuitConnection(g, 47, 340, flow3);
		paintCircuitConnection(g, 47, 555, flow3);
		paintCircuitConnection(g, 502, 340, flow3);
		paintCircuitConnection(g, 502, 485, flow3);
	}
	
	/**
	 * Paint an I/O connection at a certain point.
	 * 
	 * @param g - The Graphics object to paint with
	 * @param contactX - The X value of the connection's center point
	 * @param contactY - The Y value of the connection's center point
	 */
	private void paintCircuitConnection(Graphics g, int contactX, int contactY, boolean on) {
		Color colorBackup = g.getColor();
		int x = contactX - CONNECTION_SIZE / 2;
		int y = contactY - CONNECTION_SIZE / 2;
		int arcWidth = 15, arcHeight = 15;
		
		//fill connection
		if (on) {
			g.setColor(FLOW_COLOR);
			g.fillRoundRect(x, y, CONNECTION_SIZE, CONNECTION_SIZE, arcWidth, arcHeight);
		}
		
		//draw connection outline
		Color outlineColor = on ? ON_CIRCUIT_COLOR : OFF_CIRCUIT_COLOR;
		
		g.setColor(outlineColor);
		g.drawRoundRect(contactX - CONNECTION_SIZE / 2, contactY - CONNECTION_SIZE / 2,
				   		CONNECTION_SIZE, CONNECTION_SIZE, 15, 15);
		
		//restore previous color
		g.setColor(colorBackup);
	}
	
	/**
	 * @return The current color of the first section of wires.
	 */
	private Color getFlow1Color() {
		Color color = getFlowColor(activateSection1());
		boolean isOn = color == ON_CIRCUIT_COLOR;
		flow1 = isOn;
		return color;
	}
	
	/**
	 * @return The current color of the second section of wires.
	 */
	private Color getFlow2Color() {
		Color color = getFlowColor(activateSection2());
		boolean isOn = color == ON_CIRCUIT_COLOR;
		flow2 = isOn;
		return color;
	}
	
	/**
	 * @return The current color of the third section of wires.
	 */
	private Color getFlow3Color() {
		Color color = getFlowColor(activateSection3());
		boolean isOn = color == ON_CIRCUIT_COLOR;
		flow3 = isOn;
		return color;
	}
	
	/**
	 * @return True if the first section should light up.
	 */
	private boolean activateSection1() {
		return dropArea1.isOccupied();
	}
	
	/**
	 * @return True if the second section should light up.
	 */
	private boolean activateSection2() {
		return dropArea2.isOccupied();
	}
	
	/**
	 * @return True if the third section should light up.
	 */
	private boolean activateSection3() {
		return combinedList.containsEntry(EntryIcon.GREEN_CANDIDATE,
										  EntryIcon.BLUE_CANDIDATE,
										  EntryIcon.GRAY_CANDIDATE);
	}
	
	/**
	 * @param flag - True to retrieve the lighten color or false to retrieve the turned off one
	 * @return A wire's color according to the flag.
	 */
	private Color getFlowColor(boolean flag) {
		return flag ? ON_CIRCUIT_COLOR : OFF_CIRCUIT_COLOR;
	}
}