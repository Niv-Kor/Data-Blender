package sa_atarim.dblender.GUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import sa_atarim.dblender.GUI.column_selection.ColumnsList;
import sa_atarim.dblender.GUI.column_selection.ListEntry.EntryIcon;

public class Circuits
{
	private static final Color OFF_CIRCUIT_COLOR = new Color(0, 195, 255);
	private static final Color ON_CIRCUIT_COLOR = new Color(104, 235, 175);
	private static final Color FLOW_COLOR = new Color(211, 255, 164);
	private static final Color CONNECTION_COLOR = new Color(140, 159, 164);
	private static final int CONNECTION_SIZE = 10;
	
	private DropArea dropArea1, dropArea2;
	private ColumnsList combinedList;
	private Component component;
	private boolean flow1, flow2, flow3;
	
	public Circuits(Component component, DropArea drop1, DropArea drop2, ColumnsList combinedList) {
		this.component = component;
		this.dropArea1 = drop1;
		this.dropArea2 = drop2;
		this.combinedList = combinedList;
		this.flow1 = false;
		this.flow2 = false;
		this.flow3 = false;
	}
	
	public void wake() {
		component.validate();
		component.revalidate();
		component.repaint();
	}
	
	public void paintCircuit(Graphics g) {
		//drop area 1 to blender
		g.setColor(getFlow1Color());
		
		g.drawLine(130, 175, 130, 145); //vertical
		g.drawLine(130, 145, 245, 145); //horizontal
		
		//drop area 1 to list 1
		g.drawLine(226, 193, 241, 193); //horizontal
		g.drawLine(241, 193, 241, 237); //vertical
		g.drawLine(241, 237, 130, 237); //horizontal
		g.drawLine(130, 237, 130, 264); //vertical
		
		if (flow1) { //flow color
			//drop area 1 to blender
			g.setColor(FLOW_COLOR);
			g.drawLine(129, 175, 129, 143); //vertical left
			g.drawLine(128, 175, 128, 143); //vertical left
			g.drawLine(131, 175, 131, 146); //vertical right
			g.drawLine(132, 175, 132, 146); //vertical right
			
			g.drawLine(130, 144, 245, 144); //horizontal top
			g.drawLine(130, 143, 245, 143); //horizontal top
			g.drawLine(131, 146, 245, 146); //horizontal bottom
			g.drawLine(131, 147, 245, 147); //horizontal bottom
			
			//drop area 1 to list 1
			g.drawLine(226, 192, 243, 192); //horizontal top
			g.drawLine(226, 191, 243, 191); //horizontal top
			g.drawLine(226, 194, 240, 194); //horizontal bottom
			g.drawLine(226, 195, 240, 195); //horizontal bottom
			
			g.drawLine(240, 194, 240, 236); //vertical left
			g.drawLine(239, 194, 239, 236); //vertical left
			g.drawLine(242, 191, 242, 239); //vertical right
			g.drawLine(243, 191, 243, 239); //vertical right
			
			g.drawLine(240, 236, 128, 236); //horizontal top
			g.drawLine(240, 235, 128, 235); //horizontal top
			g.drawLine(241, 238, 131, 238); //horizontal bottom
			g.drawLine(241, 239, 131, 239); //horizontal bottom
			
			g.drawLine(129, 237, 129, 264); //vertical left
			g.drawLine(128, 237, 128, 264); //vertical left
			g.drawLine(131, 238, 131, 264); //vertical right
			g.drawLine(132, 238, 132, 264); //vertical right
		}
		
		//connections
		paintCircuitConnection(g, 130, 175);
		paintCircuitConnection(g, 226, 193);
		paintCircuitConnection(g, 130, 264);
		
		//drop area 2 to blender
		g.setColor(getFlow2Color());
		
		g.drawLine(420, 175, 420, 145); //vertical
		g.drawLine(420, 145, 305, 145); //horizontal
		
		//drop area 2 to list 2
		g.drawLine(324, 193, 310, 193); //horizontal
		g.drawLine(310, 193, 310, 237); //vertical
		g.drawLine(310, 237, 420, 237); //horizontal
		g.drawLine(420, 237, 420, 264); //vertical
		
		if (flow2) { //flow color
			//drop area 2 to blender
			g.setColor(FLOW_COLOR);
			g.drawLine(419, 175, 419, 146); //vertical left
			g.drawLine(418, 175, 418, 146); //vertical left
			g.drawLine(421, 175, 421, 143); //vertical right
			g.drawLine(422, 175, 422, 143); //vertical right
			
			g.drawLine(422, 144, 305, 144); //horizontal top
			g.drawLine(422, 143, 305, 143); //horizontal top
			g.drawLine(419, 146, 305, 146); //horizontal bottom
			g.drawLine(419, 147, 305, 147); //horizontal bottom
			
			//drop area 2 to list 2
			g.drawLine(324, 192, 308, 192); //horizontal top
			g.drawLine(324, 191, 308, 191); //horizontal top
			g.drawLine(324, 194, 311, 194); //horizontal bottom
			g.drawLine(324, 195, 311, 195); //horizontal bottom
			
			g.drawLine(309, 193, 309, 239); //vertical left
			g.drawLine(308, 193, 308, 239); //vertical left
			g.drawLine(311, 194, 311, 236); //vertical right
			g.drawLine(312, 194, 312, 236); //vertical right
			
			g.drawLine(311, 236, 422, 236); //horizontal top
			g.drawLine(311, 235, 422, 235); //horizontal top
			g.drawLine(310, 238, 419, 238); //horizontal bottom
			g.drawLine(310, 239, 419, 239); //horizontal bottom
			
			g.drawLine(419, 238, 419, 264); //vertical left
			g.drawLine(418, 238, 418, 264); //vertical left
			g.drawLine(421, 235, 421, 264); //vertical right
			g.drawLine(422, 235, 422, 264); //vertical right
		}
		
		//connections
		paintCircuitConnection(g, 420, 175);
		paintCircuitConnection(g, 324, 193);
		paintCircuitConnection(g, 420, 264);
		
		//list 1 to combined list
		g.setColor(getFlow3Color());
		
		g.drawLine(47, 375, 25, 375); //horizontal
		g.drawLine(25, 375, 25, 590); //vertical
		g.drawLine(25, 590, 47, 590); //horizontal
		
		//list 2 to combined list
		g.drawLine(502, 375, 525, 375); //horizontal
		g.drawLine(525, 375, 525, 590); //vertical
		g.drawLine(525, 590, 502, 590); //horizontal
		
		if (flow3) { //flow color
			//drop area 2 to blender
			g.setColor(FLOW_COLOR);
			g.drawLine(47, 374, 23, 374); //horizontal top
			g.drawLine(47, 373, 23, 373); //horizontal top
			g.drawLine(47, 376, 26, 376); //horizontal bottom
			g.drawLine(47, 377, 26, 377); //horizontal bottom
			
			g.drawLine(24, 373, 24, 592); //vertical left
			g.drawLine(23, 373, 23, 592); //vertical left
			g.drawLine(26, 376, 26, 589); //vertical right
			g.drawLine(27, 376, 27, 589); //vertical right
			
			g.drawLine(26, 589, 47, 589); //horizontal top
			g.drawLine(26, 588, 47, 588); //horizontal top
			g.drawLine(23, 591, 47, 591); //horizontal bottom
			g.drawLine(23, 592, 47, 592); //horizontal bottom
			
			//list 2 to combined list
			g.drawLine(502, 374, 527, 374); //horizontal top
			g.drawLine(502, 373, 527, 373); //horizontal top
			g.drawLine(502, 376, 524, 376); //horizontal bottom
			g.drawLine(502, 377, 524, 377); //horizontal bottom
			
			g.drawLine(524, 376, 524, 589); //vertical left
			g.drawLine(523, 376, 523, 589); //vertical left
			g.drawLine(526, 373, 526, 592); //vertical right
			g.drawLine(527, 373, 527, 592); //vertical right
			
			g.drawLine(524, 589, 502, 589); //horizontal top
			g.drawLine(524, 588, 502, 588); //horizontal top
			g.drawLine(527, 591, 502, 591); //horizontal bottom
			g.drawLine(527, 592, 502, 592); //horizontal bottom
		}
		
		//connections
		paintCircuitConnection(g, 47, 375);
		paintCircuitConnection(g, 47, 590);
		paintCircuitConnection(g, 502, 375);
		paintCircuitConnection(g, 502, 590);
	}
	
