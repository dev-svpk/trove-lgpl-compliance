package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TObjectCharProcedure;

class TObjectCharHashMap$2 implements TObjectCharProcedure {
   private boolean first;
   // $FF: synthetic field
   final TObjectCharHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TObjectCharHashMap$2(TObjectCharHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(Object var1, char var2) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(",");
      }

      this.val$buf.append(var1).append("=").append(var2);
      return true;
   }
}
