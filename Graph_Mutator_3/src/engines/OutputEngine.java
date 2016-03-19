package engines;
/**
 * 
 */

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.csvreader.CsvWriter;

/**
 * @author Anthony Jackson
 * @id 11170365
 *
 */
public class OutputEngine {

	CsvWriter csvOutput = null;
	String filepath = "C:\\Users\\jtony_000\\Google Drive\\NUIG 2015\\CT413 FYP\\mutator_stats_4\\";
	String filename = "1";
	String fileExtension = ".csv";
	String fullFilePath = "";
	boolean isClosed = false;

	public OutputEngine(String destinationDirectory, String fileNameSuffix, String[] headers) {

		if (!destinationDirectory.equals("")) {

			String withAddedSlashes = destinationDirectory.replace("/", "\\");
			withAddedSlashes = withAddedSlashes + "\\";
			filepath = withAddedSlashes;
		}


		File file = new File(filepath + filename + fileNameSuffix + fileExtension);
		while (file.exists()) {

			int v = Integer.parseInt(filename);
			++v;
			filename = String.valueOf(v);
			;
			file = new File(filepath + filename + fileNameSuffix + fileExtension);
		}
		if (!file.exists()) {
			try {

				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		csvOutput = new CsvWriter(file.getAbsolutePath());
		try {
				
			for(String header : headers){
				csvOutput.write(header);
			}
			csvOutput.endRecord();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean isClosed() {
		return isClosed;
	}

	public void shutCSVWriter() {
		try {
			csvOutput.flush();
			csvOutput.close();
			isClosed = true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void saveStat(Object[] stats) {

		try {
			for(Object stat : stats){
				
				if(stat instanceof Integer){
					csvOutput.write(Integer.toString((int)stat));
				} else if (stat instanceof Double){
					csvOutput.write(Double.toString((double)stat));
				} else {
					// must be a string
					csvOutput.write((String) stat);
				}				
			}
			csvOutput.endRecord();
			csvOutput.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}


