package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TShortIntProcedure;

class TShortIntHashMap$1 implements TShortIntProcedure {
   private boolean first;
   // $FF: synthetic field
   final TShortIntHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TShortIntHashMap$1(TShortIntHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(short var1, int var2) {
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
