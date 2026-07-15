package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TShortShortProcedure;

class TShortShortHashMap$1 implements TShortShortProcedure {
   private boolean first;
   // $FF: synthetic field
   final TShortShortHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TShortShortHashMap$1(TShortShortHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(short var1, short var2) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var2);
      return true;
   }
}
