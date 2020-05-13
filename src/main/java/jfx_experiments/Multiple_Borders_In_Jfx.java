/**
 * © 2020 isp-insoft GmbH
 */
package jfx_experiments;

import static java.util.stream.Collectors.toList;

import java.util.Collection;

import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * @since 29.04.2020
 */
public class Multiple_Borders_In_Jfx extends Application
{
  /**
   * @param args get passed on to the JavaFx-runtime.
   */
  public static void main( final String[] args )
  {
    Application.launch( args );
  }

  @Override
  public void start( final Stage mainWindow ) throws Exception
  {
    final var state = new State();
    final var guiRoot = makeGui( state );
    final var mainScene = new Scene( guiRoot, 1200, 1000 );
    mainWindow.setScene( mainScene );
    mainWindow.getIcons().add( new Image( "https://www.iconfinder.com/icons/92498/download/png/32" ) );
    mainWindow.sizeToScene();
    mainWindow.centerOnScreen();
    mainWindow.show();
  }

  private static Parent makeGui( final State state )
  {
    final var pane = new BorderPane();
    pane.setTop( makeHeadingGui( state ) );
    pane.setCenter( makePreviewGui( state ) );
    pane.setRight( makeControlsGui( state ) );
    pane.setBottom( makeStatusGui( state ) );
    return pane;
  }

  private static Node makeStatusGui( final State state )
  {
    final var statusText = new Text();
    statusText.setFont( Font.font( "sans", FontWeight.LIGHT, 12.0 ) );
    statusText.setFontSmoothingType( FontSmoothingType.LCD );
    statusText.textProperty().bind( state.status );

    final var statusBox = new TextFlow( statusText );
    statusBox.setPrefHeight( 55 );
    statusBox.setPadding( new Insets( 16 ) );

    return statusBox;
  }

  private static Node makeControlsGui( final State state )
  {
    final var selectedShapeLabel = new Label( "Shape:" );
    final var selectedShapeBox = new ComboBox<>( FXCollections.observableArrayList( SelectedShape.values() ) );
    HBox.setHgrow( selectedShapeBox, Priority.ALWAYS );
    selectedShapeBox.setMaxWidth( Double.POSITIVE_INFINITY );
    selectedShapeBox.valueProperty().bindBidirectional( state.selected );
    selectedShapeLabel.setLabelFor( selectedShapeBox );
    selectedShapeLabel.setMinWidth( 50 );

    final var selectedShapeColorLabel = new Label( "Colour:" );
    final var selectedShapeColorPicker = new ColorPicker( state.color.getValue() );
    HBox.setHgrow( selectedShapeColorPicker, Priority.ALWAYS );
    selectedShapeColorPicker.setMaxWidth( Double.POSITIVE_INFINITY );
    selectedShapeColorPicker.valueProperty().bindBidirectional( state.color );
    selectedShapeColorLabel.setLabelFor( selectedShapeColorPicker );
    selectedShapeColorLabel.setMinWidth( 50 );


    final var borderList = new ListView<>( state.strokes );
    borderList.getSelectionModel().setSelectionMode( SelectionMode.SINGLE );
    borderList.setEditable( true );
    borderList.setPlaceholder( new Label( "No strokes!" ) );
    borderList.setCellFactory( __ -> new BorderEditListCell() );
    VBox.setVgrow( borderList, Priority.ALWAYS );

    final var addBorderBtn = new Button( "+++ Add stroke +++" );
    addBorderBtn.setMaxWidth( Double.POSITIVE_INFINITY );
    addBorderBtn.setOnAction( __ -> state.strokes.add( new Stroke() ) );

    final var deleteBorderBtn = new Button( "--- Remove stroke ---" );
    deleteBorderBtn.setMaxWidth( Double.POSITIVE_INFINITY );
    deleteBorderBtn.disableProperty().bind( borderList.getSelectionModel().selectedItemProperty().isNull() );
    deleteBorderBtn.setOnAction( __ -> state.strokes.remove( borderList.getSelectionModel().getSelectedItem() ) );

    final var controlBox = new VBox( 16,
        new HBox( 8, selectedShapeLabel, selectedShapeBox ),
        new HBox( 8, selectedShapeColorLabel,
            selectedShapeColorPicker ),
        borderList,
        addBorderBtn,
        deleteBorderBtn );
    controlBox.setAlignment( Pos.TOP_CENTER );
    controlBox.setPadding( new Insets( 0.0, 16.0, 0.0, 16.0 ) );
    controlBox.setMinWidth( 420.0 );
    return controlBox;
  }

