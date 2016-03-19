/**
 * 
 */
package graphenvironment;

import java.util.Arrays;

import javax.swing.JLabel;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * @author Anthony Jackson
 * @id 11170365
 *
 */
public class ControlBar {
	
	GridPane grid;

	final TextField directoryPath = new TextField();
	final TextField numModelsRequired = new TextField();
	final TextField numPlayersPerModel = new TextField();
	final TextField available_1 = new TextField();
	
	final TextField p_edge = new TextField();
	
	Button startButton = new Button("Start");
	Button pauseButton = new Button("Pause");
	Button resetButton = new Button("Reset");
	
	Label nModelLabel = new Label("Model No.");
	TextField modelNum = new TextField();
	
	Label nPlayersLabel = new Label("#Players");
	TextField numPlayers = new TextField();
	TextField reqPlayers = new TextField();
	
	Label nCoopsLabel = new Label("%Coops");
	TextField numCoops = new TextField();
	
	Label nDefsLabel = new Label("%Defs");
	TextField numDefs = new TextField();
	
	ProgressBar modelProgress = new ProgressBar(0.0);
	
	// stacked bar chart
	final static String playerTypeSplit = "% Player Split";
	final static String edgeTypeSplit = "% Edge Split";
	final NumberAxis xAxis = new NumberAxis(0,100,25);
	final CategoryAxis yAxis = new CategoryAxis();	
	final StackedBarChart<Number,String> sbc = new StackedBarChart<Number,String>(xAxis, yAxis);
    final XYChart.Series<Number,String> coops = new XYChart.Series<Number,String>();
    final XYChart.Series<Number,String> defs = new XYChart.Series<Number,String>();
    final XYChart.Series<Number,String> ccs = new XYChart.Series<Number,String>();
    final XYChart.Series<Number,String> cds = new XYChart.Series<Number,String>();
    final XYChart.Series<Number,String> dds = new XYChart.Series<Number,String>();
    
	Label nMutations = new Label("Mutations Count");
	TextField numMutations = new TextField();
    
	
	
	public ControlBar(){
		
		  grid = new GridPane();
		  buildControlPanel();
	}
	
	


