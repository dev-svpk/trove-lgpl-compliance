package solver;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

final class GameTypeComboListener implements DocumentListener {
   // $FF: synthetic field
   private StackSizeFieldListener sourcePanel;
   // $FF: synthetic field
   private final StackSizeFieldListener refreshPanel;

   GameTypeComboListener(StackSizeFieldListener sourcePanel, StackSizeFieldListener refreshPanel) {
      super();
      this.sourcePanel = sourcePanel;
      this.refreshPanel = refreshPanel;
   }

   @Override
   public final void changedUpdate(DocumentEvent event) {
      this.updateRange();
   }

   @Override
   public final void removeUpdate(DocumentEvent event) {
      this.updateRange();
   }

   @Override
   public final void insertUpdate(DocumentEvent event) {
      this.updateRange();
   }

   private void updateRange() {
       GameTypeComboListener self = this;
      (new Thread(() -> {
         synchronized(this.sourcePanel.a) {
            HandRange range = ResultsReader.a.parseRange(this.sourcePanel.a.getText(), (card[])null, 1);
            this.sourcePanel.b.copyFrom(range);
            self.refreshPanel.refreshComboCount();
         }
      })).start();
   }
}
