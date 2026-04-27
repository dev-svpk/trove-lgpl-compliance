package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TDoubleShortProcedure;

class TDoubleShortHashMap$1 implements TDoubleShortProcedure {
   private boolean first;
   // $FF: synthetic field
   final TDoubleShortHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TDoubleShortHashMap$1(TDoubleShortHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(double var1, short var3) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var3);
      return true;
   }
}
