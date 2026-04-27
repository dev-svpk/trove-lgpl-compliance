package solver;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public final class GameTreeWriter extends JPanel implements Serializable {
   private RangeDisplayTable b;
   public ArrayList a = new ArrayList();

   public GameTreeWriter(RangeDisplayTable var1) {
      this.b = var1;
      char[] var3 = new char[]{'\u2663', '\u2660', '\u2666', '\u2665'};
      JCheckBox var2;
      (var2 = new JCheckBox("<HTML> <FONT SIZE = 4><FONT COLOR = red>" + var3[3] + "</FONT></FONT>")).addActionListener(new GameTreeReader(this, var2));
      this.add(var2);
      (var2 = new JCheckBox("<HTML> <FONT SIZE = 4><FONT COLOR = green>" + var3[0] + "</FONT></FONT>")).addActionListener(new SolverConfigWriter(this, var2));
      this.add(var2);
      (var2 = new JCheckBox("<HTML> <FONT SIZE = 4><FONT COLOR = black>" + var3[1] + "</FONT></FONT>")).addActionListener(new SolverConfigReader(this, var2));
      this.add(var2);
      JCheckBox var4;
      (var4 = new JCheckBox("<HTML> <FONT SIZE = 4><FONT COLOR = blue>" + var3[2] + "</FONT></FONT>")).addActionListener(new ResultsWriter(this, var4));
      this.add(var4);
   }

   public final void a() {
      if (this.a.size() == 0) {
         this.b.a();
      } else if (this.a.size() == 1) {
         this.b.c();
         this.b.a((String)this.a.get(0));
      } else {
         if (this.a.size() == 2) {
            this.b.c();
            this.b.a((String)this.a.get(0), (String)this.a.get(1));
         }

      }
   }

   // $FF: synthetic method
   static void a(GameTreeWriter var0, String var1) {
      var1 = var1;
      var0 = var0;

      for(int var2 = 0; var2 < var0.a.size(); ++var2) {
         if (var1.equals(var0.a.get(var2))) {
            var0.a.remove(var2);
            return;
         }
      }

   }
}
