package solver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;

final class GameTreeReader implements ActionListener {
   // $FF: synthetic field
   private GameTreeWriter writer;
   // $FF: synthetic field
   private final JCheckBox checkBox;

   GameTreeReader(GameTreeWriter writer, JCheckBox checkBox) {
      super();
      this.writer = writer;
      this.checkBox = checkBox;
   }

   public final void actionPerformed(ActionEvent event) {
      if (this.checkBox.isSelected()) {
         this.writer.selectedSuits.add("h");
         this.writer.applySuitFilter();
         System.lineSeparator();
      } else {
         GameTreeWriter.removeSuit(this.writer, "h");
         this.writer.applySuitFilter();
      }
   }
}
