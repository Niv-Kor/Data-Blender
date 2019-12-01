package sa_atarim.dblender.output;
import java.io.IOException;
import sa_atarim.dblender.sheets.Blender;

public class FileProcessor
{
	private Blender blender;
	
	public FileProcessor() {
		this.blender = new Blender();
	}
	
	public void process(OutputRequest request, FileSpecifications ... files) throws IOException {
		for (FileSpecifications file : files) request.addFile(file);
		blender.blend(request);
	}
}