package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TObjectDoubleProcedure;

class TObjectDoubleHashMap$2 implements TObjectDoubleProcedure {
   private boolean first;
   // $FF: synthetic field
   final TObjectDoubleHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TObjectDoubleHashMap$2(TObjectDoubleHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(Object var1, double var2) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(",");
      }

      this.val$buf.append(var1).append("=").append(var2);
      return true;
   }
}
