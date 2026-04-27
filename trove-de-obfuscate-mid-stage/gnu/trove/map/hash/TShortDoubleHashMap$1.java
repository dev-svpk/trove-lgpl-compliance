package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TShortDoubleProcedure;

class TShortDoubleHashMap$1 implements TShortDoubleProcedure {
   private boolean first;
   // $FF: synthetic field
   final TShortDoubleHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TShortDoubleHashMap$1(TShortDoubleHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(short var1, double var2) {
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