  private static Node makePreviewGui( final State state )
  {
    final int size = 100;

    final var rectShape = new Rectangle( size, size );
    rectShape.fillProperty().bind( state.color );
    final var rectangle = bindBorder( rectShape, state.strokes );

    final var circShape = new Circle( size );
    circShape.fillProperty().bind( state.color );
    final var circle = bindBorder( circShape, state.strokes );

    final var triaShape = new Polygon( 2 * size, 0.0, size, -size, 0.0, 0.0 );
    triaShape.fillProperty().bind( state.color );
    final var triangle = bindBorder( triaShape, state.strokes );

    final var polygonShape =
        new Path( new MoveTo( 0, 0 ), new LineTo( -size / 3, 0 ), new LineTo( size / 3, size / 2 ), new LineTo( -size / 2, size ),
            new LineTo( size, size ), new LineTo( size / 2, size / 3 ), new LineTo( size / 2, -size / 2 ), new ClosePath() );
    polygonShape.fillProperty().bind( state.color );
    polygonShape.setStroke( null );
    final var polygon = bindBorder( polygonShape, state.strokes );

    final var lineShape = new Polyline( 0.0, 0.0, size / 2, 0.0, size / 3, size, size, size );
    lineShape.strokeProperty().bind( state.color );
    final var line = bindBorder( lineShape, state.strokes );

    final var svgShape = new SVGPath();
    svgShape
        .setContent(
            "M139.273,49.088c0-3.284-2.75-5.949-6.146-5.949c-0.219,0-0.434,0.012-0.646,0.031l-42.445-1.001l-14.5-37.854   "
                + "C74.805,1.824,72.443,0,69.637,0c-2.809,0-5.168,1.824-5.902,4.315L49.232,42.169L6.789,43.17c-0.213-0.021-0.43-0.031-0.646-0.031   "
                + "C2.75,43.136,0,45.802,0,49.088c0,2.1,1.121,3.938,2.812,4.997l33.807,23.9l-12.063,37.494c-0.438,0.813-0.688,1.741-0.688,2.723   "
                + "c0,3.287,2.75,5.952,6.146,5.952c1.438,0,2.766-0.484,3.812-1.29l35.814-22.737l35.812,22.737c1.049,0.806,2.371,1.29,3.812,1.29   "
                + "c3.393,0,6.143-2.665,6.143-5.952c0-0.979-0.25-1.906-0.688-2.723l-12.062-37.494l33.806-23.9   "
                + "C138.15,53.024,139.273,51.185,139.273,49.088" );
    svgShape.fillProperty().bind( state.color );
    final var path = bindBorder( svgShape, state.strokes );
    final var rectangleCard = makePreviewCard( rectangle );
    final var circleCard = makePreviewCard( circle );
    final var triangleCard = makePreviewCard( triangle );
    final var polygonCard = makePreviewCard( polygon );
    final var lineCard = makePreviewCard( line );
    final var pathCard = makePreviewCard( path );
    final var previewCards = new StackPane(
        pathCard,
        lineCard,
        triangleCard,
        polygonCard,
        circleCard,
        rectangleCard );
    state.selected.addListener( ( __, ___, selected ) -> bringSelectedToFront(
        rectangleCard,
        circleCard,
        triangleCard,
        polygonCard,
        lineCard,
        pathCard,
        selected ) );
    bringSelectedToFront( rectangleCard, circleCard, triangleCard, polygonCard, lineCard, pathCard, state.selected.getValue() );
    return previewCards;
  }

  private static Node bindBorder( final Shape shape, final ObservableList<Stroke> strokes )
  {
    final var borderRegion = makeStrokeRegion( shape );
    final var fakeShape = new Group( shape, borderRegion );
    final ChangeListener<? super Object> borderMaker = ( __, ___, ____ ) -> borderRegion.setBorder( makeBorder( strokes ) );
    strokes.addListener( (ListChangeListener<Stroke>) change ->
    {
      //FIXME(okr | 11.05.2020): very inefficient... but ... demo ...
      while ( change.next() )
      {
        if ( change.wasRemoved() )
        {
          change.getRemoved().forEach( stroke ->
          {
            stroke.inset.removeListener( borderMaker );
            stroke.paint.removeListener( borderMaker );
            stroke.style.removeListener( borderMaker );
            stroke.width.removeListener( borderMaker );
          } );

        }
        if ( change.wasAdded() )
        {
          change.getAddedSubList().forEach( stroke ->
          {
            stroke.inset.addListener( borderMaker );
            stroke.paint.addListener( borderMaker );
            stroke.style.addListener( borderMaker );
            stroke.width.addListener( borderMaker );
          } );
        }
      }

      borderRegion.setBorder( makeBorder( strokes ) );
    } );
    return fakeShape;
  }