	public void buildControlPanel(){
		
	    grid.setPadding(new Insets(10, 10, 10, 10));
	    grid.setVgap(5);
	    grid.setHgap(5);
	    //grid.setGridLinesVisible(true);
	    ColumnConstraints column1 = new ColumnConstraints();
	    column1.setPercentWidth(25);
	    ColumnConstraints column2 = new ColumnConstraints();
	    column2.setPercentWidth(25);
	    ColumnConstraints column3 = new ColumnConstraints();
	    column3.setPercentWidth(25);
	    ColumnConstraints column4 = new ColumnConstraints();
	    column4.setPercentWidth(25);
	    grid.getColumnConstraints().addAll(column1, column2, column3, column4);
	    grid.setMaxWidth(450);
	    //Defining the Name text field
	    
	    directoryPath.setPromptText("output to C:/mydir");
	    directoryPath.getText();
	    GridPane.setConstraints(directoryPath, 0, 0, 4, 1);
	    //directoryPath.setPrefWidth(100);
	    grid.getChildren().add(directoryPath);
	    
	    numModelsRequired.setPromptText("#Models to build");
	    numModelsRequired.setPrefColumnCount(3);
	    numModelsRequired.getText();
	    GridPane.setConstraints(numModelsRequired, 0, 1, 4, 1);
	    numModelsRequired.setPrefWidth(60);
	    grid.getChildren().add(numModelsRequired);
	    
	    numPlayersPerModel.setPromptText("#Players Per Model");
	    numPlayersPerModel.setPrefColumnCount(3);
	    numPlayersPerModel.getText();
	    GridPane.setConstraints(numPlayersPerModel, 0, 2, 4, 1);
	    numPlayersPerModel.setPrefWidth(60);
	    grid.getChildren().add(numPlayersPerModel);
	    
	    available_1.setPromptText("available_1");
	    available_1.setPrefColumnCount(3);
	    available_1.getText();
	    GridPane.setConstraints(available_1, 0, 3, 4, 1);
	    available_1.setPrefWidth(60);
	    //grid.getChildren().add(available_1);
	    
	   
	    
	    p_edge.setPromptText("p(edge)");
	    p_edge.setPrefColumnCount(3);
	    p_edge.setPrefWidth(60);
	    GridPane.setConstraints(p_edge, 0, 4, 4, 1);
	    grid.getChildren().add(p_edge);
	    
	    //Defining the control buttons
	    
	    GridPane.setConstraints(startButton, 0, 5, 2, 1);
	    startButton.setMaxWidth(Double.MAX_VALUE);
	    grid.getChildren().add(startButton);
	    
	    GridPane.setConstraints(pauseButton, 2, 5, 1, 1);
	    pauseButton.setMaxWidth(Double.MAX_VALUE);
	    grid.getChildren().add(pauseButton);
	    
	    GridPane.setConstraints(resetButton, 3, 5, 1, 1);	
	    resetButton.setMaxWidth(Double.MAX_VALUE);
	    grid.getChildren().add(resetButton);
	    
	    
	    
	    GridPane.setConstraints(nModelLabel, 0, 8, 2, 1);
	    nModelLabel.setMaxWidth(Double.MAX_VALUE);
	    grid.getChildren().add(nModelLabel);
	    
	    GridPane.setConstraints(modelNum, 2, 8, 2, 1);
	    modelNum.setMaxWidth(Double.MAX_VALUE);
	    grid.getChildren().add(modelNum);
	    
	    
	    GridPane.setConstraints(nPlayersLabel, 0, 9, 2, 1);
	    nPlayersLabel.setMaxWidth(Double.MAX_VALUE);
	    grid.getChildren().add(nPlayersLabel);
	    
	    GridPane.setConstraints(numPlayers, 2, 9, 1, 1);
	    numPlayers.setMaxWidth(Double.MAX_VALUE);
	    grid.getChildren().add(numPlayers);
	    
	    GridPane.setConstraints(reqPlayers, 3, 9, 1, 1);
	    reqPlayers.setMaxWidth(Double.MAX_VALUE);
	    grid.getChildren().add(reqPlayers);
	    
	    
	    
		GridPane.setConstraints(modelProgress, 0, 10, 4, 1);
		modelProgress.setMaxWidth(Double.MAX_VALUE);
//		modelProgress.getStyleClass().add("track");
		modelProgress.getStyleClass().add("bar");
		grid.getChildren().add(modelProgress);
		for (Node n: modelProgress.lookupAll(".progress-bar.bar")) n.setStyle("-fx-accent: lightgreen;");
		
		
	    GridPane.setConstraints(nCoopsLabel, 0, 11, 2, 1);
		nCoopsLabel.setMaxWidth(Double.MAX_VALUE);
		grid.getChildren().add(nCoopsLabel);
		
		GridPane.setConstraints(nDefsLabel, 2, 11, 2, 1);
		nDefsLabel.setMaxWidth(Double.MAX_VALUE);
		grid.getChildren().add(nDefsLabel);
		
		GridPane.setConstraints(numCoops, 0, 12, 2, 1);
		numCoops.setMaxWidth(Double.MAX_VALUE);
		grid.getChildren().add(numCoops);
		
		GridPane.setConstraints(numDefs, 2, 12, 2, 1);
		numDefs.setMaxWidth(Double.MAX_VALUE);
		grid.getChildren().add(numDefs);
	    

		
		GridPane.setConstraints(sbc, 0, 13, 4, 1);
		sbc.setMaxWidth(Double.MAX_VALUE);		
		grid.getChildren().add(sbc);
		yAxis.setCategories(FXCollections.<String>observableArrayList(
                Arrays.asList( edgeTypeSplit, playerTypeSplit)));
		sbc.setCategoryGap(60);		
		
		setRatios(0.0, 0.0, 0.0, 0.0, 0.0);		
//		coops.setName("Cooperators");
//		defs.setName("Defectors");
//		ccs.setName("C-C edges");
//		cds.setName("C-D egdes");
//		dds.setName("D-D edges");
		sbc.getData().addAll(coops, defs, ccs, cds, dds);
		sbc.setLegendVisible(false);
		for (Node n: sbc.lookupAll(".default-color0.chart-bar")) n.setStyle("-fx-bar-fill: blue;");
		for (Node n: sbc.lookupAll(".default-color1.chart-bar")) n.setStyle("-fx-bar-fill: red;");
		for (Node n: sbc.lookupAll(".default-color2.chart-bar")) n.setStyle("-fx-bar-fill: blue;");
		for (Node n: sbc.lookupAll(".default-color3.chart-bar")) n.setStyle("-fx-bar-fill: green;");
		for (Node n: sbc.lookupAll(".default-color4.chart-bar")) n.setStyle("-fx-bar-fill: red;");
		
		
		GridPane.setConstraints(nMutations, 0, 14, 2, 1);
		nMutations.setMaxWidth(Double.MAX_VALUE);
		grid.getChildren().add(nMutations);
		
		GridPane.setConstraints(numMutations, 2, 14, 2, 1);
		numMutations.setMaxWidth(Double.MAX_VALUE);
		grid.getChildren().add(numMutations);
	}
	
