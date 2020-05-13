/**
 * @author okr
 */
module scratch
{
  requires java.base;
  requires javafx.base;
  requires transitive javafx.graphics;
  requires org.controlsfx.controls;

  // helps debugging
  requires javafx.fxml;
  requires transitive javafx.web;
  requires transitive javafx.swing;
  requires org.scenicview.scenicview;

  exports jfx_experiments;
}