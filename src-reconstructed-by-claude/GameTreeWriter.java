package solver;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public final class GameTreeWriter extends JPanel implements Serializable {
   private RangeDisplayTable table;
   public ArrayList selectedSuits = new ArrayList();

   public GameTreeWriter(RangeDisplayTable table) {
      this.table = table;
      char[] suitSymbols = new char[]{'\u2663', '\u2660', '\u2666', '\u2665'};
      JCheckBox suitCheckBox;
      (suitCheckBox = new JCheckBox("<HTML> <FONT SIZE = 4><FONT COLOR = red>" + suitSymbols[3] + "</FONT></FONT>")).addActionListener(new GameTreeReader(this, suitCheckBox));
      this.add(suitCheckBox);
      (suitCheckBox = new JCheckBox("<HTML> <FONT SIZE = 4><FONT COLOR = green>" + suitSymbols[0] + "</FONT></FONT>")).addActionListener(new SolverConfigWriter(this, suitCheckBox));
      this.add(suitCheckBox);
      (suitCheckBox = new JCheckBox("<HTML> <FONT SIZE = 4><FONT COLOR = black>" + suitSymbols[1] + "</FONT></FONT>")).addActionListener(new SolverConfigReader(this, suitCheckBox));
      this.add(suitCheckBox);
      JCheckBox diamondCheckBox;
      (diamondCheckBox = new JCheckBox("<HTML> <FONT SIZE = 4><FONT COLOR = blue>" + suitSymbols[2] + "</FONT></FONT>")).addActionListener(new ResultsWriter(this, diamondCheckBox));
      this.add(diamondCheckBox);
   }

   public final void applySuitFilter() {
      if (this.selectedSuits.size() == 0) {
         this.table.redrawButtons();
      } else if (this.selectedSuits.size() == 1) {
         this.table.hideAllButtons();
         this.table.relabelMonotone((String)this.selectedSuits.get(0));
      } else {
         if (this.selectedSuits.size() == 2) {
            this.table.hideAllButtons();
            this.table.relabelMixedSuits((String)this.selectedSuits.get(0), (String)this.selectedSuits.get(1));
         }

      }
   }

   // $FF: synthetic method
   static void removeSuit(GameTreeWriter writer, String suit) {
      suit = suit;
      writer = writer;

      for(int index = 0; index < writer.selectedSuits.size(); ++index) {
         if (suit.equals(writer.selectedSuits.get(index))) {
            writer.selectedSuits.remove(index);
            return;
         }
      }

   }
}