	private void paintCircuitConnection(Graphics g, int contactX, int contactY) {
		Color colorBackup = g.getColor();
		
		g.setColor(CONNECTION_COLOR);
		g.fillRect(contactX - CONNECTION_SIZE / 2, contactY - CONNECTION_SIZE / 2,
				   CONNECTION_SIZE, CONNECTION_SIZE);
		
		g.setColor(colorBackup);
	}
	
	private Color getFlow1Color() {
		Color color = getFlowColor(activateFile1ToBlender());
		boolean isOn = color == ON_CIRCUIT_COLOR;
		flow1 = isOn;
		return color;
	}
	
	private Color getFlow2Color() {
		Color color = getFlowColor(activateFile2ToBlender());
		boolean isOn = color == ON_CIRCUIT_COLOR;
		flow2 = isOn;
		return color;
	}
	
	private Color getFlow3Color() {
		Color color = getFlowColor(activateCombinedToLists());
		boolean isOn = color == ON_CIRCUIT_COLOR;
		flow3 = isOn;
		return color;
	}
	
	private boolean activateFile1ToBlender() {
		return dropArea1.isOccupied();
	}
	
	private boolean activateFile2ToBlender() {
		return dropArea2.isOccupied();
	}
	
	private boolean activateCombinedToLists() {
		return combinedList.containsEntry(EntryIcon.GREEN_CANDIDATE,
										  EntryIcon.BLUE_CANDIDATE,
										  EntryIcon.GRAY_CANDIDATE);
	}
	
	private Color getFlowColor(boolean flag) {
		return flag ? ON_CIRCUIT_COLOR : OFF_CIRCUIT_COLOR;
	}
}