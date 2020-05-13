/**
 * © 2020 isp-insoft GmbH
 */
package jfx_experiments;

import java.util.Objects;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.paint.Color;

class Stroke
{
  final Property<Color>             paint = new SimpleObjectProperty<>( Color.SLATEBLUE );
  final Property<BorderStrokeStyle> style = new SimpleObjectProperty<>( BorderStrokeStyle.SOLID );
  final Property<Number>            width = new SimpleDoubleProperty( 5.0 );
  final Property<Number>            inset = new SimpleDoubleProperty( 0.0 );

  @Override
  public int hashCode()
  {
    return Objects.hash( inset.getValue(), paint.getValue(), style.getValue(), width.getValue() );
  }

  @Override
  public boolean equals( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof Stroke) )
    {
      return false;
    }
    final Stroke other = (Stroke) obj;
    return Objects.equals( inset.getValue(), other.inset.getValue() ) && Objects.equals( paint.getValue(), other.paint.getValue() )
        && Objects.equals( style.getValue(), other.style.getValue() )
        && Objects.equals( width.getValue(), other.width.getValue() );
  }


}