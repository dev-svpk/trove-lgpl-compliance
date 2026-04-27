package solver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;

final class GameTreeReader implements ActionListener {
   // $FF: synthetic field
   private GameTreeWriter a;
   // $FF: synthetic field
   private final JCheckBox b;

   GameTreeReader(GameTreeWriter var1, JCheckBox var2) {
      super(); 
      this.a = var1;
      this.b = var2;      
   }

   public final void actionPerformed(ActionEvent var1) {
      if (this.b.isSelected()) {
         this.a.a.add("h");
         this.a.a();
         System.lineSeparator();
      } else {
         GameTreeWriter.a(this.a, "h");
         this.a.a();
      }
   }
}
