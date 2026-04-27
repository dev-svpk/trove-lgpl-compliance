package solver;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

final class GameTypeComboListener implements DocumentListener {
   // $FF: synthetic field
   private StackSizeFieldListener a;
   // $FF: synthetic field
   private final StackSizeFieldListener b;

   GameTypeComboListener(StackSizeFieldListener var1, StackSizeFieldListener var2) {
      super(); 
      this.a = var1;
      this.b = var2;      
   }

   @Override
   public final void changedUpdate(DocumentEvent var1) {
      this.a();
   }

   @Override
   public final void removeUpdate(DocumentEvent var1) {
      this.a();
   }

   @Override
   public final void insertUpdate(DocumentEvent var1) {
      this.a();
   }

   private void a() {
       GameTypeComboListener be2 = this;
      (new Thread(() -> {
         synchronized(this.a.a) {
            HandRange var3 = ResultsReader.a.c(this.a.a.getText(), (card[])null, 1);
            this.a.b.a(var3);
            be2.b.a();
         }
      })).start();
   }
}