  private static Border makeBorder( final Collection<Stroke> strokes )
  {
    return new Border( strokes.stream().map( stroke ->
    {
      final double inset = stroke.inset.getValue().doubleValue();
      final double width = stroke.width.getValue().doubleValue();
      final Color paint = stroke.paint.getValue();
      final BorderStrokeStyle style = stroke.style.getValue();
      return new BorderStroke( paint, style, CornerRadii.EMPTY, new BorderWidths( width ), new Insets( inset ) );
    } ).collect( toList() ), null );
  }

  private static Region makeStrokeRegion( final Shape shape )
  {
    final var region = new Region();
    region.setBackground( null );
    region.setShape( shape );
    region.setCenterShape( false );
    region.setScaleShape( false );
    region.setMouseTransparent( true );
    return region;
  }

  private static void bringSelectedToFront( final Node rectangle,
                                            final Node circle,
                                            final Node triangle,
                                            final Node polygon,
                                            final Node line,
                                            final Node path,
                                            final SelectedShape selected )
  {
    if ( selected == null )
    {
      return;
    }
    switch ( selected )
    {
      case CIRC:
        circle.toFront();
        break;
      case LINE:
        line.toFront();
        break;
      case PATH:
        path.toFront();
        break;
      case RECT:
        rectangle.toFront();
        break;
      case TRIA:
        triangle.toFront();
        break;
      case POLY:
        polygon.toFront();
        break;
    }
  }

  private static Region makePreviewCard( final Node innerShape )
  {
    final var card = new StackPane( innerShape );
    card.setBackground( new Background( new BackgroundFill( Color.gray( 0.9 ), CornerRadii.EMPTY, Insets.EMPTY ) ) );
    card.setBorder( new Border(
        new BorderStroke( Color.gray( 0.33 ),
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            BorderWidths.DEFAULT ) ) );
    card.setPrefSize( 400, 400 );
    return card;
  }

  private static Node makeHeadingGui( final State state )
  {
    final var heading = new Text( "JavaFx experiment with multiple borders" );
    heading.setFont( Font.font( "monospace", FontWeight.BOLD, 28.0 ) );
    heading.setFontSmoothingType( FontSmoothingType.LCD );
    final var subtitle = new Text( "How can we apply multiple borders to a Shape?" );
    subtitle.setFont( Font.font( "serif", 18.0 ) );
    subtitle.setFontSmoothingType( FontSmoothingType.LCD );
    final var previewInfo = makePreviewInfo( state.selected );
    final var centerBox = new VBox( 16, heading, subtitle, previewInfo );
    centerBox.setAlignment( Pos.TOP_CENTER );
    centerBox.setPadding( new Insets( 16 ) );
    return centerBox;
  }

  private static Node makePreviewInfo( final Property<SelectedShape> selected )
  {
    final var info = new Text( "No shape selected" );
    info.setFontSmoothingType( FontSmoothingType.LCD );
    info.setFont( Font.font( "sans", 12.0 ) );
    final SelectedShape selectedShape = selected.getValue();
    if ( selectedShape == null )
    {
      return info;
    }
    selected.addListener( ( __, ___, newShape ) -> info.setText( toInfoText( newShape ) ) );
    info.setText( toInfoText( selectedShape ) );
    return info;
  }

  private static String toInfoText( final SelectedShape selectedShape )
  {
    switch ( selectedShape )
    {
      case CIRC:
        return "Circle shape is selected";
      case LINE:
        return "Line shape is selected";
      case PATH:
        return "SVG shape is selected";
      case RECT:
        return "Rectangle shape is selected";
      case TRIA:
        return "Triangle shape is selected";
      case POLY:
        return "Polygon shape is selected";
    }
    return "";
  }

  private enum SelectedShape
  {
    RECT,
    CIRC,
    TRIA,
    POLY,
    LINE,
    PATH,
    ;
  }

  private static class State
  {
    final Property<Color>         color    = new SimpleObjectProperty<>( Color.HOTPINK );
    final Property<SelectedShape> selected = new SimpleObjectProperty<>( SelectedShape.RECT );
    final Property<String>        status   = new SimpleStringProperty( "" );
    final ObservableList<Stroke>  strokes  = FXCollections.observableArrayList();
  }
}
