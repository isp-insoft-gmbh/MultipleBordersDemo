/**
 * © 2020 isp-insoft GmbH
 */
package jfx_experiments;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.util.StringConverter;

/**
 * @author okr
 * @since 08.05.2020
 *
 */
public class BorderEditListCell extends ListCell<Stroke>
{
  private final VBox box;

  private final ObjectProperty<Color>             borderColor;
  private final ObjectProperty<BorderStrokeStyle> borderStyle;
  private final ObjectProperty<Number>            borderWidth;
  private final ObjectProperty<Number>            borderInset;

  /**
   * Builds a GUI-form to edit the properties of a Stroke.
   */
  public BorderEditListCell()
  {
    final var paintLabel = new Label( "Colour:" );
    final var paintPicker = new ColorPicker();
    borderColor = paintPicker.valueProperty();
    paintLabel.setLabelFor( paintPicker );

    final var styleLabel = new Label( "Style:" );
    final BorderStrokeStyle experimentalStyle =
        new BorderStrokeStyle( StrokeType.OUTSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 1, 0, null );
    final var stylePicker = new ComboBox<>( FXCollections.observableArrayList(
        BorderStrokeStyle.SOLID,
        BorderStrokeStyle.DASHED,
        BorderStrokeStyle.DOTTED,
        BorderStrokeStyle.NONE,
        experimentalStyle ) );
    stylePicker.setStyle( "-fx-font: 12px \"Monospace\";" );
    stylePicker.setConverter( new StringConverter<BorderStrokeStyle>()
    {
      @Override
      public String toString( final BorderStrokeStyle style )
      {
        if ( BorderStrokeStyle.DASHED.equals( style ) )
        {
          return "[- - - - - - -]";
        }
        if ( BorderStrokeStyle.DOTTED.equals( style ) )
        {
          return "[. . . . . . .]";
        }
        if ( BorderStrokeStyle.NONE.equals( style ) )
        {
          return "[             ]";
        }
        if ( BorderStrokeStyle.SOLID.equals( style ) )
        {
          return "[-------------]";
        }
        if ( experimentalStyle.equals( style ) )
        {
          return "[?????????????]";
        }

        return "";
      }

      @Override
      public BorderStrokeStyle fromString( final String __ )
      {
        //will never be called
        return null;
      }
    } );
    borderStyle = stylePicker.valueProperty();
    styleLabel.setLabelFor( stylePicker );

    final var widthLabel = new Label( "Width:" );
    final var widthPicker = new Spinner<Number>( 0.0, 100.0, 1.0 );
    borderWidth = widthPicker.getValueFactory().valueProperty();
    widthLabel.setLabelFor( widthLabel );

    final var insetLabel = new Label( "Inset:" );
    final var insetPicker = new Spinner<Number>( -100.0, 100.0, 1.0 );
    borderInset = insetPicker.getValueFactory().valueProperty();
    insetLabel.setLabelFor( insetLabel );

    box = new VBox(
        16,
        makeRowBox( paintLabel,
            paintPicker ),
        makeRowBox( styleLabel, stylePicker ),
        makeRowBox( widthLabel, widthPicker ),
        makeRowBox( insetLabel, insetPicker ) );

    box.setPadding( new Insets( 16, 8, 16, 8 ) );

    setAlignment( Pos.CENTER_LEFT );
    setContentDisplay( ContentDisplay.LEFT );
    setGraphic( null );
    setText( null );
  }

  private static HBox makeRowBox( final Label label, final Region node )
  {
    final HBox hBox = new HBox( 8, label, node );
    hBox.setAlignment( Pos.CENTER );
    HBox.setHgrow( node, Priority.ALWAYS );
    label.setMinWidth( 50 );
    node.setMaxWidth( Double.POSITIVE_INFINITY );
    return hBox;
  }

  @Override
  protected void updateItem( final Stroke item, final boolean empty )
  {
    super.updateItem( item, empty );
    if ( !empty && item != null )
    {
      // set ui as graphic
      setGraphic( box );
      // unbind/bind props to sync cell with one stroke
      borderColor.unbindBidirectional( item.paint );
      borderStyle.unbindBidirectional( item.style );
      borderWidth.unbindBidirectional( item.width );
      borderInset.unbindBidirectional( item.inset );
      borderColor.bindBidirectional( item.paint );
      borderStyle.bindBidirectional( item.style );
      borderWidth.bindBidirectional( item.width );
      borderInset.bindBidirectional( item.inset );
      //FIXME(okr | 13.05.2020): there seems to be a bug here with more than 3 cells. cells start sharing state.
      //this class followed the pattern in CheckboxListCell, so it is unclear what the problem is.
      //for this demo though, it is irrelevant
    }
    else
    {
      setGraphic( null );
      setText( null );
    }
  }
}