	public void setRatios(double coop, double def, double ccEdges, double ddEdges, double cdEdges){
		coops.getData().add(new XYChart.Data<Number,String>(coop, playerTypeSplit));
		defs.getData().add(new XYChart.Data<Number,String>(def, playerTypeSplit));
		
		ccs.getData().add(new XYChart.Data<Number,String>(ccEdges, edgeTypeSplit));
		cds.getData().add(new XYChart.Data<Number,String>(cdEdges, edgeTypeSplit));
		dds.getData().add(new XYChart.Data<Number,String>(ddEdges, edgeTypeSplit));
		
	}
	
	public void updateRatios(double coop, double def, double ccEdges, double ddEdges, double cdEdges){
				
		coops.getData().get(0).setXValue(coop);
		defs.getData().get(0).setXValue(def);		
		ccs.getData().get(0).setXValue(ccEdges);
		cds.getData().get(0).setXValue(cdEdges);
		dds.getData().get(0).setXValue(ddEdges);
		
	}
	
	
	
	public Button getStartButton(){
		return this.startButton;
	}
	
	public Button getPauseButton(){
		return this.pauseButton;
	}
	
	public Button getResetButton(){
		return this.resetButton;
	}
	
	public void setModelNum(int modelNo){
		modelNum.setText(String.valueOf(modelNo));
	}
	
	public void setNumPlayers(int numPlayas, int numPlayasRequied){
		numPlayers.setText(String.valueOf(numPlayas));
		reqPlayers.setText(String.valueOf(numPlayasRequied));
		
	}
	
	public void setNumCoops(int numC){
		numCoops.setText(String.valueOf(numC));
	}
	
	public void setNumDefs(int numD){
		numDefs.setText(String.valueOf(numD));
	}
	
	public void setNumMutations(int nMutations){
		numMutations.setText(String.valueOf(nMutations));
	}
	
	
	
	/**
	 * @param cDsplit the cDsplit to set
	 */
	public void setModelProgress(double percentCooperators) {
		modelProgress.setProgress(percentCooperators);
	}

	/**
	 * @return the directoryPath
	 */
	public String getDirectoryPath() {
		return directoryPath.getText();
	}


	public GridPane getControlPanel(){
		return this.grid;
	}
	
	/**
	 * @return
	 */
	public int getNumModelsRequired() {
		if(!numModelsRequired.getText().equalsIgnoreCase("")){
			return Integer.parseInt(numModelsRequired.getText());
		}
		return 20;
	}
	
	public void setNumModelsRequired(int num){
		numModelsRequired.setText(String.valueOf(num));
	}
	

	/**
	 * @return
	 */
	public int getNumPlayersPerModel() {
		if(!numPlayersPerModel.getText().equalsIgnoreCase("")){
			return Integer.parseInt(numPlayersPerModel.getText());
		}
		return 45;
	}
	
	public void setNumPlayersPerModel(int numPlayersThisModel){
		numPlayersPerModel.setText(String.valueOf(numPlayersThisModel));
	}
	
	/**
	 * @return the p_edge
	 */
	public double getP_edge() {
		if(!p_edge.getText().equalsIgnoreCase("")){
			return Double.parseDouble(p_edge.getText());
		}
		
		return 0.1;
	}
	
	public void setP_edge(double p){
		p_edge.setText(String.valueOf(p));
	}



}
